package com.ddalggak.finalproject.domain.label.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.label.dto.LabelMapper;
import com.ddalggak.finalproject.domain.label.dto.LabelRequestDto;
import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.label.dto.LabelUserRequestDto;
import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.label.repository.LabelRepository;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.task.repository.TaskRepository;
import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
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
public class LabelService {

	private final LabelMapper labelMapper;
	private final UserMapper userMapper;
	private final TicketMapper ticketMapper;
	private final TaskRepository taskRepository;
	private final LabelRepository labelRepository;
	private final UserRepository userRepository;
	private final TicketRepository ticketRepository;

	//라벨 생성
	@Transactional
	public ResponseEntity<?> createLabel(User user, LabelRequestDto labelRequestDto) {
		User existUser = userRepository.findByEmail(user.getEmail())
			.orElseThrow(() -> new CustomException(UNAUTHENTICATED_USER));
		// 유효성 검증
		Task task = validateTask(labelRequestDto.getTaskId());
		validateExistMember(task, TaskUser.create(task, existUser));
		if (!(task.getTaskLeader().equals(existUser.getEmail()) || task.getLabelLeadersList()
			.contains(existUser.getEmail()))) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		for (Label label : task.getLabelList()) {
			if (label.getLabelTitle().equals(labelRequestDto.getLabelTitle()))
				throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
		}
		// label create 작업
		LabelUserRequestDto labelUserRequestDto = LabelUserRequestDto.create(existUser);
		LabelUser labelUser = LabelUser.create(labelUserRequestDto);
		Label label = Label.create(labelRequestDto, labelUser, task);
		// label 저장
		labelRepository.save(label);
		// return label list
		List<LabelResponseDto> labelList = labelRepository
			.findByTaskId(task.getTaskId())
			.stream()
			.map(labelMapper::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(labelList);
	}

	//라벨 삭제
	@Transactional
	public ResponseEntity<?> deleteLabel(User user, Long labelId) {
		// 유효성 검증
		Task task = taskRepository.findTaskByLabelId(labelId).orElseThrow(() -> new CustomException(TASK_NOT_FOUND));
		Label label = validateLabel(labelId);
		// 서비스 로직
		if (task.getTaskLeader().equals(user.getEmail()) || label.getLabelLeader().equals(user.getEmail())) {
			labelRepository.delete(label);
		} else {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		// return label list
		List<LabelResponseDto> labelList = labelRepository
			.findByTaskId(task.getTaskId())
			.stream()
			.map(labelMapper::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(labelList);
	}

	//라벨 초대
	@Transactional
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
		List<UserResponseDto> userList = userRepository
			.getUserFromLabelId(labelId)
			.stream()
			.map(userMapper::toUserResponseDtoWithLabel)
			.collect(Collectors.toList());
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
		// label에 있는 user 검증
		List<UserResponseDto> userList = userRepository
			.getUserFromLabelId(labelId)
			.stream()
			.map(userMapper::toUserResponseDtoWithLabel)
			.collect(Collectors.toList());
		return ResponseEntity.ok(userList);
	}

	// 라벨당 존재하는 티켓 리스트 조회
	@Transactional(readOnly = true)
	public ResponseEntity<?> getTicketListByLabel(User user, Long labelId) {
		// label 검증
		Label label = validateLabel(labelId);
		//project 내에 존재하는 유저들은 label별 티켓 확인 가능
		Project project = label.getTask().getProject();
		validateExistMember(project, ProjectUser.create(project, user));
		// label에 속한 티켓들을 가져옴
		List<Ticket> ticketList = ticketRepository.findWithLabelId(labelId);
		Map<TicketStatus, List<TicketResponseDto>> tickets = ticketMapper.toDtoMapWithStatus(ticketList);
		return ResponseEntity.ok(tickets);
	}

	private Task validateTask(Long id) { //todo AOP 적용
		return taskRepository.findById(id).orElseThrow(
			() -> new CustomException(TASK_NOT_FOUND)
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

	private void validateExistMember(Project project, ProjectUser projectUser) {
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