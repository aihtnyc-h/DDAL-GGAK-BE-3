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
	@Schema(name = "프로젝트 이름")
	public String projectTitle;
	@Schema(name = "프로젝트 썸네일", example = "http://ddalggak.ap-northeast-1.amazonaws.com/thumbnail/projects/~.jpg")
	public String thumbnail;

	@QueryProjection
	public ProjectBriefResponseDto(Long id, String projectTitle, String thumbnail) {
		this.id = id;
		this.projectTitle = projectTitle;
		this.thumbnail = thumbnail;
	}

	@Builder
	public ProjectBriefResponseDto(Project project) {
		id = project.getProjectId();
		projectTitle = project.getProjectTitle();
		thumbnail = project.getThumbnail();
	}

	public static ProjectBriefResponseDto of(ProjectUser projectUser) {
		return ProjectBriefResponseDto.builder()
			.project(projectUser.getProject())
			.build();
	}
}
