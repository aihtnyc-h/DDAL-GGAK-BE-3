package com.ddalggak.finalproject.global.jwt.token.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.jwt.token.entity.RefreshToken;
import com.ddalggak.finalproject.global.jwt.token.entity.Token;
import com.ddalggak.finalproject.global.jwt.token.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public void login(Token token) {

		RefreshToken refreshToken = RefreshToken.builder()
			.email(token.getEmail())
			.refreshToken(token.getRefreshToken())
			.build();
		String loginUserEmail = refreshToken.getEmail();
		if (refreshTokenRepository.existsByEmail(loginUserEmail)) {
			log.info("기존의 존재하는 refresh 토큰 삭제");
			refreshTokenRepository.deleteByEmail(loginUserEmail);
		}
		refreshTokenRepository.save(refreshToken);

	}

	public Optional<RefreshToken> getRefreshToken(String refreshToken) {

		return refreshTokenRepository.findByRefreshToken(refreshToken);
	}

	public Map<String, String> validateRefreshToken(String refreshToken) {
		RefreshToken savedRefreshToken = getRefreshToken(refreshToken).get();
		String createdAccessToken = jwtUtil.validateRefreshToken(savedRefreshToken);

		return createRefreshJson(createdAccessToken);
	}

	public Map<String, String> createRefreshJson(String createdAccessToken) {

		Map<String, String> map = new HashMap<>();
		if (createdAccessToken == null) {

			map.put("errortype", "Forbidden");
			map.put("status", "402");
			map.put("message", "Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.");

			return map;
		}
		//기존에 존재하는 accessToken 제거

		map.put("status", "200");
		map.put("message", "Refresh 토큰을 통한 Access Token 생성이 완료되었습니다.");
		map.put("accessToken", createdAccessToken);

		return map;

	}
}
