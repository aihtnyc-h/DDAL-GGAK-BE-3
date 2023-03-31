package com.ddalggak.finalproject.domain.review.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.review.dto.ReviewCommentRequestDto;
import com.ddalggak.finalproject.domain.review.service.ReviewCommentService;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review Comment Controller", description = "리뷰 댓글 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewCommentController {
	private final ReviewCommentService reviewCommentService;

	// 리뷰 댓글 등록
	@Operation(summary = "review comment 생성", description = "review comment 등록 post 메서드 체크")

	@PostMapping("/review_comment")
	public ResponseEntity<?> createReviewComment(
		@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ReviewCommentRequestDto reviewCommentRequestDto) {
		return reviewCommentService.createReviewComment(userDetails.getUser(), reviewCommentRequestDto);
	}

	// 리뷰 댓글 수정
	@PostMapping("/review_comment/{review_commentId}")
	public ResponseEntity<?> updateReviewComment(
		@PathVariable Long review_commentId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody ReviewCommentRequestDto reviewCommentRequestDto) {
		return reviewCommentService.updateReviewComment(review_commentId, userDetails.getUser(), reviewCommentRequestDto);
	}
	// 리뷰 댓글 삭제
	@DeleteMapping("/review_comment/{review_commentId}")
	public ResponseEntity<?> deleteReviewComment(
		@PathVariable Long review_commentId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
	return reviewCommentService.deleteReviewComment(review_commentId, userDetails.getUser());
	}
}