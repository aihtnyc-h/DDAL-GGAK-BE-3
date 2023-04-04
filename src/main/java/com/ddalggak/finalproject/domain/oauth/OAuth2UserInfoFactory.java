package com.ddalggak.finalproject.domain.oauth;

import java.util.Map;

import com.ddalggak.finalproject.domain.oauth.dto.GoogleOAuth2UserInfo;
import com.ddalggak.finalproject.domain.oauth.dto.OAuth2UserInfo;
import com.ddalggak.finalproject.domain.oauth.entity.ProviderType;
import com.ddalggak.finalproject.domain.oauth.exception.OAuthException;
import com.ddalggak.finalproject.global.error.ErrorCode;

public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
		switch (providerType) {
			case GOOGLE:
				return new GoogleOAuth2UserInfo(attributes);
			default:
				throw new OAuthException(ErrorCode.INVALID_PROVIDER_TYPE);
		}
	}
}
