package com.ddalggak.finalproject.domain.label.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.label.dto.LabelRequestDto;
import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.label.dto.LabelUserRequestDto;
import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.label.repository.LabelRepository;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.task.repository.TaskRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
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

	@Transactional
	public ResponseEntity<LabelResponseDto> createLabel(User user, LabelRequestDto labelRequestDto) {
		Task task = validateTask(labelRequestDto.getTaskId());
		validateExistMember(task, TaskUser.create(task, user));
		if (!(task.getTaskLeader().equals(user.getEmail()) || task.getLabelLeadersList().contains(user.getEmail()))) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		for (Label label : task.getLabelList()) {
			if (label.getLabelTitle().equals(labelRequestDto.getLabelTitle()))
				throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
		}
		LabelUserRequestDto labelUserRequestDto = LabelUserRequestDto.create(user);
		LabelUser labelUser = LabelUser.create(labelUserRequestDto);
		Label label = Label.create(labelRequestDto, labelUser, task);
		labelRepository.save(label);
		return ResponseEntity.ok(LabelResponseDto.of(label));
	}

	@Transactional
	public ResponseEntity<SuccessResponseDto> deleteLabel(User user, Long taskId, Long labelId) {
		Task task = validateTask(taskId);
		Label label = validateLabel(labelId);
		if (!(task.getTaskLeader().equals(user.getEmail()) || label.getLabelLeader().equals(user.getEmail()))) {
			labelRepository.delete(label);
		} else {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
		return SuccessResponseDto.toResponseEntity(SuccessCode.DELETED_SUCCESSFULLY);
	}

	@Transactional
	public ResponseEntity<SuccessResponseDto> inviteLabel(User user, LabelRequestDto labelRequestDto, Long labelId) {
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
		return SuccessResponseDto.toResponseEntity(SuccessCode.JOINED_SUCCESSFULLY);
	}

	@Transactional
	public ResponseEntity<SuccessResponseDto> assignLeader(User user, LabelRequestDto labelRequestDto, Long labelId) {
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
		return SuccessResponseDto.toResponseEntity(SuccessCode.UPDATED_SUCCESSFULLY);
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

	private void validateLeader(Task task, User user) {
		if (!(task.getTaskLeader().equals(user.getEmail()) ||
			task.getProject().getProjectLeader().equals(user.getEmail()) ||
			task.getLabelLeadersList().contains(user.getEmail()))) {
			throw new CustomException(ErrorCode.UNAUTHENTICATED_USER);
		}
	}
}
