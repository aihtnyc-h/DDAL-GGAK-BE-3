package com.ddalggak.finalproject.domain.review.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.review.dto.ReviewRequestDto;
import com.ddalggak.finalproject.domain.review.service.ReviewService;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
@Tag(name = "Review Controller", description = "리뷰 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
	private final ReviewService reviewService;

	// 리뷰 등록
	@PostMapping("/review")
	public ResponseEntity<?> createReview(
		@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ReviewRequestDto reviewRequestDto) {
		return reviewService.createReview(userDetails.getUser(), reviewRequestDto);
	}
}
