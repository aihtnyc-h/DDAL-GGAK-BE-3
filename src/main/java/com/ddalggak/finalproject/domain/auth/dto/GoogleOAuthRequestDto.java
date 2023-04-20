package com.ddalggak.finalproject.domain.auth.dto;

import lombok.Getter;

public class GoogleOAuthRequestDto {

	@Getter
	public static class Token {

		private String access_token;
		private Integer expires_in;
		private String refresh_token;
		private String scope;
		private String token_type;

	}

	@Getter
	public static class GoogleUser {

		private String id;
		private String email;
		private Boolean verifiedEmail;
		private String name;
		private String givenName;
		private String familyName;
		private String picture;
		private String locale;

	}

}