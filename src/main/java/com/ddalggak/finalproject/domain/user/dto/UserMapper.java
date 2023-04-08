package com.ddalggak.finalproject.domain.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.mysql.cj.util.StringUtils;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mappings({
		@Mapping(target = "id", source = "taskUser.user.userId"),
		@Mapping(target = "email", source = "taskUser.user.email"),
		@Mapping(target = "nickname", source = "taskUser.user.nickname"),
		@Mapping(target = "thumbnail", source = "taskUser.user.profile"),
		@Mapping(target = "role", expression = "java(isLeaderOfTask(taskUser))")
	})
	UserResponseDto toUserResponseDtoWithTaskUser(TaskUser taskUser);

	default UserResponseDto toUserResponseDtoWithLabel(LabelUser entity) {
		return UserResponseDto.builder()
			.id(entity.getUser().getUserId())
			.email(entity.getUser().getEmail())
			.nickname(entity.getUser().getNickname())
			.thumbnail(entity.getUser().getProfile())
			.role(StringUtils.isNullOrEmpty(entity.getLabel().getLabelLeader()) ? "MEMBER" :
				entity.getLabel().getLabelLeader().equals(entity.getUser().getEmail()) ? "LEADER" : "MEMBER")
			.build();
	}

	default String isLeaderOfTask(TaskUser taskUser) {
		return StringUtils.isNullOrEmpty(taskUser.getTask().getTaskLeader()) ? "MEMBER" :
			taskUser.getTask().getTaskLeader().equals(taskUser.getUser().getEmail()) ? "LEADER" : "MEMBER";
	}
}
