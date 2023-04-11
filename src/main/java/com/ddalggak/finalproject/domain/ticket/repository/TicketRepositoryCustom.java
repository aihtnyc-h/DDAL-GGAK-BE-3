package com.ddalggak.finalproject.domain.ticket.repository;

import java.util.List;
import java.util.Optional;

import com.ddalggak.finalproject.domain.ticket.entity.Ticket;

public interface TicketRepositoryCustom {
	Optional<Ticket> findWithOrderedComments(Long ticketId);

	List<Ticket> findWithTaskId(Long taskId);
	// void update(Long ticketId, TicketRequestDto ticketRequestDto);

	List<Ticket> findWithLabelId(Long labelId);

}
