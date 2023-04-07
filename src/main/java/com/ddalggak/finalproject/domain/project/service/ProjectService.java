package com.ddalggak.finalproject.domain.project.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectRequestDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectResponseDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectUserRequestDto;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.project.repository.ProjectRepository;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.infra.aws.S3Uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
	private final ProjectRepository projectRepository;
	private final S3Uploader s3Uploader;
	private final UserRepository userRepository;

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
		//4. projectLeader 주입
		project.setProjectLeader(user.getEmail());
		//5. projectRepository에 project 저장
		projectRepository.save(project);
		return ResponseEntity.ok(projectRepository.findProjectAllByUserId(user.getUserId()));
	}

	@Transactional(readOnly = true)
	public ResponseEntity<List<ProjectBriefResponseDto>> viewProjectAll(User user) {
		List<ProjectBriefResponseDto> result = projectRepository.findProjectAllByUserId(user.getUserId());
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(result);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<ProjectResponseDto> viewProject(User user, Long id) {
		Project project = validateProject(id);
		validateExistMember(project, ProjectUser.create(project, user));

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(ProjectResponseDto.of(project));
	}

	@Transactional
	public ResponseEntity<SuccessResponseDto> deleteProject(User user, Long projectId) {
		Project project = validateProject(projectId);
		if (project.getProjectLeader().equals(user.getEmail())) {
			if (project.getThumbnail() != null) {
				s3Uploader.delete(project.getThumbnail());
			}
			projectRepository.delete(project);
			return SuccessResponseDto.toResponseEntity(SuccessCode.DELETED_SUCCESSFULLY);
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
		return SuccessResponseDto.toResponseEntity(SuccessCode.JOINED_SUCCESSFULLY);
	}

	@Transactional
	public ResponseEntity<ProjectBriefResponseDto> updateProject(User user, Long projectId,
		MultipartFile image, ProjectRequestDto projectRequestDto) throws IOException {
		Project project = validateProject(projectId);
		if (!project.getProjectLeader().equals(user.getEmail())) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		String imageUrl = null;
		if (!(image == null)) {
			fileCheck(image);
			imageUrl = s3Uploader.upload(image, "project");
		}
		projectRequestDto.setThumbnail(imageUrl);
		projectRepository.update(projectId, projectRequestDto);
		return ResponseEntity.ok(new ProjectBriefResponseDto(project));
	}

	public ResponseEntity<?> deleteProjectUser(User user, Long projectId, Long userId) {
		Project project = validateProject(projectId);
		User projectUser = userRepository.findById(userId).orElseThrow(
			() -> new UserException(ErrorCode.EMPTY_CLIENT)
		);
		if (!project.getProjectLeader().equals(user.getEmail())) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		project.getProjectUserList().remove(ProjectUser.create(project, projectUser));
		return SuccessResponseDto.toResponseEntity(SuccessCode.DELETED_SUCCESSFULLY);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> viewProjectUsers(User user, Long projectId) {
		Project project = validateProject(projectId);
		validateExistMember(project, ProjectUser.create(project, user));
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(project.getProjectUserList().stream().map(UserResponseDto::of).collect(Collectors.toList()));
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

