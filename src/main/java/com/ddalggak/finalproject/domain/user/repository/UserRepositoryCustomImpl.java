package com.ddalggak.finalproject.domain.user.repository;

import static com.ddalggak.finalproject.domain.label.entity.QLabel.*;
import static com.ddalggak.finalproject.domain.label.entity.QLabelUser.*;
import static com.ddalggak.finalproject.domain.project.entity.QProject.*;
import static com.ddalggak.finalproject.domain.project.entity.QProjectUser.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;
import static com.ddalggak.finalproject.domain.task.entity.QTaskUser.*;
import static com.ddalggak.finalproject.domain.user.entity.QUser.*;

import java.util.List;

import com.ddalggak.finalproject.domain.label.entity.LabelUser;
import com.ddalggak.finalproject.domain.project.entity.ProjectUser;
import com.ddalggak.finalproject.domain.task.entity.TaskUser;
import com.ddalggak.finalproject.domain.user.entity.User;
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

}
