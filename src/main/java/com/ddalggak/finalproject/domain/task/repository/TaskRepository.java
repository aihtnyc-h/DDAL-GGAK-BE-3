package com.ddalggak.finalproject.domain.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddalggak.finalproject.domain.task.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
}
