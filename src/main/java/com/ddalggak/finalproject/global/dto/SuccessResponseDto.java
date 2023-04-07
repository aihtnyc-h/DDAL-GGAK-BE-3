package com.ddalggak.finalproject.global.dto;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SuccessResponseDto<T> {

	private final LocalDateTime timestamp = LocalDateTime.now();

	private final int status;

	private final String message;

	public SuccessResponseDto(SuccessCode successCode) {
		this.status = successCode.getHttpStatus().value();
		this.message = successCode.getDetail();
	}

	public static ResponseEntity<SuccessResponseDto> toResponseEntity(SuccessCode successCode) {
		return ResponseEntity
			.status(successCode.getHttpStatus())
			.body(SuccessResponseDto.builder()
				.status(successCode.getHttpStatus().value())
				.message(successCode.getDetail())
				.build()
			);
	}

	public static <T> ResponseEntity<SuccessResponseDto<T>> of(SuccessCode successCode) {
		return ResponseEntity
			.status(successCode.getHttpStatus())
			.body(SuccessResponseDto.<T>builder()
				.status(successCode.getHttpStatus().value())
				.message(successCode.getDetail())
				.build()
			);
	}
}
