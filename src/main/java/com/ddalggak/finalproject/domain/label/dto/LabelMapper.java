package com.ddalggak.finalproject.domain.label.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.global.mapper.GenericMapper;

@Mapper(componentModel = "spring")
public interface LabelMapper extends GenericMapper<LabelResponseDto, Label> {
	@Override
	@Mappings({
		@Mapping(target = "labelId", source = "entity.labelId"),
		@Mapping(target = "labelTitle", source = "entity.labelTitle")
	})
	LabelResponseDto toDto(Label entity);

	@Override
	default Label toEntity(LabelResponseDto dto) {
		return null;
	}

	@Override
	default void updateEntity(LabelResponseDto dto, Label entity) {

	}

	List<LabelResponseDto> toDtoList(List<Label> entityList);
}
