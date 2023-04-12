package com.ddalggak.finalproject.domain.ticket.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.ticket.dto.TicketLabelRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.service.TicketService;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;
import com.ddalggak.finalproject.global.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

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

	// 티켓 상세 조회
	@Operation(summary = "get ticket", description = "Ticket 상세조회 get 메서드 체크")
	@GetMapping("/ticket/{ticketId}")
	@JsonView(Views.Ticket.class)
	public ResponseEntity<TicketResponseDto> getTicket(
		@PathVariable Long ticketId,
		@RequestParam Long taskId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ticketService.getTicket(ticketId, taskId, userDetails.getUser());
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
	public ResponseEntity<?> deleteTicket(@PathVariable Long ticketId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ticketService.deleteTicket(userDetails.getUser(), ticketId);
	}

	//티켓 완료
	@Operation(summary = "complete ticket", description = "Ticket 완료 complete 메서드 체크")
	@PostMapping("/ticket/{ticketId}/complete")
	public ResponseEntity<?> completeTicket(@PathVariable Long ticketId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ticketService.completeTicket(userDetails.getUser(), ticketId);
	}

	//티켓 가져가기
	@Operation(summary = "assign ticket", description = "Ticket 가져가기 assign 메서드 체크")
	@PostMapping("/ticket/{ticketId}/assign")
	public ResponseEntity<?> assignTicket(@PathVariable Long ticketId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ticketService.assignTicket(userDetails.getUser(), ticketId);
	}

	@Operation(summary = "get label for ticket", description = "Ticket에 라벨 부여하기")
	@PostMapping("/ticket/{ticketId}/label")
	public ResponseEntity<?> getLabelForTicket(@PathVariable Long ticketId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody TicketLabelRequestDto ticketLabelRequestDto) {
		return ticketService.getLabelForTicket(userDetails.getUser(), ticketId, ticketLabelRequestDto);
	}
	// 티켓 이동하기
	@PostMapping("/ticket/{ticketId}/movement")
	public ResponseEntity<?> movementTicket(@PathVariable Long ticketId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ticketService.movementTicket(userDetails.getUser(), ticketId);
	}
}
