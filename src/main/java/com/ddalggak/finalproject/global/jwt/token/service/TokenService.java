package com.ddalggak.finalproject.global.jwt.token.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.jwt.token.entity.AccessToken;
import com.ddalggak.finalproject.global.jwt.token.entity.RefreshToken;
import com.ddalggak.finalproject.global.jwt.token.repository.AccessTokenRepository;
import com.ddalggak.finalproject.global.jwt.token.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final AccessTokenRepository accessTokenRepository;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	public void getAccessToken(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtUtil.resolveToken(request);
		Claims userInfo = jwtUtil.getUserInfo(accessToken);
		String email = userInfo.getSubject();

		RefreshToken refreshToken = refreshTokenRepository.findById(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_REFRESH_TOKEN));
		String resolvedRefreshToken = jwtUtil.resolveRefreshToken(refreshToken.getRefreshToken());

		if (!jwtUtil.validateRefreshToken(resolvedRefreshToken)) {
			throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
		}

		accessTokenRepository.deleteById(email);
		String recreateAccessToken = jwtUtil.recreateAccessToken(accessToken);
		AccessToken newAccessToken = new AccessToken(email, recreateAccessToken);

		if (accessTokenRepository.findById(email).isEmpty()) {
			accessTokenRepository.save(newAccessToken);
		}

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, recreateAccessToken);
	}

	public boolean checkAccessToken(HttpServletRequest request) {
		String accessToken = jwtUtil.resolveToken(request);
		Claims userInfo = jwtUtil.getUserInfo(accessToken);
		String email = userInfo.getSubject();

		refreshTokenRepository.findById(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_REFRESH_TOKEN));

		AccessToken savedAccessToken = accessTokenRepository.findById(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_AUTH_TOKEN));

		String checkToken = savedAccessToken.getAccessToken().substring(7);

		return checkToken.equals(accessToken);
	}
}