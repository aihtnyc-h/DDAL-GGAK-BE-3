package com.ddalggak.finalproject.global.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ddalggak.finalproject.domain.user.entity.User;

import lombok.Getter;

@Getter
public class UserDetailsImpl implements UserDetails {

	private final User user;
	private final String email;
	private final String password;

	public UserDetailsImpl(User user, String email, String password) {
		this.user = user;
		this.email = email;
		this.password = password;
	}

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
}
