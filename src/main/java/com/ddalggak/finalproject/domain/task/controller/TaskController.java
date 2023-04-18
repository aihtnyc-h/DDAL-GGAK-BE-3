package com.ddalggak.finalproject.domain.task.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskBriefResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskRequestDto;
import com.ddalggak.finalproject.domain.task.dto.TaskResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskReviewDto;
import com.ddalggak.finalproject.domain.task.service.TaskService;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.user.dto.EmailRequestDto;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.global.aop.ExecutionTimer;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;
import com.ddalggak.finalproject.global.validation.RequestId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Task Controller", description = "Task 관련 API입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class TaskController {
	private final TaskService taskService;

	@Operation(summary = "Task 생성", description = "api for creating task")
	@PostMapping("/task")
	@ExecutionTimer
	public ResponseEntity<List<TaskBriefResponseDto>> createTask(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody TaskRequestDto taskRequestDto) {
		return taskService.createTask(userDetails.getUser(), taskRequestDto);
	}

	@Operation(summary = "Task 조회", description = "api for view one task")
	@GetMapping("/task/{taskId}")
	@ExecutionTimer
	public ResponseEntity<TaskResponseDto> viewTask(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestId @RequestParam Long projectId,
		@Valid @RequestId @PathVariable Long taskId) {
		return taskService.viewTask(userDetails.getUser(), projectId, taskId);
	}

	@Operation(summary = "Task 삭제", description = "api for delete one task")
	@DeleteMapping("/task/{taskId}")
	@ExecutionTimer
	public ResponseEntity<List<TaskBriefResponseDto>> deleteTask(
		@AuthenticationPrincipal UserDetailsImpl user,
		@Valid @RequestId @PathVariable Long taskId) {
		return taskService.deleteTask(user.getUser(), taskId);
	}

	@Operation(summary = "Task 리더 부여", description = "api for assign admin to task")
	@PostMapping("/task/{taskId}/leader")
	@ExecutionTimer
	public ResponseEntity<List<UserResponseDto>> assignLeader(
		@AuthenticationPrincipal UserDetailsImpl user,
		@Valid @RequestId @PathVariable Long taskId,
		@RequestBody EmailRequestDto emailRequestDto) {
		return taskService.assignLeader(user.getUser(), emailRequestDto, taskId);
	}

	@Operation(summary = "Task 초대", description = "api for invite user to task")
	@PostMapping("/task/{taskId}/invite")
	@ExecutionTimer
	public ResponseEntity<List<UserResponseDto>> inviteTask(
		@AuthenticationPrincipal UserDetailsImpl user,
		@RequestBody TaskRequestDto taskRequestDto,
		@Valid @RequestId @PathVariable Long taskId) {
		return taskService.inviteTask(user.getUser(), taskRequestDto, taskId);
	}

	@Operation(summary = "Task의 label 전체조회", description = "api for view all labels of task")
	@GetMapping("/task/{taskId}/labels")
	@ExecutionTimer
	public ResponseEntity<List<LabelResponseDto>> viewLabels(
		@AuthenticationPrincipal UserDetailsImpl user,
		@Valid @RequestId @PathVariable Long taskId) {
		return taskService.viewLabels(user.getUser(), taskId);
	}

	@Operation(summary = "Task의 ticket 전체조회", description = "api for view all tickets of task")
	@GetMapping("/task/{taskId}/tickets")
	@ExecutionTimer
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> viewTickets(
		@AuthenticationPrincipal UserDetailsImpl user,
		@Valid @RequestId @PathVariable Long taskId) {
		return taskService.viewTickets(user.getUser(), taskId);
	}

	@Operation(summary = "task의 review 상태 티켓 조회", description = "api for view tickets of review status")
	@GetMapping("/task/{taskId}/tickets/review")
	@ExecutionTimer
	public ResponseEntity<TaskReviewDto> viewReviewTickets(
		@AuthenticationPrincipal UserDetailsImpl user,
		@Valid @RequestId @PathVariable Long taskId) {
		return taskService.viewReviewTickets(user.getUser(), taskId);
	}
}
