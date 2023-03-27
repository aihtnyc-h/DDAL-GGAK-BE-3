package com.ddalggak.finalproject.domain.project.dto;

import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProjectUserRequestDto {

	@Schema(name = "project entity", description = "프로젝트 정보가 들어있는 엔티티")
	public Project project;

	@Schema(name = "user entity", defaultValue = "anonymous")
	public User user;

	public static ProjectUserRequestDto create(User user) {
		return ProjectUserRequestDto.builder()
			.user(user)
			.build();
	}
}
