package com.ddalggak.finalproject.global.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = "unknown";
		if (authentication != null && authentication.isAuthenticated()) {
			username = authentication.getName();
		}
		return Optional.of(username);
	}
}
