package com.ddalggak.finalproject.domain.label.dto;

import com.ddalggak.finalproject.domain.label.entity.Label;
import com.ddalggak.finalproject.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LabelUserRequestDto {

	@Schema(name = "label", description = "label entity")
	public Label label;

	@Schema(name = "user", defaultValue = "anonymous")
	public User user;

	public static LabelUserRequestDto create(User user) {
		return LabelUserRequestDto.builder()
			.user(user)
			.build();
	}

}
