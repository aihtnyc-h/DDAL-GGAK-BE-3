package com.ddalggak.finalproject.domain.task.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.task.dto.TaskBriefResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskRequestDto;
import com.ddalggak.finalproject.domain.task.dto.TaskResponseDto;
import com.ddalggak.finalproject.domain.task.service.TaskService;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Task Controller", description = "Task 관련 API입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {
	private final TaskService taskService;

	@Operation(summary = "Task 생성", description = "api for creating task")
	@PostMapping("/task")
	public ResponseEntity<List<TaskBriefResponseDto>> createTask(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody TaskRequestDto taskRequestDto) {
		return taskService.createTask(userDetails.getUser(), taskRequestDto);
	}

	@Operation(summary = "Task 조회", description = "api for view one task")
	@GetMapping("/task/{taskId}")
	public ResponseEntity<TaskResponseDto> viewTask(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam Long projectId,
		@PathVariable Long taskId) {
		return taskService.viewTask(userDetails.getUser(), projectId, taskId);
	}

	@Operation(summary = "Task 삭제", description = "api for delete one task")
	@DeleteMapping("/task/{taskId}")
	public ResponseEntity<List<TaskBriefResponseDto>> deleteTask(
		@AuthenticationPrincipal UserDetailsImpl user,
		@PathVariable Long taskId) {
		return taskService.deleteTask(user.getUser(), taskId);
	}

	@Operation(summary = "Task 리더 부여", description = "api for assign admin to task")
	@PostMapping("/task/{taskId}/leader")
	public ResponseEntity<List<UserResponseDto>> assignLeader(
		@AuthenticationPrincipal UserDetailsImpl user,
		@PathVariable Long taskId,
		@Valid @RequestBody TaskRequestDto taskRequestDto) {
		return taskService.assignLeader(user.getUser(), taskRequestDto, taskId);
	}

	@Operation(summary = "Task 초대", description = "api for invite user to task")
	@PostMapping("/task/{taskId}/invite")
	public ResponseEntity<List<UserResponseDto>> inviteTask(
		@AuthenticationPrincipal UserDetailsImpl user,
		@Valid @RequestBody TaskRequestDto taskRequestDto,
		@PathVariable Long taskId) {
		return taskService.inviteTask(user.getUser(), taskRequestDto, taskId);
	}

}
