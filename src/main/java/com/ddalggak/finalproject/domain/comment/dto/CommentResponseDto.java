package com.ddalggak.finalproject.domain.comment.dto;

import com.ddalggak.finalproject.domain.comment.entity.Comment;
import com.ddalggak.finalproject.global.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class CommentResponseDto {

	@JsonView(Views.Ticket.class)
	private Long commentId;
	@JsonView(Views.Ticket.class)
	private String comment;
	@JsonView(Views.Ticket.class)
	private String email;

	@QueryProjection
	public CommentResponseDto(Long commentId, String comment, String email) {
		this.commentId = commentId;
		this.comment = comment;
		this.email = email;
	}

	public CommentResponseDto(Comment c) {
		this.commentId = c.getCommentId();
		this.comment = c.getComment();
		this.email = c.getUser().getEmail();
	}
}