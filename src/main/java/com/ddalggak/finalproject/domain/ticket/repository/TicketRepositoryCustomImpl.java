package com.ddalggak.finalproject.domain.ticket.repository;

import static com.ddalggak.finalproject.domain.comment.entity.QComment.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

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
		return new TicketResponseDto(result);
	}

	//todo 2점에서 1점으로 바꿀때 task의 토탈 점수 어떻게 db에 인서트해야함?
	// @Override //todo @DynamicUpdate 단점 조사해서 비교하고 update문 수정
	// public void update(Long ticketId, TicketRequestDto ticketRequestDto) {
	// 	queryFactory
	// 		.update(task)
	// 		.set(task.totalDifficulty, )
	//
	// 		queryFactory
	// 			.update(ticket)
	// 			.set(ticket.ticketTitle, ticketRequestDto.getTicketTitle())
	// 			.set(ticket.ticketDescription, ticketRequestDto.getTicketDescription())
	// 			.set(ticket.priority, ticketRequestDto.getPriority())
	// 			.set(ticket.difficulty, ticketRequestDto.getDifficulty())
	// 			.set(ticket.expiredAt, ticketRequestDto.getTicketExpiredAt())
	// 			.where(ticket.ticketId.eq(ticketId))
	// 			.execute();
	//
	// }

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