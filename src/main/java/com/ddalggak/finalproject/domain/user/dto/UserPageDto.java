package com.ddalggak.finalproject.domain.user.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
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
	public List<ProjectBriefResponseDto> projects;

	@Builder
	public UserPageDto(User user) {
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.profile = user.getProfile();
		this.projects = user.getProjectUserList()
			.stream()
			.map(ProjectBriefResponseDto::of).collect(Collectors.toList());
	}
}
