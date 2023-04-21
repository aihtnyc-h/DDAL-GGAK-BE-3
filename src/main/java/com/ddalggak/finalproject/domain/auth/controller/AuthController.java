package com.ddalggak.finalproject.domain.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.domain.auth.service.AuthService;
import com.ddalggak.finalproject.domain.user.dto.EmailRequestDto;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.aop.ExecutionTimer;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.error.ErrorResponse;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.jwt.token.service.TokenService;
import com.ddalggak.finalproject.global.mail.MailService;
import com.ddalggak.finalproject.global.mail.emailAuthCode.EmailAuthCodeDto;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth Controller", description = "인증 관련 API 입니다.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final MailService mailService;
	private final AuthService authService;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final TokenService tokenService;

	@Operation(summary = "email authentication", description = "email 인증 코드 발송 post 메서드 체크")
	@PostMapping("/email")
	@ExecutionTimer
	public ResponseEntity<SuccessResponseDto> emailAuthentication(
		@Valid @RequestBody EmailRequestDto emailRequestDto) {
		return mailService.sendRandomCode(emailRequestDto.getEmail());
	}

	@Operation(summary = "email authentication check", description = "email 인증 코드 일치 확인 get 메서드 체크")
	@GetMapping("/email")
	@ExecutionTimer
	public ResponseEntity<SuccessResponseDto> emailAuthenticationWithRandomCode(
		@RequestBody EmailAuthCodeDto emailAuthCodeDto) {
		authService.emailAuthenticationWithRandomCode(emailAuthCodeDto);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_AUTH);
	}

	@Operation(summary = "signup", description = "회원가입 post 메서드 체크")
	@PostMapping("/signup")
	@ExecutionTimer
	public ResponseEntity<SuccessResponseDto> signup(
		@Valid @RequestBody UserRequestDto userRequestDto) {
		return authService.signup(userRequestDto);
	}

	@Operation(summary = "login", description = "로그인 post 메서드 체크")
	@PostMapping("/login")
	@ExecutionTimer
	public ResponseEntity<UserPageDto> login(
		@RequestBody UserRequestDto userRequestDto,
		HttpServletResponse response) {
		return authService.login(userRequestDto, response);
	}

	@Operation(summary = "logout", description = "로그아웃 post 메서드 체크")
	@PostMapping("/logout")
	@ExecutionTimer
	public ResponseEntity<SuccessResponseDto> logout(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		String email = userDetails.getEmail();
		// 시큐리티 정보 지우기
		SecurityContextHolder.clearContext();
		jwtUtil.logout(email);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_LOGOUT);
	}

	@Operation(summary = "validate token", description = "권한 확인 get 메서드 체크")
	@GetMapping("/validToken")
	@ExecutionTimer
	public ResponseEntity<?> validateToken(
		HttpServletRequest request) {
		// 토큰 가져오기
		String token = jwtUtil.resolveToken(request);
		Claims claims;
		if (token != null) {
			// 토큰 유효성 검사
			if (jwtUtil.validateToken(token)) {
				claims = jwtUtil.getUserInfo(token);
			} else {
				return ErrorResponse.from(ErrorCode.INVALID_REQUEST);
			}
			userRepository.findByEmail(claims.getSubject())
				.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
		} else
			throw new UserException(ErrorCode.INVALID_REQUEST);
		return SuccessResponseDto.of(SuccessCode.SUCCESS_AUTH);
	}

	// @Operation(summary = "refresh token", description = "토큰 재발급 get 메서드 체크")
	// @GetMapping("/refreshToken")
	// @ExecutionTimer
	// public void refreshToken(
	// 	HttpServletRequest request,
	// 	HttpServletResponse response) {
	// 	tokenService.refreshToken(request, response);
	// }

}
