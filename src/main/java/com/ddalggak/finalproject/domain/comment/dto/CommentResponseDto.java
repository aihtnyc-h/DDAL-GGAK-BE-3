package com.ddalggak.finalproject.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentResponseDto {

	private Long commentId;
	private String comment;
	private String email;
}