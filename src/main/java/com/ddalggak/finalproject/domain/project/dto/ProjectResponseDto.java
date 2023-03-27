package com.ddalggak.finalproject.domain.project.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.task.dto.TaskBriefResponseDto;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectResponseDto {

	@Schema(name = "프로젝트 이름")
	public String projectTitle;
	@Schema(name = "프로젝트 썸네일", example = "http://ddalggak.ap-northeast-1.amazonaws.com/thumbnail/projects/~.jpg")
	public String thumbnail;
	@Schema(name = "프로젝트 리더")
	public String projectLeader;
	@Schema(name = "프로젝트 내 참여자 정보")
	public List<UserResponseDto> participants;
	@Schema(name = "프로젝트 내 task 간단 정보")
	public List<TaskBriefResponseDto> tasks;

	public static ProjectResponseDto of(Project project) {
		return ProjectResponseDto.builder()
			.projectTitle(project.getProjectTitle())
			.thumbnail(project.getThumbnail())
			.projectLeader(project.getCreatedBy())
			.participants(project.getProjectUserList().stream().map(UserResponseDto::new).collect(Collectors.toList()))
			.tasks(project.getTaskList().stream().map(TaskBriefResponseDto::new).collect(Collectors.toList()))
			.build();
	}
}
