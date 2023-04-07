package com.ddalggak.finalproject.domain.user.dto;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NicknameDto {
	@Pattern(regexp = "^(?=.*[A-Za-z가-힣].*[A-Za-z가-힣])[A-Za-zㄱ-ㅎㅏ-ㅣ가-힣]*$")
	private String nickname;
}
