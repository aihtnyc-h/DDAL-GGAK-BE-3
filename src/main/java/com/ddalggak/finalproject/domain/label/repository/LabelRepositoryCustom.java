package com.ddalggak.finalproject.domain.label.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.label.dto.LabelResponseDto;

public interface LabelRepositoryCustom {

	List<LabelResponseDto> findByTaskId(Long taskId);
}
