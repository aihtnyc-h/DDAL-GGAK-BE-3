package com.ddalggak.finalproject.domain.oauth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.ddalggak.finalproject.domain.oauth.repository.CookieAuthorizationRequestRepository;
import com.ddalggak.finalproject.global.cookie.CookieUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		String targetUrl = CookieUtil.getCookie(request, "redirect_uri")
			.map(Cookie::getValue)
			.orElse("/");

		targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("error", exception.getLocalizedMessage())
			.build().toUriString();

		cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}