package com.ddalggak.finalproject.domain.user.dto;

import javax.validation.constraints.Pattern;

import lombok.Getter;

@Getter
public class NicknameRequestDto {
	@Pattern(regexp = "^(?=.*[A-Za-z가-힣].*[A-Za-z가-힣])[A-Za-zㄱ-ㅎㅏ-ㅣ가-힣]*$")
	private String nickname;
}
