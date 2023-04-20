package com.ddalggak.finalproject.domain.auth.service;

import static com.ddalggak.finalproject.global.dto.SuccessCode.*;
import static com.ddalggak.finalproject.global.error.ErrorCode.*;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.user.dto.UserMapper;
import com.ddalggak.finalproject.domain.user.dto.UserPageDto;
import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.jwt.JwtUtil;
import com.ddalggak.finalproject.global.mail.emailAuthCode.EmailAuthCode;
import com.ddalggak.finalproject.global.mail.emailAuthCode.EmailAuthCodeDto;
import com.ddalggak.finalproject.global.mail.emailAuthCode.EmailAuthCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final UserMapper userMapper;
	private final EmailAuthCodeRepository emailAuthCodeRepository;

	@Transactional
	public void emailAuthenticationWithRandomCode(EmailAuthCodeDto emailAuthCodeDto) {
		String email = emailAuthCodeDto.getEmail();
		String emailAuthCode = emailAuthCodeDto.getEmailAuthCode();
		// 인증 코드 확인
		EmailAuthCode savedCode = emailAuthCodeRepository.findById(email)
			.orElseThrow(() -> new UserException(INVALID_RANDOM_CODE));
		String savedEmail = savedCode.getEmail();
		String savedEmailAuthCode = savedCode.getEmailAuthCode();
		// 인증 코드 데이터 삭제
		if (emailAuthCode.equals(savedEmailAuthCode) && email.equals(savedEmail)) {
			emailAuthCodeRepository.delete(savedCode);
		}
	}

	@Transactional
	public ResponseEntity<SuccessResponseDto> signup(UserRequestDto userRequestDto) {
		String email = userRequestDto.getEmail();
		// 이메일 중복 확인
		Optional<User> foundUser = userRepository.findByEmail(email);
		if (foundUser.isPresent()) {
			throw new UserException(DUPLICATE_MEMBER);
		}
		// 닉네임 임의 등록
		String[] parts = email.split("@");
		String nickname = parts[0];
		// 비밀번호 암호화
		String password = passwordEncoder.encode(userRequestDto.getPassword());
		// 유저 정보 저장
		User user = User.builder()
			.email(email)
			.nickname(nickname)
			.password(password)
			.role(UserRole.USER)
			.build();
		userRepository.save(user);
		return SuccessResponseDto.toResponseEntity(CREATED_SUCCESSFULLY);
	}

	@Transactional
	public ResponseEntity<UserPageDto> login(UserRequestDto userRequestDto, HttpServletResponse response) {
		String email = userRequestDto.getEmail();
		// email 확인
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(INVALID_EMAIL_PASSWORD));
		String password = userRequestDto.getPassword();
		String dbPassword = user.getPassword();
		// 비밀번호 확인
		if (!passwordEncoder.matches(password, dbPassword)) {
			throw new UserException(INVALID_EMAIL_PASSWORD);
		}
		// 로그인 처리 (토큰 발급)
		jwtUtil.login(email, user.getRole(), response);
		// 프론트 요청 데이터 (기본 유저 정보)
		UserPageDto userPageDto = userMapper.toUserPageDto(user);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(userPageDto);
	}
}