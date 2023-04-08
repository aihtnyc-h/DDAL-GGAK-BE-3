package com.ddalggak.finalproject.global.jwt.token.repository;

import org.springframework.data.repository.CrudRepository;

import com.ddalggak.finalproject.global.jwt.token.entity.AccessToken;

public interface AccessTokenRepository extends CrudRepository<AccessToken, String> {
}
