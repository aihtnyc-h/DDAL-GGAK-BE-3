package com.ddalggak.finalproject.domain.comment.dto;

import com.ddalggak.finalproject.domain.comment.entity.Comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
	private Long commentId;
	private String comment;
	private String email;
	@Builder
	public CommentResponseDto(Comment c) {
		this.commentId = c.getCommentId();
		this.comment = c.getComment();
		this.email = c.getUser().getEmail();
	}
}
