package com.ddalggak.finalproject.domain.user.dto;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.mysql.cj.util.StringUtils;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto {

	public Long id;

	public String email;

	public String nickname;

	public String thumbnail;

	public String role;

	@QueryProjection
	public UserResponseDto(ProjectUser projectUser) {
		this.id = projectUser.getUser().getUserId();
		this.email = projectUser.getUser().getEmail();
		this.nickname = projectUser.getUser().getNickname();
		this.thumbnail = projectUser.getUser().getProfile();
		this.role =
			projectUser.getProject().getProjectLeader().equals(projectUser.getUser().getEmail()) ? "LEADER" : "MEMBER";
	}

	public UserResponseDto(TaskUser taskUser) {
		this.id = taskUser.getUser().getUserId();
		this.email = taskUser.getUser().getEmail();
		this.nickname = taskUser.getUser().getNickname();
		this.thumbnail = taskUser.getUser().getProfile();
		this.role = StringUtils.isNullOrEmpty(taskUser.getTask().getTaskLeader()) ? "MEMBER" :
			taskUser.getTask().getTaskLeader().equals(taskUser.getUser().getEmail()) ? "LEADER" : "MEMBER";
	}

	public UserResponseDto(LabelUser labelUser) {
		id = labelUser.getUser().getUserId();
		email = labelUser.getUser().getEmail();
		nickname = labelUser.getUser().getNickname();
		thumbnail = labelUser.getUser().getProfile();
		role = StringUtils.isNullOrEmpty(labelUser.getLabel().getLabelLeader()) ? "MEMBER" :
			labelUser.getLabel().getLabelLeader().equals(labelUser.getUser().getEmail()) ? "LEADER" : "MEMBER";
	}

	public static UserResponseDto of(ProjectUser projectUser) {
		return new UserResponseDto(projectUser);
	}

	public static UserResponseDto of(TaskUser taskUser) {
		return new UserResponseDto(taskUser);
	}
}
