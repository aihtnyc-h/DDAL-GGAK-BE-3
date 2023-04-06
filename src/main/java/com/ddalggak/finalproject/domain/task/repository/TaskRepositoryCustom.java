package com.ddalggak.finalproject.domain.task.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.task.dto.TaskBriefResponseDto;
import com.ddalggak.finalproject.domain.task.dto.TaskResponseDto;

public interface TaskRepositoryCustom {
	TaskResponseDto findTaskById(Long id);

	List<TaskBriefResponseDto> findTaskByProject(Long projectid);
}