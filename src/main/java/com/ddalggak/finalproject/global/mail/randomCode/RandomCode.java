package com.ddalggak.finalproject.global.mail.randomCode;

import javax.persistence.Column;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 60 * 5 * 1000L)
public class RandomCode {
	@Id
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String randomCode;

	public RandomCode(String email, String randomCode) {
		this.email = email;
		this.randomCode = randomCode;
	}
}
