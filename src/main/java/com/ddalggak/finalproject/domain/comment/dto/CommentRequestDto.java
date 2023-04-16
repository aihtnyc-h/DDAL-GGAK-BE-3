package com.ddalggak.finalproject.domain.comment.dto;

import com.ddalggak.finalproject.global.validation.RequestId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
	@Schema(name = "ticket Id")
	@RequestId
	private Long ticketId;
	private String comment;

}
