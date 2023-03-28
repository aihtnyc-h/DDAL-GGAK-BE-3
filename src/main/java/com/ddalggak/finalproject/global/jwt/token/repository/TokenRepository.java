package com.ddalggak.finalproject.global.jwt.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddalggak.finalproject.global.jwt.token.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
	Token findByEmail(String email);
}
