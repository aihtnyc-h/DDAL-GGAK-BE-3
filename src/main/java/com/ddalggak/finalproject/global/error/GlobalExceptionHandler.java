package com.ddalggak.finalproject.global.error;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.io.IOException;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ddalggak.finalproject.domain.user.exception.UserException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = {ConstraintViolationException.class, DataIntegrityViolationException.class})
	protected ResponseEntity<ErrorResponse> handleDataException() {
		log.error("handleDataException throw Exception : {}", DUPLICATE_RESOURCE);
		return ErrorResponse.of(DUPLICATE_RESOURCE);
	}

	/**
	 * 비즈니스 로직 실행 중 오류 발생시 핸들링 , 로그 기록해야함
	 */
	@ExceptionHandler(value = {CustomException.class})
	protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
		return ErrorResponse.from(e.getErrorCode());
	}

	@ExceptionHandler(value = {IOException.class})
	protected ResponseEntity<?> handleIOException(IOException e) {
		log.error("handleIOException throws IOException : {}", e.getMessage());
		return ErrorResponse.of(ErrorCode.SERVER_ERROR);
	}

	@ExceptionHandler(value = {UserException.class})
	protected ResponseEntity<ErrorResponse> handleUserException(UserException e) {
		log.error("handleUserException throw UserException : {}", e.getErrorCode());
		return ErrorResponse.from(e.getErrorCode(), e.getMessage());
	}

	/*
	 * IllegalArgumentException 인 경우 어떤 부분에서 error 뿌리는지 직접 출력
	 */
	@ExceptionHandler(value = {IllegalArgumentException.class})
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
		log.error("handleIAException throws IllegalArgumentException : {}", e.getMessage());
		return ErrorResponse.from(ErrorCode.INVALID_REQUEST, e.getMessage());
	}

	protected ErrorResponse makeErrorResponse(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.status(errorCode.getHttpStatus().value())
			.message(errorCode.getMessage())
			.build();
	}

	/*
	 * BindingResult로 에러 나왔을 때 처리
	 */

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
		HttpStatus status,
		WebRequest request) {
		log.error("handleBindException throws BindException : {}", ex.getMessage());
		return ErrorResponse.from(INVALID_REQUEST, ex.getBindingResult());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error("handleBindException throws BindException : {}", ex.getMessage());
		return ErrorResponse.from(INVALID_REQUEST, ex.getBindingResult());
	}


	/*
	 * 이외의 예외처리
	 */

	@ExceptionHandler(Exception.class)
	private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode, String message) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(makeErrorResponse(errorCode, message));
	}

	protected ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
		return ErrorResponse.builder()
			.status(errorCode.getHttpStatus().value())
			.message(message)
			.build();
	}

}
