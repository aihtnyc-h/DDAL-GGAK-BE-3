package com.ddalggak.finalproject.domain.task.dto;

import static com.ddalggak.finalproject.domain.ticket.entity.TicketStatus.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
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

	@Schema(name = "when does this task created at", example = "2023-03-22")
	@JsonView(Views.Task.class)
	public LocalDate createdAt;

	@Schema(name = "when does this task expired at", example = "2023-03-22")
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
}
