package com.ddalggak.finalproject.global.jwt.token.repository;

import org.springframework.data.repository.CrudRepository;

import com.ddalggak.finalproject.global.jwt.token.entity.Token;

public interface TokenRepository extends CrudRepository<Token, String> {
}
