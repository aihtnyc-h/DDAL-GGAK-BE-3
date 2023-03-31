package com.ddalggak.finalproject.domain.review.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ddalggak.finalproject.domain.review.dto.ReviewCommentRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.entity.BaseEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ReviewComment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewCommentId;
	@Column(nullable = false)
	private String reviewComment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reviewId")
	private Review review;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@Builder
	public ReviewComment(User user, Review review, ReviewCommentRequestDto reviewCommentList) {
		this.reviewCommentId = getReviewCommentId();
		this.user = user;
		this.review = review;
		this.reviewComment = reviewCommentList.getReviewComment();
	}

	public static ReviewComment create(ReviewCommentRequestDto reviewCommentRequestDto, Review review) {
		return ReviewComment.builder()
			.reviewCommentList(reviewCommentRequestDto)
			.review(review)
			.build();
	}

	public void update(ReviewCommentRequestDto reviewCommentRequestDto) {
		this.reviewComment = reviewCommentRequestDto.getReviewComment();
	}
}
