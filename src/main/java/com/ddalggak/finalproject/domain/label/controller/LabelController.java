package com.ddalggak.finalproject.domain.label.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.label.dto.LabelRequestDto;
import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.label.service.LabelService;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.global.aop.ExecutionTimer;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Label Controller", description = "Label 관련 API입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {
	private final LabelService labelService;

	@Operation(summary = "Label 생성", description = "api for creating label")
	@PostMapping("/label")
	@ExecutionTimer
	public ResponseEntity<List<LabelResponseDto>> createLabel(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody LabelRequestDto labelRequestDto) {
		return labelService.createLabel(userDetails.getUser(), labelRequestDto);
	}

	@Operation(summary = "Label 삭제", description = "api for delete label")
	@DeleteMapping("/label/{labelId}")
	@ExecutionTimer
	public ResponseEntity<List<LabelResponseDto>> deleteLabel(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long labelId) {
		return labelService.deleteLabel(userDetails.getUser(), labelId);
	}

	@Operation(summary = "Label 강제입성", description = "api for invite label")
	@PostMapping("/label/{labelId}/invite")
	@ExecutionTimer
	public ResponseEntity<List<UserResponseDto>> inviteLabel(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody LabelRequestDto labelRequestDto,
		@PathVariable Long labelId) {
		return labelService.inviteLabel(userDetails.getUser(), labelRequestDto, labelId);
	}

	@Operation(summary = "Label 리더 부여", description = "api for assign admin to label")
	@PostMapping("/label/{labelId}/leader")
	@ExecutionTimer
	public ResponseEntity<List<UserResponseDto>> assignLeader(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody LabelRequestDto labelRequestDto,
		@PathVariable Long labelId) {
		return labelService.assignLeader(userDetails.getUser(), labelRequestDto, labelId);
	}

	@Operation(summary = "Label별 티켓 조회", description = "api for get ticketList by label")
	@GetMapping("/label/{labelId}")
	@ExecutionTimer
	public ResponseEntity<Map<TicketStatus, List<TicketResponseDto>>> getTicketListByLabel(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long labelId) {
		return labelService.getTicketListByLabel(userDetails.getUser(), labelId);
	}

}
