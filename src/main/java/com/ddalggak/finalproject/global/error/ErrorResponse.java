package com.ddalggak.finalproject.global.error;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

	private final LocalDateTime timestamp = LocalDateTime.now();
	private final int status;
	private final String message;

	/*
	 * 중요한 비즈니스 로직일 경우 메시지를 숨기기 위해 에러코드로 작성
	 */

	public static ResponseEntity<ErrorResponse> of(ErrorCode errorCode) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.message(errorCode.getErrorCode())
				.build()
			);
	}

	/*
	 * 중요하지 않은 비즈니스 로직일 경우 메시지를 노출해도 상관없음
	 */

	public static ResponseEntity<ErrorResponse> from(ErrorCode errorCode) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.message(errorCode.getMessage())
				.build()
			);
	}

	public static ResponseEntity<ErrorResponse> from(ErrorCode errorCode, String message) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.message(message)
				.build()
			);
	}

	/*
	 * @Valid 검증을 할 경우 BindingResult를 통한 검증 방식 만듬
	 */
	public static ResponseEntity<Object> from(ErrorCode errorCode, BindingResult bindingResult) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.message(createMessage(bindingResult))
				.build()
			);
	}

	public static String createMessage(BindingResult bindingResult) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;

		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			if (!isFirst) {
				sb.append(", ");
			} else {
				isFirst = false;
			}
			sb.append("[");
			sb.append(fieldError.getField());
			sb.append("] ");
			sb.append(fieldError.getDefaultMessage());
		}
		return sb.toString();
	}

}