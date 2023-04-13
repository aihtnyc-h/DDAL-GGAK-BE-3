package com.ddalggak.finalproject.global.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.Link;
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

	public static <T> ResponseEntity<GlobalResponseDto<T>> of(SuccessCode successCode, T data, List<Link> links) {
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
