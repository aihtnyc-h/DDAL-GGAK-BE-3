package com.ddalggak.finalproject.domain.task.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;
import static org.springframework.http.ResponseEntity.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.label.dto.LabelMapper;
import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.label.repository.LabelRepository;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.project.repository.ProjectRepository;
import com.ddalggak.finalproject.domain.task.dto.TaskBriefResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskMapper;
import com.ddalggak.finalproject.domain.task.dto.TaskRequestDto;
import com.ddalggak.finalproject.domain.task.dto.TaskResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskReviewDto;
import com.ddalggak.finalproject.domain.task.dto.TaskUserRequestDto;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.task.repository.TaskRepository;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
import com.ddalggak.finalproject.domain.user.dto.EmailRequestDto;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.error.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskMapper taskMapper;
	private final UserMapper userMapper;
	private final LabelMapper labelMapper;
	private final TicketMapper ticketMapper;
	private final TaskRepository taskRepository;
	private final TicketRepository ticketRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final LabelRepository labelRepository;

	// 태스크 생성
	@Transactional
	public ResponseEntity<List<TaskBriefResponseDto>> createTask(User user, TaskRequestDto taskRequestDto) {
		User existUser = userRepository.findByEmail(user.getEmail())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		// 1.유효성 검증
		Project project = validateProject(taskRequestDto.projectId);
		validateExistMember(project, ProjectUser.create(project, existUser));
		if (!(project.getProjectLeader().equals(existUser.getEmail())) || project.getTaskLeadersList()
			.contains(existUser.getEmail())) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 2. 태스크 생성
		TaskUserRequestDto taskUserRequestDto = TaskUserRequestDto.create(existUser);
		TaskUser taskUser = TaskUser.create(taskUserRequestDto);
		Task task = Task.create(taskRequestDto, taskUser, project);

		// 3. 태스크 저장
		taskRepository.save(task);

		// 4. 리턴
		List<TaskBriefResponseDto> result = taskRepository.findTaskByProject(taskRequestDto.projectId)
			.stream()
			.map(taskMapper::toBriefDto)
			.collect(Collectors.toList());
		return ok(result);
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
		return ok(taskResponseDto);
	}

	// 태스크 삭제
	@Transactional
	public ResponseEntity<List<TaskBriefResponseDto>> deleteTask(User user, Long taskId) {
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
		return ok(result);
	}

	/*
	 * 프로젝트 리더이거나 taskLeader인 경우만 가능,
	 * 혹시 다른 taskLeader는 초대못할경우 task.getTaskLeader().equals(user.getEmail())로 검증
	 */
	// 태스크 내부로 유저 초대
	@Transactional
	public ResponseEntity<List<UserResponseDto>> inviteTask(User user, TaskRequestDto taskRequestDto, Long taskId) {
		//유효성 검증
		Task task = validateTask(taskId);
		Project project = validateProject(taskRequestDto.getProjectId());
		User invited = validateUserByEmail(taskRequestDto.getEmail());
		TaskUser taskUser = TaskUser.create(task, invited);
		// 유효성 검증 : task에 이미 있는 멤버면 안됨, 프로젝트 리더이거나 taskLeader(이 태스크 아니어도 됨)인 경우만 초대가능
		if (task.getTaskUserList().contains(taskUser)) {
			throw new CustomException(DUPLICATE_MEMBER);
		} else if (project.getTaskList().stream().noneMatch(
			t -> t.getTaskId().equals(taskId)
		)) { // task가 project안에 있는지 확인해야함
			throw new CustomException(INVALID_REQUEST);
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
		return ok(result);
	}

	// 태스크 리더 지정
	@Transactional
	public ResponseEntity<List<UserResponseDto>> assignLeader(User user, EmailRequestDto emailRequestDto,
		Long taskId) {
		//유효성 검증
		User userToLeader = validateUserByEmail(emailRequestDto.getEmail());
		Task task = validateTask(taskId);
		validateExistMember(task, TaskUser.create(task, userToLeader));
		// 권한 검증 : 프로젝트 리더만 task Leader 지정 가능, 아니면 해당 프로젝트의 task Leader만 위임 가능
		if (task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getTaskLeader().equals(user.getEmail())) {
			task.setTaskLeader(emailRequestDto.getEmail());
		} else {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
		// 결과 리턴
		List<UserResponseDto> result = userRepository.getTaskUserFromTaskId(taskId)
			.stream()
			.map(userMapper::toUserResponseDtoWithTaskUser)
			.collect(Collectors.toList());
		return ok(result);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<List<LabelResponseDto>> viewLabels(User user, Long taskId) {
		// 유효성 검증
		Task task = validateTask(taskId);
		validateExistMember(task.getProject(), ProjectUser.create(task.getProject(), user));
		// 결과 리턴
		List<LabelResponseDto> result = labelRepository.findByTaskId(taskId)
			.stream()
			.map(labelMapper::toDto)
			.collect(Collectors.toList());
		return ok(result);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> viewTickets(User user, Long taskId) {
		//유효성 검증
		Task task = validateTask(taskId);
		validateExistMember(task.getProject(), ProjectUser.create(task.getProject(), user));
		// 결과 리턴
		List<Ticket> ticketList = ticketRepository.findWithTaskId(task.getTaskId());
		Map<TicketStatus, List<TicketResponseDto>> ListWithTicketStatus = ticketMapper.toDtoMapWithStatus(ticketList);
		return ok(ListWithTicketStatus);
	}

	public ResponseEntity<TaskReviewDto> viewReviewTickets(User user, Long taskId) {
		// 유효성 검증
		Task task = validateTask(taskId);
		validateExistMember(task.getProject(), ProjectUser.create(task.getProject(), user));
		// 결과 리턴
		TaskReviewDto result = new TaskReviewDto();
		List<LabelResponseDto> labels = labelRepository.findByTaskId(taskId)
			.stream()
			.map(labelMapper::toDto)
			.collect(Collectors.toList());
		result.setLabelLeaders(labels);
		List<TicketResponseDto> ticketsInReview = ticketRepository.findByStatusAndTaskId(TicketStatus.REVIEW, taskId)
			.stream()
			.map(ticketMapper::toDto)
			.collect(Collectors.toList());
		result.setTickets(ticketsInReview);
		return ok(result);
	}

	private void validateExistMember(Task task, TaskUser taskUser) {
		if (!task.getTaskUserList().contains(taskUser)) {
			throw new CustomException(UNAUTHENTICATED_USER);
		}
	}

	private void validateExistMember(Project project, ProjectUser projectUser) {
		if (!project.getProjectUserList().contains(projectUser)) {
			throw new CustomException(EMPTY_CLIENT);
		}
	}

	private User validateUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(MEMBER_NOT_FOUND)
		);
	}

	private Project validateProject(Long id) {
		return projectRepository.findById(id).orElseThrow(
			() -> new CustomException(PROJECT_NOT_FOUND)
		);
	}

	private Task validateTask(Long id) {
		return taskRepository.findById(id).orElseThrow(
			() -> new CustomException(TASK_NOT_FOUND)
		);
	}
}
