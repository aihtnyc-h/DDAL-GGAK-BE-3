package com.ddalggak.finalproject.global.jwt.token.exception;

import java.io.IOException;

import com.ddalggak.finalproject.global.error.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Getter
public class JwtExceptionResponse {
	private final ErrorCode errorCode;
	private final String message;

	public JwtExceptionResponse(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}

	public String convertToJson() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

}
