package com.ddalggak.finalproject.domain.label.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.label.dto.LabelRequestDto;
import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.label.service.LabelService;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Label Controller", description = "Label 관련 API입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {
	private final LabelService labelService;

	@Operation(summary = "Label 생성", description = "api for creating label")
	@PostMapping("/label")
	public ResponseEntity<LabelResponseDto> createLabel(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody LabelRequestDto labelRequestDto) {
		return labelService.createLabel(userDetails.getUser(), labelRequestDto);
	}

	@Operation(summary = "Label 삭제", description = "api for delete label")
	@DeleteMapping("/label/{labelId}")
	public ResponseEntity<SuccessResponseDto> deleteLabel(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody Long taskId,
		@PathVariable Long labelId) {
		return labelService.deleteLabel(userDetails.getUser(), taskId, labelId);
	}
}
