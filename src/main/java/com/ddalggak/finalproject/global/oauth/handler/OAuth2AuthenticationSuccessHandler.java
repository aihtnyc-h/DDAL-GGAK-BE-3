package com.ddalggak.finalproject.global.oauth.handler;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.ddalggak.finalproject.global.cookie.CookieUtil;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.jwt.token.entity.AccessToken;
import com.ddalggak.finalproject.global.jwt.token.entity.RefreshToken;
import com.ddalggak.finalproject.global.jwt.token.repository.AccessTokenRepository;
import com.ddalggak.finalproject.global.jwt.token.repository.RefreshTokenRepository;
import com.ddalggak.finalproject.global.oauth.OAuth2UserInfoFactory;
import com.ddalggak.finalproject.global.oauth.dto.OAuth2UserInfo;
import com.ddalggak.finalproject.global.oauth.entity.ProviderType;
import com.ddalggak.finalproject.global.oauth.repository.CookieAuthorizationRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${app.oauth2.authorizedRedirectUri}")
	private String redirectUri;
	private final JwtUtil jwtUtil;
	private final AccessTokenRepository accessTokenRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final CookieAuthorizationRequestRepository authorizationRequestRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		Optional<String> redirectUri = CookieUtil.getCookie(request, "redirectUri")
			.map(Cookie::getValue);

		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new IllegalArgumentException(
				"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken)authentication;
		ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

		OidcUser user = ((OidcUser)authentication.getPrincipal());
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

		String accessToken = jwtUtil.createAccessToken(authentication);
		String refreshToken = jwtUtil.createRefreshToken(authentication);

		Optional<AccessToken> existingAccessToken = accessTokenRepository.findById(userInfo.getEmail());
		if (existingAccessToken.isPresent()) {
			accessTokenRepository.delete(existingAccessToken.get());
			AccessToken token = AccessToken.builder()
				.email(userInfo.getEmail())
				.accessToken(accessToken)
				.build();
			accessTokenRepository.save(token);
		} else {
			AccessToken token = AccessToken.builder()
				.email(userInfo.getEmail())
				.accessToken(accessToken)
				.build();
			accessTokenRepository.save(token);
		}

		Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findById(userInfo.getEmail());
		if (existingRefreshToken.isPresent()) {
			refreshTokenRepository.delete(existingRefreshToken.get());
			RefreshToken token = RefreshToken.builder()
				.email(userInfo.getEmail())
				.refreshToken(refreshToken)
				.build();
			refreshTokenRepository.save(token);
		} else {
			RefreshToken token = RefreshToken.builder()
				.email(userInfo.getEmail())
				.refreshToken(refreshToken)
				.build();
			refreshTokenRepository.save(token);
		}

		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);

		return UriComponentsBuilder.fromUriString(targetUrl)
			.path("/login")            //프론트와 redirect url 정하기!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			.build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);

		URI authorizedURI = URI.create(redirectUri);
		return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
			&& authorizedURI.getPort() == clientRedirectUri.getPort();
	}
}