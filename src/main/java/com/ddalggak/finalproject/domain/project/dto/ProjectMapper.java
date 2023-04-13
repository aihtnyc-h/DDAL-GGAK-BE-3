package com.ddalggak.finalproject.domain.project.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ddalggak.finalproject.domain.project.entity.Project;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.dto.TaskMapper;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;

@Mapper(componentModel = "spring", uses = {TaskMapper.class, UserMapper.class})
public interface ProjectMapper {

	/*
	 * queryDsl에서 constructor Projection 사용 안하고 project 다 땡겨올 시 사용하는 용도
	 */
	@Mappings({
		@Mapping(target = "id", source = "entity.project.projectId"),
		@Mapping(target = "thumbnail", source = "entity.project.thumbnail"),
		@Mapping(target = "projectTitle", source = "entity.project.projectTitle")
	})
	ProjectBriefResponseDto toBriefDtoWithProjectUser(ProjectUser entity);

	@Mappings({
		@Mapping(target = "projectTitle", source = "entity.projectTitle"),
		@Mapping(target = "thumbnail", source = "entity.thumbnail"),
		@Mapping(target = "projectLeader", source = "entity.projectLeader"),
		@Mapping(target = "participants", source = "entity.projectUserList"),
		@Mapping(target = "tasks", source = "entity.taskList")
	})
	ProjectResponseDto toDto(Project entity);
}
