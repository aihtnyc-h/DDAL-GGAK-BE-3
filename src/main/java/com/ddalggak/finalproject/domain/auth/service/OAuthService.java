package com.ddalggak.finalproject.domain.auth.service;

import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.auth.dto.GoogleOAuthRequestDto;
import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.oauth.entity.ProviderType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {
	private final GoogleOAuth googleOAuth;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final UserMapper userMapper;

	@Transactional
	public ResponseEntity<UserPageDto> googleLogin(HttpServletResponse response, String code) {
		GoogleOAuthRequestDto.Token tokenRequestDto = googleOAuth.getAccessToken(code);
		GoogleOAuthRequestDto.GoogleUser googleUserDto = googleOAuth.getUserInfo(tokenRequestDto);

		User user = createOAuthUser(googleUserDto);
		String email = googleUserDto.getEmail();

		// 로그인 처리 (토큰 발급)
		jwtUtil.login(email, UserRole.USER, response);
		// 프론트 요청 데이터 (기본 유저 정보)
		UserPageDto userPageDto = userMapper.toUserPageDto(user);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userPageDto);
	}

	@Transactional
	public User createOAuthUser(GoogleOAuthRequestDto.GoogleUser googleUserDto) {
		User user = userRepository.findByEmail(googleUserDto.getEmail())
			.orElseGet(() -> userRepository.save(new User(googleUserDto)));

		if (user.getProviderType() != ProviderType.GOOGLE) {
			throw new UserException(UNAUTHORIZED_MEMBER);
		}

		return user;
	}
}
