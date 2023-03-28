package com.ddalggak.finalproject.domain.user.dto;

import com.ddalggak.finalproject.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserPageDto {
	public Long userId;
	public String email;
	public String nickname;
	public String profile;

	@Builder
	public UserPageDto(User user) {
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.profile = user.getProfile();
	}
}
