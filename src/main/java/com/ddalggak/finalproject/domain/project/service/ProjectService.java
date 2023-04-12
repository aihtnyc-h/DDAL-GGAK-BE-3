package com.ddalggak.finalproject.domain.project.service;

import static com.ddalggak.finalproject.global.dto.SuccessCode.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectMapper;
import com.ddalggak.finalproject.domain.project.dto.ProjectRequestDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectResponseDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectUserRequestDto;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.project.repository.ProjectRepository;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.GlobalResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.mail.MailService;
import com.ddalggak.finalproject.infra.aws.S3Uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectMapper projectMapper;
	private final UserMapper userMapper;
	private final ProjectRepository projectRepository;
	private final S3Uploader s3Uploader;
	private final UserRepository userRepository;
	private final MailService mailService;

	@Transactional
	public ResponseEntity<?> createProject(User user, MultipartFile image,
		ProjectRequestDto projectRequestDto) throws
		IOException {
		//1. user로 projectUserRequestDto 생성
		ProjectUserRequestDto projectUserRequestDto = ProjectUserRequestDto.create(user);
		//2. projectUserDto로 projectUser생성
		ProjectUser projectUser = ProjectUser.create(projectUserRequestDto);
		//2.5 image S3 서버에 업로드 -> 분기처리
		String imageUrl = null;
		if (!(image == null)) {
			fileCheck(image);
			imageUrl = s3Uploader.upload(image, "project");
		}
		projectRequestDto.setThumbnail(imageUrl);
		//3. projectUser로 project생성
		Project project = Project.create(projectRequestDto, projectUser);
		//4. projectRepository에 project 저장
		projectRepository.save(project);
		//5. projectResponseDto로 반환
		List<ProjectBriefResponseDto> result = projectRepository.findProjectAllByUserId(user.getUserId());
		return GlobalResponseDto.of(CREATED_SUCCESSFULLY, result);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> viewProjectAll(User user) {
		List<ProjectBriefResponseDto> result = projectRepository.findProjectAllByUserId(user.getUserId());
		return GlobalResponseDto.of(SUCCESS_SEND, result);
	}

	//프로젝트 확인
	@Transactional(readOnly = true)
	public ResponseEntity<?> viewProject(User user, Long id) {
		// 유효성 검증
		Project project = validateProject(id);
		validateExistMember(project, ProjectUser.create(project, user));

		// 리턴
		ProjectResponseDto projectResponseDto = projectMapper.toDto(project);

		return GlobalResponseDto.of(SUCCESS_SEND, projectResponseDto);
	}

	@Transactional
	public ResponseEntity<?> deleteProject(User user, Long projectId) {
		Project project = validateProject(projectId);
		if (project.getProjectLeader().equals(user.getEmail())) {
			if (project.getThumbnail() != null) {
				s3Uploader.delete(project.getThumbnail());
			}
			projectRepository.delete(project);
			List<ProjectBriefResponseDto> result = projectRepository.findProjectAllByUserId(
				user.getUserId());
			return GlobalResponseDto.of(DELETED_SUCCESSFULLY, result);
		} else {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}

	@Transactional
	public ResponseEntity<?> joinProject(User user, Long projectId) {
		Project project = validateProject(projectId);
		ProjectUser projectUser = ProjectUser.create(project, user);
		validateDuplicateMember(project, projectUser);
		project.addProjectUser(projectUser);
		List<ProjectBriefResponseDto> result = projectRepository.findProjectAllByUserId(
			user.getUserId());
		return GlobalResponseDto.of(DELETED_SUCCESSFULLY, result);
	}

	// 프로젝트 정보 변경
	@Transactional
	public ResponseEntity<?> updateProject(User user, Long projectId,
		MultipartFile image, ProjectRequestDto projectRequestDto) throws IOException {
		// 유효성 검사
		Project project = validateProject(projectId);
		if (!project.getProjectLeader().equals(user.getEmail())) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		// 기존 이미지 삭제 후 새로운 이미지 업로드
		String imageUrl = project.getThumbnail() == null ? null : project.getThumbnail();
		if (!(image == null)) {
			fileCheck(image);
			imageUrl = s3Uploader.upload(image, "project");
		}
		// 업로드한 이미지의 url을 바탕으로 update 쿼리
		projectRequestDto.setThumbnail(imageUrl);
		project.update(projectRequestDto);

		// 새로운 프로젝트 다시 받아옴 , todo 무엇을 반환해야 할까?
		ProjectResponseDto projectResponseDto = projectMapper.toDto(projectRepository.findById(projectId).get());
		return GlobalResponseDto.of(UPDATED_SUCCESSFULLY, projectResponseDto);
	}

	public ResponseEntity<?> deleteProjectUser(User user, Long projectId, Long userId) {
		// 유효성 검사
		Project project = validateProject(projectId);
		User projectUser = userRepository.findById(userId).orElseThrow(
			() -> new UserException(ErrorCode.EMPTY_CLIENT)
		);
		if (!project.getProjectLeader().equals(user.getEmail())) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		project.getProjectUserList().remove(ProjectUser.create(project, projectUser));
		// 참여중인 유저 목록 리턴
		List<UserResponseDto> userList = userRepository
			.getProjectUserFromProjectId(projectId)
			.stream()
			.map(userMapper::toUserResponseDtoWithProjectUser)
			.collect(Collectors.toList());
		return GlobalResponseDto.of(DELETED_SUCCESSFULLY, userList);
	}

	// 프로젝트에 참여중인 유저 목록 조회
	@Transactional(readOnly = true)
	public ResponseEntity<?> viewProjectUsers(User user, Long projectId) {
		// 유효성 검증
		Project project = validateProject(projectId);
		validateExistMember(project, ProjectUser.create(project, user));

		// 참여중인 유저 목록 리턴
		List<UserResponseDto> userList = userRepository
			.getProjectUserFromProjectId(projectId)
			.stream()
			.map(userMapper::toUserResponseDtoWithProjectUser)
			.collect(Collectors.toList());
		return GlobalResponseDto.of(HttpStatus.OK, userList);
	}

	public ResponseEntity<?> inviteProjectUser(User user, Long projectId, List<String> emails) {
		// 유효성 검증
		Project project = validateProject(projectId);
		validateExistMember(project, ProjectUser.create(project, user));

		// email 유효성 검증
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
		for (String email : emails) {
			if (!pattern.matcher(email).matches()) {
				return ResponseEntity.badRequest().body("Invalid email format: " + email);
			}
		}

		if (project.getUuid() == null) {
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			project.createUuid(uuid);
		}
		String uuid = project.getUuid();

		// 참여중인 유저 목록 리턴
		List<UserResponseDto> userList = userRepository
			.getProjectUserFromProjectId(projectId)
			.stream()
			.map(userMapper::toUserResponseDtoWithProjectUser)
			.collect(Collectors.toList());

		Map<String, Object> response = mailService.sendProjectCode(uuid, emails);
		response.put("userResponseDtoList", userList);

		return GlobalResponseDto.of(HttpStatus.OK, response);
	}

	private void validateDuplicateMember(Project project, ProjectUser projectUser) {
		if (project.getProjectUserList().contains(projectUser)) {
			throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
		}
	}

	private void validateExistMember(Project project, ProjectUser projectUser) {
		if (!project.getProjectUserList().contains(projectUser)) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}

	private Project validateProject(Long id) { // todo 로직 만들어서 participant가 3명 이상이면 3명까지만 출력하도록 method 작성
		return projectRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
		);
	}

	private void fileCheck(MultipartFile file) {
		String fileName = StringUtils.getFilenameExtension(file.getOriginalFilename());
		if (fileName != null) {
			String exe = fileName.toLowerCase();
			if (!(exe.equals("jpg") || exe.equals("png") || exe.equals("jpeg") || exe.equals("gif") || exe.equals(
				"webp"))) {
				throw new CustomException(ErrorCode.TYPE_MISMATCH);
			}
		}
	}
}

