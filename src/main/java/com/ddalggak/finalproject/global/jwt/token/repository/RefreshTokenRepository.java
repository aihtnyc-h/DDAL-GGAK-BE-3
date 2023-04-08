package com.ddalggak.finalproject.global.jwt.token.repository;

import org.springframework.data.repository.CrudRepository;

import com.ddalggak.finalproject.global.jwt.token.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
