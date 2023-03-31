package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;
import java.util.List;

import com.ddalggak.finalproject.domain.comment.dto.CommentResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketResponseDto {
	@Schema(name = "ticket id", example = "1")
	private Long ticketId;
	@Schema(name = "ticket title", example = "ticket title")
	private String title;
	@Schema(name = "ticket description", example = "ticket description")
	private String description;

	@Schema(name = "ticket status", example = "TODO")
	private TicketStatus ticketStatus;
	@Schema(name = "ticket priority", example = "ticket priority")
	private int priority;
	@Schema(name = "ticket difficulty", example = "ticket difficulty")
	private int difficulty;
	@Schema(name = "ticket assigned", example = "ticket assigned")
	private String assigned;

	@Schema(name = "ticket expired at", example = "ticket expired at")
	private LocalDate expiredAt;

	@Schema(name = "when did ticket completed", example = "2020-03-11")
	private LocalDate completedAt;

	@Schema(name = "label", example = "label leader")
	private String label;
	@Schema(name = "total comments")
	private List<CommentResponseDto> commentList;

	@Builder
	public TicketResponseDto(Ticket ticket, List<CommentResponseDto> commentList) {
		ticketId = ticket.getTicketId();
		title = ticket.getTicketTitle();
		description = ticket.getTicketDescription();
		ticketStatus = ticket.getStatus();
		priority = ticket.getPriority();
		difficulty = ticket.getDifficulty();
		assigned = ticket.getUser() == null ? null : ticket.getUser().getEmail();
		expiredAt = ticket.getExpiredAt();
		label = ticket.getLabel() == null ? null : ticket.getLabel().getLabelTitle();
		this.commentList = commentList;
	}

	public static TicketResponseDto of(Ticket ticket) {
		return TicketResponseDto.builder()
			.ticket(ticket)
			.build();
	}

	// public static ResponseEntity<TicketResponseDto>ticketResponseDtoResponseEntity of(Ticket ticket) {
	// 	return ResponseEntity
	// 		.status(200)
	// 		.body(TicketResponseDto.builder()
	// 			.ticket(ticket)
	// 			.build());
	// }

}
