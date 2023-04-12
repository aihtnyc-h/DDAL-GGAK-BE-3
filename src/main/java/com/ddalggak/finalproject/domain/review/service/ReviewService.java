package com.ddalggak.finalproject.domain.review.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.review.dto.ReviewCommentResponseDto;
import com.ddalggak.finalproject.domain.review.dto.ReviewRequestDto;
import com.ddalggak.finalproject.domain.review.dto.ReviewResponseDto;
import com.ddalggak.finalproject.domain.review.entity.Review;
import com.ddalggak.finalproject.domain.review.entity.ReviewComment;
import com.ddalggak.finalproject.domain.review.repository.ReviewCommentRepository;
import com.ddalggak.finalproject.domain.review.repository.ReviewRepository;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.repository.TicketRepository;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final TicketRepository ticketRepository;
	private final UserRepository userRepository;
	private final ReviewCommentRepository reviewCommentRepository;

	// 리뷰 등록
	@Transactional
	public ResponseEntity<?> createReview(User user, ReviewRequestDto reviewRequestDto) {
		validateUserByEmail(user.getEmail());
		Ticket ticket = validateTicket(reviewRequestDto.getTicketId());
		Review review = Review.create(reviewRequestDto, ticket);
		reviewRepository.save(review);
		return SuccessResponseDto.toResponseEntity(SuccessCode.CREATED_SUCCESSFULLY);
	}

	// 리뷰 전체 조회 (전체 조회 가능)
	@Transactional(readOnly = true)
	public ResponseEntity<List<ReviewResponseDto>> getReviewAll(User user, ReviewRequestDto reviewRequestDto) {
		validateTicket(reviewRequestDto.getTicketId());
		List<Review> reviewList = reviewRepository.findAllByOrderByModifiedAtDesc();
		List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();
		for (Review review : reviewList) {
			if (reviewList.isEmpty()) {
				throw new CustomException(REVIEW_NOT_FOUND);
			}
			reviewResponseDtoList.add(ReviewResponseDto.of(review));
		}
		return ResponseEntity.ok().body(reviewResponseDtoList);
	}
	// 리뷰 상세 조회 (전체 조회 가능)
	@Transactional(readOnly = true)
	public ResponseEntity<ReviewResponseDto> getReview(Long reviewId, User user, ReviewRequestDto reviewRequestDto) {
		validateTicket(reviewRequestDto.getTicketId());
		Review review = reviewRepository.findById(reviewId).orElseThrow(
			()-> new CustomException(REVIEW_NOT_FOUND));
		List<ReviewCommentResponseDto> reviewCommentResponseDtoList = getReviewComment(review);
		ReviewResponseDto reviewResponseDto = new ReviewResponseDto(review, reviewCommentResponseDtoList);
		return ResponseEntity.ok().body(reviewResponseDto);
	}
	// 리뷰 수정 (리뷰 작성자만 작성가능)
	@Transactional
	public ResponseEntity<?> updateReview(Long reviewId, User user, ReviewRequestDto reviewRequestDto) {
		validateUserByEmail(user.getEmail());
		validateTicket(reviewRequestDto.getTicketId());
		Review review = validateReview(reviewId);
		review.update(reviewRequestDto);
		return SuccessResponseDto.toResponseEntity(SuccessCode.UPDATED_SUCCESSFULLY);
	}
	// 리뷰 삭제 (리뷰 작성자만 작성가능)
	@Transactional
	public ResponseEntity<?> deleteReview(Long reviewId, User user) {
		validateUserByEmail(user.getEmail());
		Review review = validateReview(reviewId);
		reviewRepository.delete(review);
		return SuccessResponseDto.toResponseEntity(SuccessCode.DELETED_SUCCESSFULLY);
	}

	// 반복 로직
	private List<ReviewCommentResponseDto> getReviewComment(Review review) {
		List<ReviewCommentResponseDto> reviewCommentResponseDtoList = new ArrayList<>();
		List<ReviewComment> reviewCommentList = reviewCommentRepository.findByReviewOrderByCreatedAtDesc(review);
		for (ReviewComment rc : reviewCommentList) {
			reviewCommentResponseDtoList.add(new ReviewCommentResponseDto(rc));
		}
		return reviewCommentResponseDtoList;
	}
	private Review validateReview(Long reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(
			() -> new CustomException(TICKET_NOT_FOUND));
	}
	private Ticket validateTicket(Long ticketId) {
		return ticketRepository.findById(ticketId).orElseThrow(
			() -> new CustomException(TICKET_NOT_FOUND));
	}

	private User validateUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(UNAUTHORIZED_MEMBER));
	}

}