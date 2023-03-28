package com.ddalggak.finalproject.domain.user.dto;

import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponseDto {

	public Long id;

	public String email;
	public String nickname;

	public String thumbnail;

	public UserResponseDto(ProjectUser projectUser) {
		this.id = projectUser.getUser().getUserId();
		this.email = projectUser.getUser().getEmail();
		this.nickname = projectUser.getUser().getNickname();
		this.thumbnail = projectUser.getUser().getProfile();
	}

	public UserResponseDto(TaskUser taskUser) {
		this.id = taskUser.getUser().getUserId();
		this.email = taskUser.getUser().getEmail();
		this.nickname = taskUser.getUser().getNickname();
		this.thumbnail = taskUser.getUser().getProfile();
	}

	public static UserResponseDto of(ProjectUser projectUser) {
		return new UserResponseDto(projectUser);
	}

	public static UserResponseDto of(TaskUser taskUser) {
		return new UserResponseDto(taskUser);
	}
}
