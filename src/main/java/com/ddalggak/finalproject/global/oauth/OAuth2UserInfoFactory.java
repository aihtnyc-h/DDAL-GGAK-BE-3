package com.ddalggak.finalproject.global.oauth;

import java.util.Map;

import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.oauth.dto.GoogleOAuth2UserInfo;
import com.ddalggak.finalproject.global.oauth.dto.OAuth2UserInfo;
import com.ddalggak.finalproject.global.oauth.entity.ProviderType;
import com.ddalggak.finalproject.global.oauth.exception.OAuthException;

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
