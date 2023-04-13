package com.ddalggak.finalproject.domain.label.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddalggak.finalproject.domain.label.entity.Label;

public interface LabelRepository extends JpaRepository<Label, Long>, LabelRepositoryCustom {
}
