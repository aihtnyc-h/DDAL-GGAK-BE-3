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
public class GlobalResponseDto<T> {

	private final LocalDateTime timestamp = LocalDateTime.now();
	private final int status;
	private final String message;
	private T data;

	public static <T> ResponseEntity<GlobalResponseDto<T>> of(SuccessCode successCode, T data) {
		return ResponseEntity
			.status(successCode.getHttpStatus())
			.body(GlobalResponseDto.<T>builder()
				.status(successCode.getHttpStatus().value())
				.message(successCode.getDetail())
				.data(data)
				.build()
			);
	}

}
