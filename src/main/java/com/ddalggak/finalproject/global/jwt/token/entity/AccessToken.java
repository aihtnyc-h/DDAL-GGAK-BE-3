package com.ddalggak.finalproject.global.jwt.token.entity;

import java.util.Date;

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
@RedisHash(value = "accessToken", timeToLive = 60 * 30 * 1000L)
public class AccessToken {
	@Id
	private String email;
	private String accessToken;
	private Date expirationTime;

}