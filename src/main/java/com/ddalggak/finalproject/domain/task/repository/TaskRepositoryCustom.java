package com.ddalggak.finalproject.domain.task.repository;

import com.ddalggak.finalproject.domain.task.dto.TaskResponseDto;

public interface TaskRepositoryCustom {
	TaskResponseDto findTaskById(Long id);
}
