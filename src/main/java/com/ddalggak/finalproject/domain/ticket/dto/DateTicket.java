package com.ddalggak.finalproject.domain.ticket.dto;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DateTicket {
	private LocalDate date;
	private Long completedTicket;

	@QueryProjection
	public DateTicket(LocalDate date, Long completedTicket) {
		this.date = date;
		this.completedTicket = completedTicket;
	}

	public DateTicket(String date, Long completedTicket) {
		this.date = LocalDate.parse(date);
		this.completedTicket = completedTicket;
	}
}
