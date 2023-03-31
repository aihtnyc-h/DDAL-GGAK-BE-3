package com.ddalggak.finalproject.domain.review.dto;

import com.ddalggak.finalproject.domain.review.entity.ReviewComment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCommentResponseDto {
	@Schema(name = "reviewComment id", example = "1")
	private Long reviewCommentId;
	@Schema(name = "reviewComment", example = "review comment")
	private String reviewComment;
	@Builder
	public ReviewCommentResponseDto(ReviewComment rc) {
		this.reviewCommentId = rc.getReviewCommentId();
		this.reviewComment = rc.getReviewComment();
	}
}
