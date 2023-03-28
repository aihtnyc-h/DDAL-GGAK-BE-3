package com.ddalggak.finalproject.global.jwt.token.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
// @RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 30)
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tokenId;
	private String email;
	private String accessToken;
	private String refreshToken;

	@Builder
	public Token(Long tokenId, String email, String accessToken, String refreshToken) {
		this.tokenId = tokenId;
		this.email = email;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}