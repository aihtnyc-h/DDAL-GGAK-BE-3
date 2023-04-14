package com.ddalggak.finalproject.domain.task.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

	@Schema(name = "task id", example = "1")
	public Long id;

	@Schema(name = "task title", example = "task title")
	public String taskTitle;

	@Schema(name = "task leader", example = "task leader")
	public String taskLeader;

	@Schema(name = "when does this task created at", example = "2023-03-22")
	public LocalDateTime createdAt;

	@Schema(name = "when does this task expired at", example = "2023-03-22")
	public LocalDate expiredAt;

	@Schema(name = "total difficulty", example = "10", defaultValue = "0")
	public int totalDifficulty;

	@Schema(name = "total priority", example = "10", defaultValue = "0")
	public int totalPriority;

	@Schema(name = "progress", example = "88.44")
	public double progress;

	@Schema(name = "how much this task is completed", example = "88.442266")
	public double completed;

	@Schema(name = "labels", example = "front-end", defaultValue = "null")
	@Setter
	public List<LabelResponseDto> labels;

	@Schema(name = "total tickets")
	@Setter
	public Map<TicketStatus, List<TicketResponseDto>> tickets = new LinkedHashMap<>();
}
