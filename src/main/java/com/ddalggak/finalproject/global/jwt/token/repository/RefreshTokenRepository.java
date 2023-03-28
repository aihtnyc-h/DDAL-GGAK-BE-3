package com.ddalggak.finalproject.global.jwt.token.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ddalggak.finalproject.global.jwt.token.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
	boolean existsByEmail(String loginUserEmail);

	void deleteByEmail(String loginUserEmail);

	Optional<RefreshToken> findByRefreshToken(String refreshToken);
}