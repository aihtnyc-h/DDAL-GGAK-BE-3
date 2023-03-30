package com.ddalggak.finalproject.domain.comment.dto;

import javax.validation.constraints.NotNull;

import com.ddalggak.finalproject.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto{
	@Schema(name = "ticket Id")
	@NotNull(message = "ticket Id is required")
	private Long ticketId;
	private String comment;

}
