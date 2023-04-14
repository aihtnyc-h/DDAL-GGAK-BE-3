package com.ddalggak.finalproject.global.error;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.io.IOException;

import javax.validation.ConstraintViolationException;

import org.hibernate.TransientPropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.global.oauth.exception.OAuthException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = {ConstraintViolationException.class, DataIntegrityViolationException.class})
	protected ResponseEntity<ErrorResponse> handleDataException() {
		log.error("handleDataException throw Exception : {}", UNPROCESSABLE_CONTENT);
		return ErrorResponse.of(UNPROCESSABLE_CONTENT);
	}

	/**
	 * 비즈니스 로직 실행 중 오류 발생시 핸들링 , 로그 기록해야함
	 */
	@ExceptionHandler(value = {CustomException.class})
	protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
		return ErrorResponse.from(e.getErrorCode());
	}

	@ExceptionHandler(value = {OAuthException.class})
	protected ResponseEntity<ErrorResponse> handleOAuthException(OAuthException e) {
		log.error("handleOAuthException throw OAuthException : {}", e.getErrorCode());
		return ErrorResponse.from(e.getErrorCode(), e.getMessage());
	}

	@ExceptionHandler(value = {IOException.class})
	protected ResponseEntity<?> handleIOException(IOException e) {
		log.error("handleIOException throws IOException : {}", e.getMessage());
		return ErrorResponse.of(SERVER_ERROR);
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

	@ExceptionHandler(value = {JpaSystemException.class})
	protected ResponseEntity<ErrorResponse> handleJpaSystemException(JpaSystemException e) {
		log.error("handleJpaSystemException throws JpaSystemException : {}", e.getMessage());
		return ErrorResponse.from(UNPROCESSABLE_CONTENT, e.getMessage());
	}

	@ExceptionHandler(value = {TransientPropertyValueException.class})
	protected ResponseEntity<ErrorResponse> handleTransientPropertyValueException(TransientPropertyValueException e) {
		log.error("handleTransientPropertyValueException throws TransientPropertyValueException : {}", e.getMessage());
		return ErrorResponse.from(UNPROCESSABLE_CONTENT, e.getMessage());
	}

	@ExceptionHandler(value = {NullPointerException.class})
	protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
		log.error("handleNullPointerException throws NullPointerException : {}", e.getMessage());
		return ErrorResponse.from(UNPROCESSABLE_CONTENT);
	}

	@ExceptionHandler(RequestRejectedException.class)
	protected ResponseEntity<ErrorResponse> handleRequestRejectedException(RequestRejectedException e) {
		log.error("handleRequestRejectedException throws RequestRejectedException : {}", e.getMessage());
		return ErrorResponse.from(INVALID_URL);
	}

	@ExceptionHandler(ClassCastException.class)
	protected ResponseEntity<ErrorResponse> handleClassCastException(ClassCastException e) {
		log.error("handleClassCastException throws ClassCastException : {}", e.getMessage());
		return ErrorResponse.from(INVALID_REQUEST);
	}

	@ExceptionHandler(InvalidDataAccessApiUsageException.class)
	protected ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(
		InvalidDataAccessApiUsageException e) {
		log.error("handleInvalidDataAccessApiUsageException throws InvalidDataAccessApiUsageException : {}",
			e.getMessage());
		return ErrorResponse.from(UNPROCESSABLE_CONTENT);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	protected ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
		MaxUploadSizeExceededException e) {
		log.error("handleMaxUploadSizeExceededException throws MaxUploadSizeExceededException : {}",
			e.getMessage());
		return ErrorResponse.from(UNPROCESSABLE_CONTENT, e.getMessage());
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	protected ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
		log.error("handleUsernameNotFoundException throws UsernameNotFoundException : {}", e.getMessage());
		return ErrorResponse.from(MEMBER_NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode, String message) {
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
