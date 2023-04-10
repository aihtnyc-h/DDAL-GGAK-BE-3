package com.ddalggak.finalproject.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

	private final String host;
	private final int port;
	private final String password;

	public RedisConfig(@Value("${spring.redis.host}") final String host, @Value("${spring.redis.port}") final int port,
		@Value("${spring.redis.password}") final String password) {
		this.host = host;
		this.port = port;
		this.password = password;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName(host);
		redisConfig.setPort(port);
		redisConfig.setPassword(password);
		return new LettuceConnectionFactory(redisConfig);
	}
}