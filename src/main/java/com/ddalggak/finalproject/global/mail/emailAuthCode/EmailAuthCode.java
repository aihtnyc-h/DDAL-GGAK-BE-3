package com.ddalggak.finalproject.global.mail.emailAuthCode;

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
@RedisHash(value = "emailAuthCode", timeToLive = 60 * 5 * 1000L)
public class EmailAuthCode {
	@Id
	private String email;
	private String emailAuthCode;
}
