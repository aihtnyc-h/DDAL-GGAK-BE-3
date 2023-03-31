package com.ddalggak.finalproject.domain.review.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.review.dto.ReviewRequestDto;
import com.ddalggak.finalproject.domain.review.dto.ReviewResponseDto;
import com.ddalggak.finalproject.domain.review.service.ReviewService;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
@Tag(name = "Review Controller", description = "리뷰 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
	private final ReviewService reviewService;

	// 리뷰 등록 (티켓을 가지고 있는 사람만 작성 가능)
	@Operation(summary = "review 생성", description = "review 등록 post 메서드 체크")
	@PostMapping("/review")
	public ResponseEntity<?> createReview(
		@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ReviewRequestDto reviewRequestDto) {
		return reviewService.createReview(userDetails.getUser(), reviewRequestDto);
	}

	// 리뷰 전체 조회 (전체 조회 가능)
	@Operation(summary = "review 전체 조회", description = "review 전체조회 get 메서드 체크")
	@GetMapping("/reviews")
	public ResponseEntity<List<ReviewResponseDto>> getReviewAll(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
		return reviewService.getReviewAll(userDetails.getUser(), reviewRequestDto);
	}
	// 리뷰 상세 조회 (전체 조회 가능)
	@Operation(summary = "review 상세 조회", description = "review 상세 조회 get 메서드 체크")
	@GetMapping("/review/{reviewId}")
	public ResponseEntity<ReviewResponseDto> getReview(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
		return reviewService.getReview(reviewId, userDetails.getUser(), reviewRequestDto);
	}
	// 리뷰 수정 (리뷰 작성자만 작성가능)
	@Operation(summary = "review 수정", description = "review 수정 patch 메서드 체크")
	@PostMapping("/review/{reviewId}")
	public ResponseEntity<?> updateReview(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
		return reviewService.updateReview(reviewId, userDetails.getUser(), reviewRequestDto);
	}
	// 리뷰 삭제 (리뷰 작성자만 작성가능)
	@Operation(summary = "review 삭제", description = "review 삭제 delete 메서드 체크")
	@DeleteMapping("/review/{reviewId}")
	public ResponseEntity<?> deleteReview(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return reviewService.deleteReview(reviewId, userDetails.getUser());
	}

}
