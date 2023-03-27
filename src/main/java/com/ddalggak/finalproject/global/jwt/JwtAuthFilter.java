package com.ddalggak.finalproject.global.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException, IOException {

		String token = jwtUtil.resolveToken(request);

		if (token != null) {
			if (!jwtUtil.validateToken(token)) {
				jwtExceptionHandler(response, ErrorCode.INVALID_AUTH_TOKEN);
				return;
			}
			Claims info = jwtUtil.getUserInfo(token);
			setAuthentication(info.getSubject());
		}
		filterChain.doFilter(request, response);
	}

	public void setAuthentication(String email) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = jwtUtil.createAuthentication(email);
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}

	public void jwtExceptionHandler(HttpServletResponse response, ErrorCode errorCode) {
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType("application/json");
		try {
			String json = new ObjectMapper().writeValueAsString(ErrorResponse.from(errorCode, errorCode.getMessage()));
			response.getWriter().write(json);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
