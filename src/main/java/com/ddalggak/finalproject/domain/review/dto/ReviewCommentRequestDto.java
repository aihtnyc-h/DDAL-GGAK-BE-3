package com.ddalggak.finalproject.domain.review.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCommentRequestDto {
	@Schema(name = "review Id")
	@NotNull(message = "")
	private Long reviewId;
	@Schema(name = "comment", example = "comment")
	private String reviewComment;
}
