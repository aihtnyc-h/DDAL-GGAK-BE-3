package com.ddalggak.finalproject.domain.review.dto;

import java.util.List;

import com.ddalggak.finalproject.domain.review.entity.Review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
	@Schema(name = "review id", example = "1")
	private Long reviewId;
	@Schema(name = "total ReviewComments")
	private List<ReviewCommentResponseDto> reviewCommentList;
	@Builder
	public ReviewResponseDto(Review review, List<ReviewCommentResponseDto> reviewCommentList) {
		this.reviewId = review.getReviewId();
		this.reviewCommentList = reviewCommentList;
	}
	public static ReviewResponseDto of(Review review) {
		return ReviewResponseDto.builder()
			.review(review)
			.build();
	}
}