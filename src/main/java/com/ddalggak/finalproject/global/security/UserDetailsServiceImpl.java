package com.ddalggak.finalproject.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	// 회원 탈퇴시 캐시 삭제 적용해주어야 한다.
	@Override
	// @Cacheable(value = CacheKey.USER, key = "#email", unless = "#result == null", cacheManager = "cacheManager")
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("Cannot find User"));
		return UserDetailsImpl.create(user);
	}

}
