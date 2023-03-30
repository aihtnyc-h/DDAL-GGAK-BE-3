package com.ddalggak.finalproject.domain.label.dto;

import com.ddalggak.finalproject.domain.label.entity.Label;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LabelResponseDto {
	private String labelTitle;

	@Builder
	public LabelResponseDto(Label label) {
		labelTitle = label.getLabelTitle();
	}

	public static LabelResponseDto of(Label label) {
		return LabelResponseDto.builder()
			.label(label)
			.build();
	}
}
