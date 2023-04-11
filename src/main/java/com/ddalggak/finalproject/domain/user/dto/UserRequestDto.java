package com.ddalggak.finalproject.domain.user.dto;

import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

	@Schema(description = "인증이 가능한 이메일을 입력해주세요",
		pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
	private String email;

	@Schema(description = "특수문자, 알파벳 대,소문자가 필수로 들어간 조합한 8~15자로 작성해주세요",
		pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[!@#$%^&*()_+\\[\\]{};':\",./<>?\\|])(?!.*\\s).{8,15}$")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[!@#$%^&*()_+\\[\\]{};':\",./<>?\\|])(?!.*\\s).{8,15}$")
	private String password;

}
