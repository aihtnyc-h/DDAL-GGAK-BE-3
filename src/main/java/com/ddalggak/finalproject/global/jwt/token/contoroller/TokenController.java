package com.ddalggak.finalproject.global.jwt.token.contoroller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.error.ErrorResponse;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.jwt.token.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {
	private final TokenService tokenService;
	private final JwtUtil jwtUtil;

	@PostMapping("/refresh")
	public ResponseEntity<?> validateRefreshToken(
		HttpServletRequest request) {
		String refreshToken = jwtUtil.resolveToken(request);
		Map<String, String> map = tokenService.validateRefreshToken(refreshToken);

		if (map.get("status").equals("402")) {
			log.info("RefreshController - Refresh Token이 만료.");
			return ErrorResponse.from(ErrorCode.UNAUTHORIZED_MEMBER);
		}

		log.info("RefreshController - Refresh Token이 유효.");
		return SuccessResponseDto.toResponseEntity(SuccessCode.GET_ACCESS_TOKEN);

	}

}
