package com.ddalggak.finalproject.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProjectBriefResponseDto {

	@Schema(name = "프로젝트 id", example = "1")
	public Long id;

	@Schema(name = "프로젝트 썸네일", example = "http://ddalggak.ap-northeast-1.amazonaws.com/thumbnail/projects/~.jpg")
	public String thumbnail;

	@Schema(name = "프로젝트 이름")
	public String projectTitle;
}