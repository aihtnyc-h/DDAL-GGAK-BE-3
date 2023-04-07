package com.ddalggak.finalproject.domain.user.dto;

import java.util.List;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserTicketResponseDto {

	private List<DateTicket> dateTickets;

	@QueryProjection
	public UserTicketResponseDto(List<DateTicket> dateTickets, UserResponseDto user) {
		this.dateTickets = dateTickets;
	}
}
