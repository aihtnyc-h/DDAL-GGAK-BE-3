package com.ddalggak.finalproject.domain.label.dto;

import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.global.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LabelResponseDto {

	@JsonView(Views.Task.class)
	private Long labelId;

	@JsonView(Views.Task.class)
	private String labelTitle;

	@Builder
	public LabelResponseDto(Label label) {
		labelId = label.getLabelId();
		labelTitle = label.getLabelTitle();
	}

	public static LabelResponseDto of(Label label) {
		return LabelResponseDto.builder()
			.label(label)
			.build();
	}

}
