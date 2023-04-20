package com.ddalggak.finalproject.domain.user.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.label.entity.QLabelUser.*;
import static com.ddalggak.finalproject.domain.project.entity.QProject.*;
import static com.ddalggak.finalproject.domain.project.entity.QProjectUser.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.task.entity.QTaskUser.*;
import static com.ddalggak.finalproject.domain.ticket.entity.QTicket.*;
import static com.ddalggak.finalproject.domain.user.entity.QUser.*;
import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.util.List;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.dto.TaskSearchCondition;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.ticket.entity.TicketStatus;
import com.ddalggak.finalproject.domain.user.dto.QUserStatsDto;
import com.ddalggak.finalproject.domain.user.dto.UserStatsDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.global.error.CustomException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<LabelUser> getUserFromLabelId(Long labelId) {
		NumberExpression<Integer> rankPath = new CaseBuilder()
			.when(labelUser.user.email.eq(label.labelLeader)).then(1)
			.otherwise(2);

		return queryFactory
			.selectFrom(labelUser)
			.join(labelUser.label, label)
			.where(label.labelId.eq(labelId))
			.orderBy(rankPath.asc())
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
		NumberExpression<Integer> rankPath = new CaseBuilder()
			.when(taskUser.user.email.eq(task.taskLeader)).then(1)
			.otherwise(2);
		return queryFactory
			.selectFrom(taskUser)
			.join(taskUser.task, task)
			.where(task.taskId.eq(taskId))
			.orderBy(rankPath.asc())
			.fetch();
	}

	@Override
	public List<User> getUserFromProjectId(Long projectId) {
		return queryFactory
			.selectFrom(user)
			.leftJoin(user.projectUserList, projectUser).fetchJoin()
			.leftJoin(projectUser.project, project)
			.where(project.projectId.eq(projectId))
			.fetch();
	}

	@Override
	public List<ProjectUser> getProjectUserFromProjectId(Long projectId) {
		NumberExpression<Integer> rankPath = new CaseBuilder()
			.when(projectUser.user.email.eq(project.projectLeader)).then(1)
			.otherwise(2);

		return queryFactory
			.selectFrom(projectUser)
			.join(projectUser.project, project)
			.where(project.projectId.eq(projectId))
			.orderBy(rankPath.asc())
			.fetch();
	}

	@Override
	public UserStatsDto getUserStats(Long userId) {
		UserStatsDto userStatsDto = queryFactory
			.select(new QUserStatsDto(
				user.userId,
				user.email,
				user.nickname,
				user.profile.as("thumbnail")))
			.from(user)
			.where(user.userId.eq(userId))
			.fetchOne();

		if (userStatsDto == null) {
			throw new CustomException(MEMBER_NOT_FOUND);
		}

		List<Tuple> stats = queryFactory
			.select(ticket.difficulty.sum(), ticket.priority.sum(), ticket.difficulty.avg(), ticket.priority.avg(),
				ticket.count())
			.from(ticket)
			.where(ticket.user.userId.eq(userId))
			.fetch();

		Long completedTicketCount = queryFactory
			.select(ticket.count())
			.from(ticket)
			.where(ticket.user.userId.eq(userId),
				ticket.status.eq(TicketStatus.DONE))
			.fetchOne();
		Integer calculatedTotalDifficulty = stats.get(0).get(ticket.difficulty.sum()) == null ? 0 :
			stats.get(0).get(ticket.difficulty.sum());
		Integer calculatedTotalPriority = stats.get(0).get(ticket.priority.sum()) == null ? 0 :
			stats.get(0).get(ticket.priority.sum());
		Double calculatedDifficulty =
			(double)(stats.get(0).get(ticket.priority.avg()) == null ? 0 :
				Math.round(stats.get(0).get(ticket.priority.avg()) * 100 / 100));
		Double calculatedPriority = stats.get(0).get(ticket.difficulty.avg()) == null ? 0 :
			(double)(Math.round(stats.get(0).get(ticket.difficulty.avg()) * 100 / 100));

		userStatsDto.setTotalDifficulty(calculatedTotalDifficulty);
		userStatsDto.setTotalPriority(calculatedTotalPriority);
		userStatsDto.setTotalTicketCount(stats.get(0).get(ticket.count()));
		userStatsDto.setAveragePriority(calculatedPriority);
		userStatsDto.setAverageDifficulty(calculatedDifficulty);
		userStatsDto.setCompletedTicketCount(completedTicketCount);

		return userStatsDto;

	}

	@Override
	public List<ProjectUser> getProjectUserWithTaskCondition(Long projectId, TaskSearchCondition condition) {
		NumberExpression<Integer> rankPath = new CaseBuilder()
			.when(projectUser.user.email.eq(project.projectLeader)).then(1)
			.otherwise(2);

		return queryFactory
			.selectDistinct(projectUser)
			.from(project)
			.join(project.projectUserList, projectUser)
			.leftJoin(project.taskList, task)
			.where(project.projectId.eq(projectId),
				statusNonEq(condition))
			.orderBy(rankPath.asc())
			.fetch();
	}

	private BooleanExpression statusNonEq(TaskSearchCondition condition) {
		return condition.getTaskId() == null ? null : projectUser.user.notIn(
			queryFactory
				.select(taskUser.user)
				.from(taskUser)
				.join(taskUser.task, task)
				.where(task.taskId.eq(condition.getTaskId()))
		);
	}
}
