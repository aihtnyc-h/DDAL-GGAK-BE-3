package com.ddalggak.finalproject.global.jwt;

import com.ddalggak.finalproject.global.error.ErrorCode;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

	private final ErrorCode errorCode;
	private final String message;

	public TokenException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}
}