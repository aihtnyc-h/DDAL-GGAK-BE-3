package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;

import lombok.Data;

@Data
public class TicketSearchCondition {
	//날짜, 티켓status

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	private TicketStatus status;

}
