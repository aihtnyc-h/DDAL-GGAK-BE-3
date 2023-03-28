package com.ddalggak.finalproject.domain.user.exception;

import com.ddalggak.finalproject.global.error.ErrorCode;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

	private final ErrorCode errorCode;
	private final String message;

	public UserException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}
}