package com.ddalggak.finalproject.global.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.*;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;

public interface GenericMapper<D, E> {
	D toDto(E entity);

	E toEntity(D dto);

	@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
	void updateEntity(D dto, @MappingTarget E entity);
}