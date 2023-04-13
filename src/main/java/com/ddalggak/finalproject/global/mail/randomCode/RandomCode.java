package com.ddalggak.finalproject.global.mail.randomCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RedisHash(value = "refreshToken", timeToLive = 60 * 5 * 1000L)
public class RandomCode {
	@Id
	private String email;
	private String randomCode;
}
