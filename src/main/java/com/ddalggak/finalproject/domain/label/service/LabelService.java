package com.ddalggak.finalproject.domain.label.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.label.dto.LabelRequestDto;
import com.ddalggak.finalproject.domain.label.dto.LabelUserRequestDto;
import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.label.repository.LabelRepository;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.task.repository.TaskRepository;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
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
public class LabelService {
	private final TaskRepository taskRepository;

	private final LabelRepository labelRepository;

	private final UserRepository userRepository;

	private final TicketRepository ticketRepository;

	//라벨 생성
	@Transactional
	public ResponseEntity<?> createLabel(User user, LabelRequestDto labelRequestDto) {
		// 유효성 검증
		Task task = validateTask(labelRequestDto.getTaskId());
		validateExistMember(task, TaskUser.create(task, user));
		if (!(task.getTaskLeader().equals(user.getEmail()) || task.getLabelLeadersList().contains(user.getEmail()))) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		for (Label label : task.getLabelList()) {
			if (label.getLabelTitle().equals(labelRequestDto.getLabelTitle()))
				throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
		}
		// label create 작업
		LabelUserRequestDto labelUserRequestDto = LabelUserRequestDto.create(user);
		LabelUser labelUser = LabelUser.create(labelUserRequestDto);
		Label label = Label.create(labelRequestDto, labelUser, task);
		// label 저장
		labelRepository.save(label);
		// return label list
		return ResponseEntity.ok(labelRepository.findByTaskId(task.getTaskId()));
	}

	//라벨 삭제
	@Transactional
	public ResponseEntity<?> deleteLabel(User user, Long taskId, Long labelId) {
		// 유효성 검증
		Task task = validateTask(taskId);
		Label label = validateLabel(labelId);
		if (!(task.getTaskLeader().equals(user.getEmail()) || label.getLabelLeader().equals(user.getEmail()))) {
			labelRepository.delete(label);
		} else {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}

		// return label list
		return ResponseEntity.ok(labelRepository.findByTaskId(task.getTaskId()));
	}

	@Transactional // todo 검증필요
	public ResponseEntity<?> inviteLabel(User user, LabelRequestDto labelRequestDto, Long labelId) {
		Task task = validateTask(labelRequestDto.getTaskId());
		Label label = validateLabel(labelId);
		// 1. user의 유효성 검증 -> task, label, project Leader가 아닐 경우 403 FORBIDDEN
		validateLeader(task, user);
		// 2. labelUser의 유효성 검증 -> 이미 초대된 유저일 경우
		for (LabelUser labelUser : label.getLabelUserList()) {
			if (labelUser.getUser().getEmail().equals(labelRequestDto.getEmail())) {
				throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
			}
		}
		// 3. 정상 생성 후 들어가기
		User inviteUser = validateUser(labelRequestDto.getEmail());
		LabelUserRequestDto labelUserRequestDto = LabelUserRequestDto.create(inviteUser);
		LabelUser labelUser = LabelUser.create(labelUserRequestDto);
		label.addLabelUser(labelUser);
		//4. 라벨의 유저 업데이트
		List<UserResponseDto> userList = userRepository.getUserFromLabel(labelId);
		return ResponseEntity.ok(userList);
	}

	// label leader 위임
	@Transactional
	public ResponseEntity<?> assignLeader(User user, LabelRequestDto labelRequestDto, Long labelId) {
		// task 검증
		Task task = validateTask(labelRequestDto.getTaskId());
		// label 검증
		Label label = validateLabel(labelId);
		// user의 권한 검증
		validateLeader(task, user);
		// 초대하려는 유저가 라벨에 있어야 labelLeader를 줄 수 있음, 이미 라벨 리더인사람 또 라벨 리더 주면 null처리 해버림
		if (label.validateUserWithEmail(labelRequestDto.getEmail())) {
			label.setLabelLeader(labelRequestDto.getEmail());
		} else {
			throw new CustomException(ErrorCode.EMPTY_CLIENT);
		}
		List<UserResponseDto> userList = userRepository.getUserFromLabel(labelId);
		return ResponseEntity.ok(userList);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> getTicketListByLabel(User user, Long labelId) {
		Label label = validateLabel(labelId);
		//project 내에 존재하는 유저들은 label별 티켓 확인 가능
		Project project = label.getTask().getProject();
		valideExistMember(project, ProjectUser.create(project, user));
		Map<TicketStatus, List<TicketResponseDto>> tickets = ticketRepository.findWithLabelId(labelId);
		return ResponseEntity.ok(tickets);
	}

	private Task validateTask(Long id) { //todo AOP 적용
		return taskRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.TASK_NOT_FOUND)
		);
	}

	private Label validateLabel(Long id) {
		return labelRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.LABEL_NOT_FOUND)
		);
	}

	private User validateUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
		);
	}

	private void validateExistMember(Task task, TaskUser taskUser) {
		if (!task.getTaskUserList().contains(taskUser)) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}

	private void valideExistMember(Project project, ProjectUser projectUser) {
		if (!project.getProjectUserList().contains(projectUser)) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}

	private void validateLeader(Task task, User user) {
		if (!(task.getTaskLeader().equals(user.getEmail()) ||
			task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getLabelLeadersList().contains(user.getEmail()))) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}
}
