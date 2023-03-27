package com.ddalggak.finalproject.domain.comment.dto;

import com.ddalggak.finalproject.global.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentResponseDto {

	@JsonView(Views.Ticket.class)
	private Long commentId;
	@JsonView(Views.Ticket.class)
	private String comment;
	@JsonView(Views.Ticket.class)
	private String email;
}
