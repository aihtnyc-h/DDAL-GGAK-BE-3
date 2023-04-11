package com.ddalggak.finalproject.global.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ddalggak.finalproject.domain.oauth.entity.ProviderType;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.role.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Slf4j
@ToString
public class UserDetailsImpl implements UserDetails, OAuth2User, OidcUser {

	private static final long serialVersionUID = 1L;
	private final User user;
	private final String email;
	private final String nickname;
	private final String password;
	private final ProviderType providerType;
	private final UserRole role;
	private final Collection<GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	public User getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// UserRole role = user.getRole();
		// String authority = role.getAuthority();
		//
		// SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		// Collection<GrantedAuthority> authorities = new ArrayList<>();
		// authorities.add(simpleGrantedAuthority);
		//
		return authorities;
	}

	@Override
	public String getName() {
		return this.email;
	}

	@Override
	public Map<String, Object> getClaims() {
		return null;
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return null;
	}

	@Override
	public OidcIdToken getIdToken() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public static UserDetailsImpl create(User user) {
		return UserDetailsImpl.builder()
			.user(user)
			.email(user.getEmail())
			.password(user.getPassword())
			.providerType(user.getProviderType())
			.role(UserRole.USER)
			.authorities(Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.toString())))
			.build();
	}

	public static UserDetailsImpl create(User user, Map<String, Object> attributes) {
		return UserDetailsImpl.builder()
			.user(user)
			.email(user.getEmail())
			.password(user.getPassword())
			.providerType(user.getProviderType())
			.role(UserRole.USER)
			.authorities(Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.toString())))
			.attributes(attributes)
			.build();
	}
}
