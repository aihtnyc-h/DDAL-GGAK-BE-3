package com.ddalggak.finalproject.global.jwt.token.TokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.jwt.token.entity.Token;
import com.ddalggak.finalproject.global.jwt.token.repository.TokenRepository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenRepository tokenRepository;
	private final JwtUtil jwtUtil;

	public ResponseEntity<SuccessResponseDto> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtUtil.resolveToken(request);
		Claims userInfo = jwtUtil.getUserInfo(accessToken);
		String email = userInfo.getSubject();

		Token token = tokenRepository.findById(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_REFRESH_TOKEN));
		String refreshToken = jwtUtil.resolveRefreshToken(token.getRefreshToken());

		if (!jwtUtil.validateRefreshToken(refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_REQUEST);
		}

		String recreateAccessToken = jwtUtil.recreateAccessToken(accessToken);

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, recreateAccessToken);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_RECREATE_TOKEN);
	}
}