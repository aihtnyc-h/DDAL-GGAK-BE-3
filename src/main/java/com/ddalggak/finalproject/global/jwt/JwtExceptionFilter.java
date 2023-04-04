package com.ddalggak.finalproject.global.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ddalggak.finalproject.global.error.ErrorCode;

import io.jsonwebtoken.JwtException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws
		ServletException, IOException {
		try {
			chain.doFilter(req, res); // go to 'JwtAuthenticationFilter'
		} catch (JwtException ex) {
			setErrorResponse(res, ex);
		}
	}

	public void setErrorResponse(HttpServletResponse res, Throwable ex) throws IOException {
		res.setStatus(999);
		res.setContentType("application/json; charset=UTF-8");

		JwtExceptionResponse jwtExceptionResponse = new JwtExceptionResponse(ErrorCode.INVALID_AUTH_TOKEN);
		res.getWriter().write(jwtExceptionResponse.convertToJson());
	}
}