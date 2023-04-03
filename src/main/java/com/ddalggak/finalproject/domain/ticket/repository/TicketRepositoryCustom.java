package com.ddalggak.finalproject.domain.ticket.repository;

import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;

public interface TicketRepositoryCustom {
	TicketResponseDto findWithOrderedComments(Long ticketId);

	// void update(Long ticketId, TicketRequestDto ticketRequestDto);
}