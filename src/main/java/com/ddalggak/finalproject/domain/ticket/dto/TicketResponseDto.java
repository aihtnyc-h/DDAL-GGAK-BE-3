package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;
import java.util.List;

import com.ddalggak.finalproject.domain.comment.dto.CommentResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDto {
	@Schema(name = "ticket id", example = "1")
	private Long ticketId;
	@Schema(name = "ticket title", example = "ticket title")
	private String title;
	@Schema(name = "ticket description", example = "ticket description")
	private String description;
	@Schema(name = "ticket status", example = "TODO")
	private TicketStatus status;
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

}