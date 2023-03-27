package com.ddalggak.finalproject.domain.task.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class TaskResponseDto {

	@Schema(name = "task id", example = "1")
	public Long id;

	@Schema(name = "task title", example = "task title")
	public String taskTitle;

	@Schema(name = "task leader", example = "task leader")
	public String taskLeader;

	@Schema(name = "when does this project expired at", example = "2023-03-22")
	public LocalDate expiredAt;

	@Schema(name = "total difficulty", example = "10", defaultValue = "0")
	public int totalDifficulty;

	@Schema(name = "total priority", example = "10", defaultValue = "0")
	public int totalPriority;
	@Schema(name = "total tickets")
	public List<TicketResponseDto> tickets;

	@Builder
	public TaskResponseDto(Task task) {
		id = task.getTaskId();
		taskTitle = task.getTaskTitle();
		taskLeader = task.getTaskLeader();
		expiredAt = task.getExpiredAt();
		totalDifficulty = task.getTotalDifficulty();
		totalPriority = task.getTotalPriority();
		tickets = task.getTicketList().stream().map(TicketResponseDto::of).collect(Collectors.toList());
	}

	// todo global response Dto 만들어서 전부 data에 넣기
	public static ResponseEntity<TaskResponseDto> toResponseEntity(Task task) {
		return ResponseEntity
			.status(200)
			.body(TaskResponseDto.builder()
				.task(task)
				.build()
			);
	}
}
