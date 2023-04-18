package com.ddalggak.finalproject.domain.label.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LabelResponseDto {

	private Long labelId;

	private String labelTitle;

	private String labelLeader;
}