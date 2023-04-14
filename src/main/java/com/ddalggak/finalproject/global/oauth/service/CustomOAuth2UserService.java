package com.ddalggak.finalproject.global.oauth.service;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.oauth.OAuth2UserInfoFactory;
import com.ddalggak.finalproject.global.oauth.dto.OAuth2UserInfo;
import com.ddalggak.finalproject.global.oauth.entity.ProviderType;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest);

		try {
			return this.process(userRequest, user);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
		ProviderType providerType = ProviderType.valueOf(
			userRequest.getClientRegistration().getRegistrationId().toUpperCase());

		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oAuth2User.getAttributes());
		String[] parts = userInfo.getEmail().split("@");
		String nickname = parts[0];

		User user = userRepository.findByEmail(userInfo.getEmail())
			.orElseGet(() -> User.builder()
				.email(userInfo.getEmail())
				.nickname(nickname)
				.profile(userInfo.getImageUrl())
				.role(UserRole.USER)
				.providerType(providerType)
				.build());
		userRepository.save(user);
		if (user.getProviderType() == null) {
			User.updateOAuth(providerType, userInfo.getImageUrl());
		}

		return UserDetailsImpl.create(user, oAuth2User.getAttributes());
	}
}