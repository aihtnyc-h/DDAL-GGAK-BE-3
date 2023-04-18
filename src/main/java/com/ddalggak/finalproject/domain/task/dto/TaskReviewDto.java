package com.ddalggak.finalproject.domain.task.dto;

import java.util.List;

import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskReviewDto {
	public List<LabelResponseDto> labelLeaders;

	public List<TicketResponseDto> tickets;
}
