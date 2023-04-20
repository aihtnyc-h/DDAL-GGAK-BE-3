package com.ddalggak.finalproject.domain.auth.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.auth.service.OAuthService;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthController {
	private final OAuthService oAuthservice;

	/*
		소셜 로그인
	 */
	@Operation(summary = "구글 소셜 로그인 API")
	@PostMapping("/loginInfo")
	public ResponseEntity<UserPageDto> callback(HttpServletResponse response,
		@RequestParam(name = "code") String code) {
		return oAuthservice.googleLogin(response, code);
	}
}
