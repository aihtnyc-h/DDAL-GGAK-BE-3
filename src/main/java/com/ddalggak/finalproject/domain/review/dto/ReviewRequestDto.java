package com.ddalggak.finalproject.domain.review.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
	@Schema(name = "ticket Id", example = "1")
	@NotNull(message = "ticket is required")
	private Long ticketId;
	@Schema(name = "review title", example = "review title")
	private String reviewTitle;
	@Schema(name = "review Description", example = "review Description")
	private String reviewDescription;
}
