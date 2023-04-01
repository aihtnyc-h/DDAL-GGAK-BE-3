package com.ddalggak.finalproject.global.jwt.token.contoroller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.jwt.token.TokenService.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {
	private final TokenService tokenService;

	@PostMapping("/refresh")
	public ResponseEntity<?> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
		tokenService.getAccessToken(request, response);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_AUTH);
	}
}
