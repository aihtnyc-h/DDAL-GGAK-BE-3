package com.ddalggak.finalproject.domain.review.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.review.dto.ReviewCommentRequestDto;
import com.ddalggak.finalproject.domain.review.entity.Review;
import com.ddalggak.finalproject.domain.review.entity.ReviewComment;
import com.ddalggak.finalproject.domain.review.repository.ReviewCommentRepository;
import com.ddalggak.finalproject.domain.review.repository.ReviewRepository;
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
public class ReviewCommentService {
	private final ReviewCommentRepository reviewCommentRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	// 리뷰 댓글 등록
	@Transactional
	public ResponseEntity<?> createReviewComment(User user, ReviewCommentRequestDto reviewCommentRequestDto) {
		validateUserByEmail(user.getEmail());
		Review review = validateReview(reviewCommentRequestDto.getReviewId());
		ReviewComment reviewComment = new ReviewComment(user, review, reviewCommentRequestDto);
		// ReviewComment.create(reviewCommentRequestDto, review);
		reviewCommentRepository.save(reviewComment);
		return SuccessResponseDto.of(SuccessCode.CREATED_SUCCESSFULLY);
	}

	// 리뷰 댓글 수정
	@Transactional

	public ResponseEntity<?> updateReviewComment(Long review_commentId, User user,
		ReviewCommentRequestDto reviewCommentRequestDto) {
		validateUserByEmail(user.getEmail());
		validateReview(reviewCommentRequestDto.getReviewId());
		ReviewComment reviewComment = validateReviewComment(review_commentId);
		reviewComment.update(reviewCommentRequestDto);
		reviewCommentRepository.save(reviewComment);
		return SuccessResponseDto.of(SuccessCode.UPDATED_SUCCESSFULLY);
	}

	// 리뷰 댓글 삭제
	@Transactional

	public ResponseEntity<?> deleteReviewComment(Long review_commentId, User user) {
		validateUserByEmail(user.getEmail());
		ReviewComment reviewComment = validateReviewComment(review_commentId);
		reviewCommentRepository.delete(reviewComment);
		return SuccessResponseDto.of(SuccessCode.UPDATED_SUCCESSFULLY);
	}

	// 반복 로직
	private ReviewComment validateReviewComment(Long reviewCommentId) {
		return reviewCommentRepository.findById(reviewCommentId).orElseThrow(
			() -> new CustomException(REVIEW_COMMENT_NOT_FOUND));
	}

	private Review validateReview(Long reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(
			() -> new CustomException(REVIEW_NOT_FOUND));
	}

	private void validateUserByEmail(String email) {
		userRepository.findByEmail(email).orElseThrow(
			() -> new CustomException(UNAUTHORIZED_MEMBER));
	}
}
