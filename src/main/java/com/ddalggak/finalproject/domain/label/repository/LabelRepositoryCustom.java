package com.ddalggak.finalproject.domain.label.repository;

import java.util.List;

import com.ddalggak.finalproject.domain.label.entity.Label;

public interface LabelRepositoryCustom {

	List<Label> findByTaskId(Long taskId);
}
