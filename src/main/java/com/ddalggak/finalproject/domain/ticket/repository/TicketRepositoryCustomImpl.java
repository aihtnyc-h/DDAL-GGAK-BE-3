package com.ddalggak.finalproject.domain.ticket.repository;

import static com.ddalggak.finalproject.domain.comment.entity.QComment.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import com.ddalggak.finalproject.domain.ticket.dto.TicketMapper;
import com.ddalggak.finalproject.domain.ticket.dto.TicketRequestDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.global.error.CustomException;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final TicketMapper ticketMapper;

	@Override
	public TicketResponseDto findWithOrderedComments(Long ticketId) {
		Ticket result = queryFactory
			.selectFrom(ticket)
			.leftJoin(ticket.comment, comment1).fetchJoin()
			.where(ticket.ticketId.eq(ticketId))
			.orderBy(comment1.modifiedAt.desc())
			.fetchOne();
		if (result == null) {
			throw new CustomException(ErrorCode.TICKET_NOT_FOUND);
		}
		return ticketMapper.toDto(result);
	}

	private BooleanExpression isTicketPriorityChanged(TicketRequestDto ticketRequestDto) {
		return ticket.priority.eq(ticketRequestDto.getPriority());
	}

	private BooleanExpression isTicketDifficultyChanged(TicketRequestDto ticketRequestDto) {
		return ticket.difficulty.eq(ticketRequestDto.getDifficulty());
	}

	private BooleanExpression isTicketExpiredAtChanged(TicketRequestDto ticketRequestDto) {
		return ticket.expiredAt.eq(ticketRequestDto.getTicketExpiredAt());
	}

	private BooleanExpression isTicketTitleChanged(TicketRequestDto ticketRequestDto) {
		return ticket.ticketTitle.eq(ticketRequestDto.getTicketTitle());
	}

	private BooleanExpression isTicketDescriptionChanged(TicketRequestDto ticketRequestDto) {
		return ticket.ticketDescription.eq(ticketRequestDto.getTicketDescription());
	}

}