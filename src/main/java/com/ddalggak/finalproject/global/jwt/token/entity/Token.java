package com.ddalggak.finalproject.global.jwt.token.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 14 * 1000L)
public class Token {
	@Id
	private String email;
	private String refreshToken;

	public static void update(Token token, String refreshToken) {
		builder()
			.email(token.getEmail())
			.refreshToken(refreshToken)
			.build();
	}
}