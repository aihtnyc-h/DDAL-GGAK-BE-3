package com.ddalggak.finalproject.domain.task.dto;

import static com.ddalggak.finalproject.domain.ticket.entity.TicketStatus.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.task.entity.Task;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.global.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

	@Schema(name = "task id", example = "1")
	@JsonView(Views.Task.class)
	public Long id;

	@Schema(name = "task title", example = "task title")
	@JsonView(Views.Task.class)
	public String taskTitle;

	@Schema(name = "task leader", example = "task leader")
	@JsonView(Views.Task.class)
	public String taskLeader;

	@Schema(name = "when does this project expired at", example = "2023-03-22")
	@JsonView(Views.Task.class)
	public LocalDate expiredAt;

	@Schema(name = "total difficulty", example = "10", defaultValue = "0")
	@JsonView(Views.Task.class)
	public int totalDifficulty;

	@Schema(name = "total priority", example = "10", defaultValue = "0")
	@JsonView(Views.Task.class)
	public int totalPriority;

	@Schema(name = "labels", example = "front-end", defaultValue = "null")
	@JsonView(Views.Task.class)
	@Setter
	public List<LabelResponseDto> labels;

	@Schema(name = "total tickets")
	@JsonView(Views.Task.class)
	@Setter
	public Map<TicketStatus, List<TicketResponseDto>> tickets = new HashMap<>() {
		{
			put(TODO, new ArrayList<>());
			put(IN_PROGRESS, new ArrayList<>());
			put(DONE, new ArrayList<>());
		}
	};

	public TaskResponseDto(Task task) {
		id = task.getTaskId();
		taskTitle = task.getTaskTitle();
		taskLeader = task.getTaskLeader();
		expiredAt = task.getExpiredAt();
		totalDifficulty = task.getTicketList().stream().mapToInt(Ticket::getDifficulty).sum();
		totalPriority = task.getTicketList().stream().mapToInt(Ticket::getPriority).sum();
		labels = task.getLabelList().stream().map(LabelResponseDto::of).collect(Collectors.toList());
		task.getTicketList()
			.stream()
			.map(TicketResponseDto::new)
			.forEach(ticket -> tickets.get(ticket.getStatus()).add(ticket));
	}

	// todo global response Dto 만들어서 전부 data에 넣기
	// public static ResponseEntity<TaskResponseDto> toResponseEntity(Task task) {
	// 	return ResponseEntity
	// 		.status(200)
	// 		.body(TaskResponseDto.builder()
	// 			.task(task)
	// 			.build()
	// 		);
	// }
}
