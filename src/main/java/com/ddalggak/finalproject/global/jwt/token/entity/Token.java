package com.ddalggak.finalproject.global.jwt.token.entity;

import javax.persistence.Column;

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
@RedisHash(value = "token", timeToLive = 60 * 60 * 2 * 1000L)
public class Token {
	@Id
	private String email;
	private String accessToken;
	private String refreshToken;
}