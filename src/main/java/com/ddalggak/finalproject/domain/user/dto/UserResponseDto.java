package com.ddalggak.finalproject.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponseDto {

	public Long id;

	public String email;

	public String nickname;

	public String thumbnail;

	public String role;

}
