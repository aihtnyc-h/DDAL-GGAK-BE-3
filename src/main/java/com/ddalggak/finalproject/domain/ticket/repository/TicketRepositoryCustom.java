package com.ddalggak.finalproject.domain.ticket.repository;

import java.util.List;
import java.util.Map;

import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;

public interface TicketRepositoryCustom {
	TicketResponseDto findWithOrderedComments(Long ticketId);

	List<TicketResponseDto> findWithTaskId(Long taskId);
	// void update(Long ticketId, TicketRequestDto ticketRequestDto);

	Map<TicketStatus, List<TicketResponseDto>> findWithLabelId(Long labelId);

}
