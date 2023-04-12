package com.ddalggak.finalproject.domain.label.dto;

import com.ddalggak.finalproject.global.view.Views;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LabelResponseDto {

	@JsonView(Views.Task.class)
	private Long labelId;

	@JsonView(Views.Task.class)
	private String labelTitle;
}
