package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;
import java.util.List;

import com.ddalggak.finalproject.domain.comment.dto.CommentResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.global.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

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
	@JsonView(Views.Task.class)
	private Long ticketId;
	@Schema(name = "ticket title", example = "ticket title")
	@JsonView(Views.Task.class)
	private String title;
	@Schema(name = "ticket description", example = "ticket description")
	@JsonView(Views.Task.class)
	private String description;
	@Schema(name = "ticket status", example = "TODO")
	@JsonView(Views.Task.class)
	private TicketStatus status;
	@Schema(name = "ticket priority", example = "ticket priority")
	@JsonView(Views.Task.class)
	private int priority;
	@Schema(name = "ticket difficulty", example = "ticket difficulty")
	@JsonView(Views.Task.class)
	private int difficulty;
	@Schema(name = "ticket assigned", example = "ticket assigned")
	@JsonView(Views.Task.class)
	private String assigned;
	@Schema(name = "ticket expired at", example = "ticket expired at")
	@JsonView(Views.Task.class)
	private LocalDate expiredAt;
	@Schema(name = "when did ticket completed", example = "2020-03-11")
	@JsonView(Views.Task.class)
	private LocalDate completedAt;
	@Schema(name = "label", example = "label leader")
	@JsonView(Views.Task.class)
	private String label;
	@Schema(name = "total comments")
	@JsonView(Views.Ticket.class)
	private List<CommentResponseDto> commentList;

}
