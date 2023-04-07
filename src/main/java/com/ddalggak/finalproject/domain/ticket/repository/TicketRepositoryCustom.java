package com.ddalggak.finalproject.domain.ticket.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;

public interface TicketRepositoryCustom {
	TicketResponseDto findWithOrderedComments(Long ticketId);

	List<TicketResponseDto> findWithTaskId(Long taskId);
	// void update(Long ticketId, TicketRequestDto ticketRequestDto);
}
