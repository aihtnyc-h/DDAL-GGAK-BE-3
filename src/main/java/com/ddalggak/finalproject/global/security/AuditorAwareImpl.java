package com.ddalggak.finalproject.global.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public class AuditorAwareImpl implements AuditorAware<String> {
	@Override
	public Optional<String> getCurrentAuditor() {
		String modifiedBy = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!StringUtils.hasText(modifiedBy)) {
			modifiedBy = "unknown";
		}
		return Optional.of(modifiedBy);
	}
}
