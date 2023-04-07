package com.ddalggak.finalproject.domain.task.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskBriefResponseDto { // task 간단 요약제공

	@Schema(name = "task id", example = "1")
	public Long id;

	@Schema(name = "task title", example = "task title")
	public String taskTitle;
	@Schema(name = "when does this project create at", example = "2023-03-22")
	public LocalDate createAt;

	@Schema(name = "when does this project expired at", example = "2023-03-22")
	public LocalDate expiredAt;

	@Schema(name = "number of completed tickets", example = "10", defaultValue = "0")
	public int completedTickets;

	@Schema(name = "number of total tickets", example = "10", defaultValue = "0")
	public int totalTickets;

	@Schema(name = "number of participants", example = "10", defaultValue = "1")
	public int participantsCount;

	@Schema(name = "list of participants, when number of participants exceeds 3, it will be shown 3 people order by email desc")
	public List<UserResponseDto> participants;

	@Builder
	public TaskBriefResponseDto(Task task) {
		id = task.getTaskId();
		taskTitle = task.getTaskTitle();
		expiredAt = task.getExpiredAt();
		createAt = LocalDate.from(task.getCreatedAt());
		// 	completedTickets = task.getCompletedTickets();
		totalTickets = task.getTicketList().size();
		participantsCount = task.getTaskUserList().size();
		participants = task.getTaskUserList().stream().map(UserResponseDto::new).collect(Collectors.toList());
	}

}
