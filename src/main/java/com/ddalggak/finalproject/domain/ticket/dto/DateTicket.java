package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DateTicket {
	private LocalDate date;
	private long completedTicket;

	@QueryProjection
	public DateTicket(LocalDate date, long completedTicket) {
		this.date = date;
		this.completedTicket = completedTicket;
	}
}
