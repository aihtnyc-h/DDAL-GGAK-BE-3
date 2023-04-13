package com.ddalggak.finalproject.domain.task.dto;

import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TaskUserRequestDto {

	@Schema(name = "task", description = "task entity")
	public Task task;

	@Schema(name = "user", defaultValue = "anonymous")
	public User user;

	public static TaskUserRequestDto create(User user) {
		return TaskUserRequestDto.builder()
			.user(user)
			.build();
	}
}