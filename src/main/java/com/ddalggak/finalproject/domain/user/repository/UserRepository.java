package com.ddalggak.finalproject.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddalggak.finalproject.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);
}
