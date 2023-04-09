package com.ddalggak.finalproject.global.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.jwt.token.service.TokenService;
import com.ddalggak.finalproject.global.security.exception.SecurityExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final TokenService tokenService;

	@SneakyThrows
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException, IOException {
		String token = jwtUtil.resolveToken(request);

		if (token != null) {
			if (!jwtUtil.validateToken(token) || !tokenService.checkAccessToken(request)) {
				jwtExceptionHandler(response, ErrorCode.INVALID_AUTH_TOKEN);
				return;
			}
			if (jwtUtil.isAccessTokenAboutToExpire(token)) {
				tokenService.getAccessToken(request, response);
			}
			if (jwtUtil.isExpired(token)) {
				int httpStatus = 1002;
				String message = "만료된 토큰입니다.";
				String errorCode = "EXPIRED_TOKEN";
				setResponse(response, errorCode, httpStatus, message);
				return;
			}
			Claims info = jwtUtil.getUserInfo(token);
			setAuthentication(info.getSubject());
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * 한글 출력을 위해 getWriter() 사용
	 */
	private void setResponse(HttpServletResponse response, String errorCode, int httpStatus, String message) throws
		IOException {
		response.setStatus(httpStatus);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().println("{ \"message\" : \"" + message
			+ "\", \"code\" : \"" + errorCode
			+ "\", \"status\" : " + httpStatus + "}");
	}

	public void setAuthentication(String email) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = jwtUtil.createAuthentication(email);
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}

	public void jwtExceptionHandler(HttpServletResponse response, ErrorCode errorCode) {
		HttpStatus httpStatus = errorCode.getHttpStatus();
		String message = errorCode.getMessage();

		response.setStatus(httpStatus.value());
		response.setContentType("application/json");
		try {
			String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(httpStatus.value(), message));
			response.getWriter().write(json);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
