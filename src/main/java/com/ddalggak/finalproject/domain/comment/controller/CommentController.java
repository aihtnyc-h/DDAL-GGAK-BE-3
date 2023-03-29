package com.ddalggak.finalproject.domain.comment.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.comment.dto.CommentRequestDto;
import com.ddalggak.finalproject.domain.comment.service.CommentService;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Log Controller", description = "로그 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	private final CommentService commentService;
	// 댓글 등록
	@Operation(summary = "ticket comment", description = "comment 등록 post 메서드 체크")
	@PostMapping("/comment")
	public ResponseEntity<SuccessResponseDto> createComment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody CommentRequestDto commentRequestDto) {
		return commentService.createComment(userDetails.getUser(), commentRequestDto);
	}
	// 댓글 수정
	@Operation(summary = "patch ticket comment", description = "comment 수정 get 메서드 체크")
	@PatchMapping("/comment/{commentId}")
	public ResponseEntity<SuccessResponseDto> updateComment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("commentId") Long commentId,
		@Valid @RequestBody CommentRequestDto commentRequestDto) {
		return commentService.updateComment(userDetails.getUser(), commentId, commentRequestDto);
	}
	// 댓글 삭제
	@Operation(summary = "delete ticket comment", description = "comment 삭제 delete 메서드 체크")
	@DeleteMapping("/comment/{commentId}")
	public ResponseEntity<SuccessResponseDto> deleteComment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("commentId") Long commentId) {
		return commentService.deleteComment(userDetails, commentId);
	}
}