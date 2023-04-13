package com.ddalggak.finalproject.domain.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.mysql.cj.util.StringUtils;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mappings({
		@Mapping(target = "id", source = "entity.user.userId"),
		@Mapping(target = "email", source = "entity.user.email"),
		@Mapping(target = "nickname", source = "entity.user.nickname"),
		@Mapping(target = "thumbnail", source = "entity.user.profile"),
		@Mapping(target = "role", expression = "java(isLeaderOfTask(entity))")
	})
	UserResponseDto toUserResponseDtoWithTaskUser(TaskUser entity);

	@Mappings({
		@Mapping(target = "id", source = "entity.user.userId"),
		@Mapping(target = "email", source = "entity.user.email"),
		@Mapping(target = "nickname", source = "entity.user.nickname"),
		@Mapping(target = "thumbnail", source = "entity.user.profile"),
		@Mapping(target = "role", expression = "java(isLeaderOfProject(entity))")
	})
	UserResponseDto toUserResponseDtoWithProjectUser(ProjectUser entity);

	@Mappings({
		@Mapping(target = "userId", source = "entity.userId"),
		@Mapping(target = "email", source = "entity.email"),
		@Mapping(target = "nickname", source = "entity.nickname"),
		@Mapping(target = "profile", source = "entity.profile"),
	})
	UserPageDto toUserPageDto(User entity);

	@Mappings({
		@Mapping(target = "id", source = "entity.user.userId"),
		@Mapping(target = "email", source = "entity.user.email"),
		@Mapping(target = "nickname", source = "entity.user.nickname"),
		@Mapping(target = "thumbnail", source = "entity.user.profile"),
		@Mapping(target = "role", expression = "java(isLeaderOfLabel(entity))")
	})
	UserResponseDto toUserResponseDtoWithLabel(LabelUser entity);

	/*
	 * queryDsl에서 constructor Projection 사용 안하고 project 다 땡겨올 시 사용하는 용도
	 */
	@Mappings({
		@Mapping(target = "id", source = "entity.project.projectId"),
		@Mapping(target = "thumbnail", source = "entity.project.thumbnail"),
		@Mapping(target = "projectTitle", source = "entity.project.projectTitle")
	})
	ProjectBriefResponseDto toBriefDtoWithProjectUser(ProjectUser entity);

	default String isLeaderOfTask(TaskUser taskUser) {
		return StringUtils.isNullOrEmpty(taskUser.getTask().getTaskLeader()) ? "MEMBER" :
			taskUser.getTask().getTaskLeader().equals(taskUser.getUser().getEmail()) ? "LEADER" : "MEMBER";
	}

	default String isLeaderOfProject(ProjectUser projectUser) {
		return projectUser.getProject().getProjectLeader().equals(projectUser.getUser().getEmail()) ? "LEADER" :
			"MEMBER";
	}

	default String isLeaderOfLabel(LabelUser labelUser) {
		return StringUtils.isNullOrEmpty(labelUser.getLabel().getLabelLeader()) ? "MEMBER" :
			labelUser.getLabel().getLabelLeader().equals(labelUser.getUser().getEmail()) ? "LEADER" : "MEMBER";
	}
}
