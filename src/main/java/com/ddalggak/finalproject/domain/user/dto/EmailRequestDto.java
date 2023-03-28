package com.ddalggak.finalproject.domain.user.dto;

import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class EmailRequestDto {

	@Schema(description = "인증이 가능한 이메일을 입력해주세요",
		pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
	private String email;

}
