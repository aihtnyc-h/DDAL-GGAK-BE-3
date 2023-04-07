package com.ddalggak.finalproject.domain.user.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.label.entity.QLabelUser.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.QDateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketResponseDto;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	//날짜 받아서 그때로부터 1년전까지 완료된 티켓 개수 가져옴. 확장성을 위해 SearchCondition으로 적용함
	@Override
	public List<DateTicket> getCompletedTicketCountByDate(TicketSearchCondition condition, Long userId) {
		return queryFactory
			.select(new QDateTicket(
				ticket.completedAt.as("date"),
				ticket.count().as("completedTicket")
			))
			.from(ticket)
			.where(ticket.user.userId.eq(userId),
				getWithOneYear(condition.getDate())
			)
			.groupBy(ticket.completedAt)
			.orderBy(ticket.completedAt.asc())
			.fetch();
	}

	@Override
	public Map<TicketStatus, List<TicketResponseDto>> getTicketByUserId(TicketSearchCondition condition, Long userId) {
		return queryFactory
			.selectFrom(ticket)
			.where(ticket.user.userId.eq(userId),
				statusEq(condition.getStatus())
			)
			.orderBy(ticket.createdAt.desc())
			.fetch()
			.stream()
			.map(TicketResponseDto::new)
			.collect(Collectors.groupingBy(TicketResponseDto::getStatus));

	}

	@Override
	public List<UserResponseDto> getUserFromLabel(Long labelId) {
		List<LabelUser> labelUserList = queryFactory
			.selectFrom(labelUser)
			.join(labelUser.label, label)
			.where(label.labelId.eq(labelId))
			.fetch();
		return labelUserList.stream().map(UserResponseDto::new).collect(Collectors.toList());
	}

	private BooleanExpression getWithOneYear(LocalDate localDate) {
		return localDate != null ? ticket.completedAt.between(localDate.minusYears(1), localDate) : null;
	}

	private BooleanExpression statusEq(TicketStatus status) {
		return status != null ? ticket.status.eq(status) : null;
	}

}
