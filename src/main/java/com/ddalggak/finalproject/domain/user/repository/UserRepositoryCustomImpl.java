package com.ddalggak.finalproject.domain.user.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.label.entity.QLabelUser.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.task.entity.QTaskUser.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;
import static com.ddalggak.finalproject.domain.user.entity.QUser.*;

import java.time.LocalDate;
import java.util.List;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.ticket.dto.DateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.QDateTicket;
import com.ddalggak.finalproject.domain.ticket.dto.TicketSearchCondition;
import com.ddalggak.finalproject.domain.ticket.entity.Ticket;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.user.entity.User;
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
	public List<Ticket> getTicketByUserId(TicketSearchCondition condition, Long userId) {
		return queryFactory
			.selectFrom(ticket)
			.where(ticket.user.userId.eq(userId),
				statusEq(condition.getStatus())
			)
			.orderBy(ticket.createdAt.desc())
			.fetch();

	}

	@Override
	public List<LabelUser> getUserFromLabelId(Long labelId) {
		return queryFactory
			.selectFrom(labelUser)
			.join(labelUser.label, label)
			.where(label.labelId.eq(labelId))
			.fetch();
	}

	@Override
	public List<User> getUserFromTaskId(Long taskId) {
		return queryFactory
			.selectFrom(user)
			.leftJoin(user.taskUserList, taskUser)
			.leftJoin(taskUser.task, task)
			.where(task.taskId.eq(taskId))
			.fetch();
	}

	@Override
	public List<TaskUser> getTaskUserFromTaskId(Long taskId) {
		return queryFactory
			.selectFrom(taskUser)
			.join(taskUser.task, task)
			.where(task.taskId.eq(taskId))
			.fetch();
	}

	private BooleanExpression getWithOneYear(LocalDate localDate) {
		return localDate != null ? ticket.completedAt.between(localDate.minusYears(1), localDate) : null;
	}

	private BooleanExpression statusEq(TicketStatus status) {
		return status != null ? ticket.status.eq(status) : null;
	}

}
