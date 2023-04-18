package com.ddalggak.finalproject.domain.ticket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;

public interface TicketRepositoryCustom {
	Optional<Ticket> findWithOrderedComments(Long ticketId);

	List<Ticket> findWithTaskId(Long taskId);
	// void update(Long ticketId, TicketRequestDto ticketRequestDto);

	List<Ticket> findWithLabelId(Long labelId);

	List<DateTicket> getCompletedTicketCountByDate(TicketSearchCondition condition, Long userId);

	List<Ticket> getTicketByUserId(Long userId);

	Slice<TicketResponseDto> getSlicedTicketCountByDate(TicketSearchCondition condition, Pageable pageable,
		Long userId);

	List<Ticket> findByStatusAndTaskId(TicketStatus status, Long taskId);

}
