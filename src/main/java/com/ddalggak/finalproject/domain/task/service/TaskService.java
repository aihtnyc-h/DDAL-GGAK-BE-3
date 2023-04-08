package com.ddalggak.finalproject.domain.task.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.project.repository.ProjectRepository;
import com.ddalggak.finalproject.domain.task.dto.TaskBriefResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskMapper;
import com.ddalggak.finalproject.domain.task.dto.TaskRequestDto;
import com.ddalggak.finalproject.domain.task.dto.TaskResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskUserRequestDto;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.task.repository.TaskRepository;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskMapper taskMapper;
	private final UserMapper userMapper;
	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;

	// 태스크 생성
	@Transactional
	public ResponseEntity<?> createTask(User user, TaskRequestDto taskRequestDto) {
		// 1.유효성 검증
		Project project = validateProject(taskRequestDto.projectId);
		validateExistMember(project, ProjectUser.create(project, user));
		if (!(project.getProjectLeader().equals(user.getEmail())) || project.getTaskLeadersList()
			.contains(user.getEmail())) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 2. 태스크 생성
		TaskUserRequestDto taskUserRequestDto = TaskUserRequestDto.create(user);
		TaskUser taskUser = TaskUser.create(taskUserRequestDto);
		Task task = Task.create(taskRequestDto, taskUser, project);

		// 3. 태스크 저장
		taskRepository.save(task);

		// 4. 리턴
		List<TaskBriefResponseDto> result = taskRepository.findTaskByProject(taskRequestDto.projectId)
			.stream()
			.map(taskMapper::toBriefDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	// 태스크 조회
	@Transactional(readOnly = true) // project member면 누구나 task 조회 가능하다. task 멤버가 아닐지라도.
	public ResponseEntity<TaskResponseDto> viewTask(User user, Long projectId, Long taskId) {
		// 유효성 검증
		Project project = validateProject(projectId);
		validateExistMember(project, ProjectUser.create(project, user));

		// 태스크 조회
		Task task = taskRepository.findTaskById(taskId).orElseThrow(
			() -> new CustomException(TASK_NOT_FOUND)
		);

		// 리턴
		TaskResponseDto taskResponseDto = taskMapper.toDto(task);
		return ResponseEntity.ok(taskResponseDto);
	}

	// 태스크 삭제
	@Transactional
	public ResponseEntity<?> deleteTask(User user, Long taskId) {
		// 유효성 검증
		Task task = validateTask(taskId);
		if (task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getTaskLeader().equals(user.getEmail())) {
			// 삭제
			taskRepository.delete(task);
		} else {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 프로젝트의 남은 태스크 조회
		List<TaskBriefResponseDto> result = taskRepository.findTaskByProject(task.getProject().getProjectId())
			.stream()
			.map(taskMapper::toBriefDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	/*
	 * 프로젝트 리더이거나 taskLeader인 경우만 가능,
	 * 혹시 다른 taskLeader는 초대못할경우 task.getTaskLeader().equals(user.getEmail())로 검증
	 */
	// 태스크 내부로 유저 초대
	@Transactional
	public ResponseEntity<?> inviteTask(User user, TaskRequestDto taskRequestDto, Long taskId) {
		//유효성 검증
		Task task = validateTask(taskId);
		Project project = validateProject(taskRequestDto.getProjectId());
		User invited = validateUserByEmail(taskRequestDto.getEmail());
		TaskUser taskUser = TaskUser.create(task, invited);
		// 유효성 검증 : task에 이미 있는 멤버면 안됨, 프로젝트 리더이거나 taskLeader(이 태스크 아니어도 됨)인 경우만 초대가능
		if (task.getTaskUserList().contains(taskUser)) {
			throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
		} else if (project.getProjectLeader().equals(user.getEmail()) ||
			project.getTaskLeadersList().stream().anyMatch(taskLeader -> taskLeader.equals(user.getEmail()))) {
			validateExistMember(project, ProjectUser.create(project, invited)); // user가 프로젝트에 있는 유저인지 검증
			task.addTaskUser(taskUser); // task에 넣기
			taskRepository.save(task);
		} else {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 태스크에 있는 유저 리스트 리턴
		List<UserResponseDto> result = userRepository.getTaskUserFromTaskId(taskId)
			.stream()
			.map(userMapper::toUserResponseDtoWithTaskUser)
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	/*
	 * 지금은 projectId가 필요하지 않지만 만약 taskId가 아닌 taskName을 url로 지정하게 될 경우 projectId가 필요할 수 있음.
	 */
	// 태스크 리더 지정
	@Transactional
	public ResponseEntity<?> assignLeader(User user, TaskRequestDto taskRequestDto, Long taskId) {
		//유효성 검증
		User userToLeader = validateUserByEmail(taskRequestDto.getEmail());
		Task task = validateTask(taskId);
		validateExistMember(task, TaskUser.create(task, userToLeader));
		// 권한 검증 : 프로젝트 리더만 task Leader 지정 가능, 아니면 해당 프로젝트의 task Leader만 위임 가능
		if (task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getTaskLeader().equals(user.getEmail())) {
			task.setTaskLeader(taskRequestDto.getEmail());
		} else {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 결과 리턴
		List<UserResponseDto> result = userRepository.getTaskUserFromTaskId(taskId)
			.stream()
			.map(userMapper::toUserResponseDtoWithTaskUser)
			.collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}

	private void validateExistMember(Task task, TaskUser taskUser) {
		if (!task.getTaskUserList().contains(taskUser)) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
	}

	private void validateExistMember(Project project, ProjectUser projectUser) {
		if (!project.getProjectUserList().contains(projectUser)) {
			throw new CustomException(ErrorCode.EMPTY_CLIENT);
		}
	}

	private User validateUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
		);
	}

	private Project validateProject(Long id) {
		return projectRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
		);
	}

	private Task validateTask(Long id) {
		return taskRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.TASK_NOT_FOUND)
		);
	}

}
