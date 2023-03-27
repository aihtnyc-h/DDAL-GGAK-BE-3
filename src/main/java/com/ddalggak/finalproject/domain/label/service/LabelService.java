package com.ddalggak.finalproject.domain.label.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.label.dto.LabelRequestDto;
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

	private final UserRepository userRepository;

	private final LabelRepository labelRepository;

	@Transactional
	public ResponseEntity<SuccessResponseDto> createLabel(User user, LabelRequestDto labelRequestDto) {
		Task task = validateTask(labelRequestDto.getTaskId());
		validateExistMember(task, TaskUser.create(task, user));
		if (!(task.getTaskLeader().equals(user.getEmail()) || task.getLabelLeadersList().contains(user.getEmail()))) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
		LabelUserRequestDto labelUserRequestDto = LabelUserRequestDto.create(user);
		LabelUser labelUser = LabelUser.create(labelUserRequestDto);
		Label label = Label.create(labelRequestDto, labelUser, task);
		labelRepository.save(label);
		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
	}

	@Transactional
	public ResponseEntity<SuccessResponseDto> deleteLabel(User user, Long taskId, Long labelId) {
		Task task = validateTask(taskId);
		Label label = validateLabel(labelId);
		if (!(task.getTaskLeader().equals(user.getEmail()) || label.getLabelLeader().equals(user.getEmail()))) {
			labelRepository.delete(label);
		} else {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
		return SuccessResponseDto.toResponseEntity(SuccessCode.DELETED_SUCCESSFULLY);
	}

	private Task validateTask(Long id) { //todo AOP 적용
		return taskRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.TASK_NOT_FOUND)
		);
	}

	private Label validateLabel(Long id) { // todo LabelRepository 삭제하고 연관관계만으로 긁어오게 만들기
		return labelRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.LABEL_NOT_FOUND)
		);
	}

	private void validateExistMember(Task task, TaskUser taskUser) {
		if (!task.getTaskUserList().contains(taskUser)) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
	}
}
