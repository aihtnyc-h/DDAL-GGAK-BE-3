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
	} //todo 할일 projectTitle, thumbnail 비교해서 다른 점 있으면 update set 하고
}