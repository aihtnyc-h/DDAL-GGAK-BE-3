package com.ddalggak.finalproject.domain.project.repository;

import static com.ddalggak.finalproject.domain.project.entity.QProject.*;
import static com.ddalggak.finalproject.domain.project.entity.QProjectUser.*;
import static com.ddalggak.finalproject.domain.task.entity.QTask.*;

import java.util.List;
import java.util.Optional;

import com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.dto.ProjectRequestDto;
import com.ddalggak.finalproject.domain.project.dto.QProjectBriefResponseDto;
import com.ddalggak.finalproject.domain.project.entity.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	//@Query("select new com.ddalggak.finalproject.domain.project.dto.ProjectBriefResponseDto(p.projectId, p.projectTitle, p.thumbnail) from Project p join p.projectUserList pu where pu.user.userId = :userId")
	@Override
	public List<ProjectBriefResponseDto> findProjectAllByUserId(Long userId) {
		return queryFactory.select(new QProjectBriefResponseDto(
				project.projectId,
				project.thumbnail,
				project.projectTitle
			))
			.from(project)
			.join(project.projectUserList, projectUser)
			.where(projectUser.user.userId.eq(userId))
			.fetch();
	}

	@Override
	public Optional<Project> findProjectByTaskId(Long taskId) {
		Project result = queryFactory
			.selectFrom(project)
			.join(project.taskList, task)
			.where(task.taskId.eq(taskId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public void update(Long projectId, ProjectRequestDto projectRequestDto) {
		queryFactory.update(project)
			.set(project.projectTitle, projectRequestDto.projectTitle)
			.set(project.thumbnail, projectRequestDto.thumbnail)
			.where(project.projectId.eq(projectId))
			.execute();
	} //todo 할일 projectTitle, thumbnail 비교해서 다른 점 있으면 update set 하고,

	// @Override
	// public ProjectResponseDto findDtoByProjectId(Long projectId) {
	// 	List<ProjectUser> result1 = queryFactory
	// 		.selectFrom(projectUser)
	// 		.where(projectUser.project.projectId.eq(projectId))
	// 		.fetch();
	// 	List<Task> result2 = queryFactory.selectFrom(task)
	// 		.where(task.project.projectId.eq(projectId))
	// 		.fetch();
	// 	Project project1 = queryFactory
	// 		.selectFrom(project)
	// 		.where(project.projectId.eq(projectId))
	// 		.fetchOne();
	//
	// 	List<Tuple> fetchedResult = queryFactory.select(project, projectUser, task)
	// 		.from(project)
	// 		.leftJoin(project.projectUserList, projectUser)
	// 		.leftJoin(project.taskList, task)
	// 		.where(project.projectId.eq(projectId))
	// 		.fetch();
	// 	Project foundProject = fetchedResult.get(0).get(project);
	// 	List<ProjectUser> projectUsers = fetchedResult.stream()
	// 		.map(tuple -> tuple.get(projectUser))
	// 		.filter(Objects::nonNull)
	// 		.distinct()
	// 		.collect(Collectors.toList());
	// 	List<Task> tasks = fetchedResult.stream()
	// 		.map(tuple -> tuple.get(task))
	// 		.filter(Objects::nonNull)
	// 		.distinct()
	// 		.collect(Collectors.toList());
	//
	// 	return new ProjectResponseDto(foundProject, projectUsers, tasks);
	// }
}

// ProjectResponseDto projectResponseDto = queryFactory
// 	.select(Projections.constructor(
// 		ProjectResponseDto.class,
// 		project,
// 		list(Projections.fields(
// 			ProjectUser.class,
// 			projectUser.user,
// 			projectUser.project).as("projectUser")),
// 		list(Projections.fields(
// 			Task.class,
// 			task.taskId,
// 			task.taskTitle,
// 			task.expiredAt,
// 			task.taskLeader
// 		).as("task"))))
// 	.from(project)
// 	.join(projectUser).on(projectUser.project.projectId.eq(projectId))
// 	.join(task).on(task.project.projectId.eq(projectId))
// 	.fetchOne();

// @Override
// public ProjectResponseDto findDtoByProjectId(Long projectId) {
// 	Map<Long, ProjectResponseDto> transform = queryFactory
// 		.from(project)
// 		.join(projectUser).on(projectUser.project.projectId.eq(projectId))
// 		.join(task).on(task.project.projectId.eq(projectId))
// 		.transform(groupBy(project.projectId).as(new QProjectResponseDto(
// 			project,
// 			list(Projections.fields(
// 				ProjectUser.class,
// 				projectUser.user,
// 				projectUser.project).as("projectUser")),
// 			list(Projections.fields(
// 				Task.class,
// 				task.taskId,
// 				task.taskTitle,
// 				task.expiredAt).as("task"))
// 		)));
// 	return transform.get(projectId);
// }