package com.ddalggak.finalproject.domain.project.dto;

import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectBriefResponseDto {

	@Schema(name = "프로젝트 id", example = "1")
	public Long id;

	@Schema(name = "프로젝트 썸네일", example = "http://ddalggak.ap-northeast-1.amazonaws.com/thumbnail/projects/~.jpg")
	public String thumbnail;

	@Schema(name = "프로젝트 이름")
	public String projectTitle;

	@QueryProjection
	public ProjectBriefResponseDto(Long id, String thumbnail, String projectTitle) {
		this.id = id;
		this.thumbnail = thumbnail;
		this.projectTitle = projectTitle;
	}

	@Builder
	public ProjectBriefResponseDto(Project project) {
		id = project.getProjectId();
		thumbnail = project.getThumbnail();
		projectTitle = project.getProjectTitle();
	}

	public static ProjectBriefResponseDto of(ProjectUser projectUser) {
		return ProjectBriefResponseDto.builder()
			.project(projectUser.getProject())
			.build();
	}
}
