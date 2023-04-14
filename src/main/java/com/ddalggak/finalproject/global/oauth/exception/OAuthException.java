package com.ddalggak.finalproject.global.oauth.exception;

import com.ddalggak.finalproject.global.error.ErrorCode;

import lombok.Getter;

@Getter
public class OAuthException extends RuntimeException {

	private final ErrorCode errorCode;
	private final String message;

	public OAuthException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}
}