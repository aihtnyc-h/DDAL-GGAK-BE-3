package com.ddalggak.finalproject.domain.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
	// Ticket findAllBytaskIdByticketId( Long taskId, Long ticketId);

	// List<Ticket> findAllByTicketOrderByCratedAtDesc(Ticket ticket);

	// List<Ticket> findAllByOrderByModifiedAtDesc();
}
