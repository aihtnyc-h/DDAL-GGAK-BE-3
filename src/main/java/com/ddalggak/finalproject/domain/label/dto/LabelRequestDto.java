package com.ddalggak.finalproject.domain.label.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LabelRequestDto {

	@Schema(name = "task Id", example = "task Id")
	@NotNull(message = "task Id is required")
	public Long taskId;

	@Schema(name = "label title", example = "label title")
	public String labelTitle;

	@Schema(name = "user email to invite", example = "kimdaehyun@abc.com")
	public String email;
}
