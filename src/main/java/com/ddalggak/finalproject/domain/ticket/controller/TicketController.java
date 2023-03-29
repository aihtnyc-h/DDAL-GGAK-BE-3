package com.ddalggak.finalproject.domain.ticket.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.ticket.dto.TicketRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.service.TicketService;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Ticket Controller", description = "티켓 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {
	private final TicketService ticketService;


	// 티켓 등록
	@Operation(summary = "ticket 생성", description = "Ticket 등록 post 메서드 체크")
	@PostMapping("/ticket")
	public ResponseEntity<?> createTicket(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody TicketRequestDto ticketRequestDto) {
		return ticketService.createTicket(userDetails.getUser(), ticketRequestDto);
	}

	// // 티켓 전체 조회 (테스크에 들어갈 내용) getTickets
	// @GetMapping
	// public ResponseEntity<List<TicketResponseDto>> getTickets(@AuthenticationPrincipal UserDetailsImpl userDetails) {
	// 	return ticketService.getTickets();
	// }

	// 티켓 상세 조회
	// @Operation(summary = "get ticket", description = "Ticket 상세조회 get 메서드 체크")
	// @GetMapping("/ticket/{ticketId}")
	// public ResponseEntity<TicketResponseDto> getTicket(@PathVariable Long ticketId, @PathVariable Long taskId) {
	// 	return ticketService.getTicket(ticketId);
	// }
	//
	// @GetMapping("/{postId}")
	// public ResponseEntity<TicketResponseDto> getPost(@PathVariable Long ticketId, @PathVariable Long taskId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
	// 	return ticketService.getTicket(ticketId, taskId, userDetails.getUser());
	// }
	// public ResponseEntity<?> getTicket(
	// 	@AuthenticationPrincipal UserDetailsImpl userDetails,
	// 	@Valid @RequestBody TicketRequestDto ticketRequestDto,
	// 	@PathVariable Long ticketId
	// ) {
	// 	return ticketService.getTicket(userDetails.getUser(), ticketRequestDto, ticketId);//.getTicket(userDetails.getUser(), ticketId);
	// }

	// 티켓 상세 조회
	// @Operation(summary = "get ticket", description = "Ticket 상세조회 get 메서드 체크")
	// @GetMapping("/ticket/{ticketId}")
	// public ResponseEntity<TicketResponseDto> getTicket(
	// 	@PathVariable Long ticketId,
	// 	@RequestBody TicketRequestDto ticketRequestDto,
	// 	@AuthenticationPrincipal UserDetailsImpl userDetails) {
	// 	return ticketService.getTicket(userDetails.getUser(), ticketId, ticketRequestDto.getTaskId());
	// }

	// 티켓 상세 조회
	@Operation(summary = "get ticket", description = "Ticket 상세조회 get 메서드 체크")
	@GetMapping("/ticket/{ticketId}")
	public ResponseEntity<TicketResponseDto> getTicket(
		@PathVariable Long ticketId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody TicketRequestDto ticketRequestDto) {
		return ticketService.getTicket(ticketId, userDetails.getUser(), ticketRequestDto);
	}
	// 티켓 수정
	@Operation(summary = "patch ticket", description = "Ticket 수정 patch 메서드 체크")
	@PatchMapping("/ticket/{ticketId}")
	public ResponseEntity<?> updateTicket(
		@PathVariable Long ticketId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody TicketRequestDto ticketRequestDto) {
		return ticketService.updateTicket(ticketId, ticketRequestDto, userDetails.getUser());
	}

	// 티켓 삭제
	@Operation(summary = "delete ticket", description = "Ticket 삭제 delete 메서드 체크")
	@DeleteMapping("/ticket/{ticketId}")
	public ResponseEntity<?> deleteTicket(@PathVariable Long ticketId,	@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ticketService.deleteTicket(userDetails.getUser(), ticketId);
	}
}
