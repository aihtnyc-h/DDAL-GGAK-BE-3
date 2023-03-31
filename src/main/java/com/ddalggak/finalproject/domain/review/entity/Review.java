package com.ddalggak.finalproject.domain.review.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.ddalggak.finalproject.domain.review.dto.ReviewRequestDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.entity.BaseEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Review extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;
	private String reviewTitle;
	// 티켓 완료 내용
	private String reviewDescription;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticketId")
	private Ticket ticket;
	@OneToMany(mappedBy = "reviewComment", cascade = CascadeType.REMOVE)
	private List<ReviewComment> reviewComments = new ArrayList<>();

	public Review(ReviewRequestDto reviewRequestDto, User user, List<ReviewComment> reviewCommentList) {
		this.reviewTitle = reviewRequestDto.getReviewTitle();
		this.reviewDescription = reviewRequestDto.getReviewDescription();
		this.reviewComments = reviewCommentList;
	}
	@Builder
	public Review(ReviewRequestDto reviewRequestDto, Ticket ticket) {
		this.reviewTitle = reviewRequestDto.getReviewTitle();
		this.reviewDescription = reviewRequestDto.getReviewDescription();
		this.ticket = ticket;
	}
	public void update(ReviewRequestDto reviewRequestDto) {
		this.reviewTitle = reviewRequestDto.getReviewTitle();
		this.reviewDescription = reviewRequestDto.getReviewDescription();
		this.reviewComments = getReviewComments();
	}

	public static Review create(ReviewRequestDto reviewRequestDto, Ticket ticket) {
		return Review.builder()
			.reviewRequestDto(reviewRequestDto)
			.ticket(ticket)
			.build();
	}
}
