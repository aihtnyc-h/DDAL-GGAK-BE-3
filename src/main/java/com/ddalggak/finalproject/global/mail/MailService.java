package com.ddalggak.finalproject.global.mail;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.user.dto.UserResponseDto;
import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.dto.SuccessCode;
import com.ddalggak.finalproject.global.dto.SuccessResponseDto;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.mail.emailAuthCode.EmailAuthCode;
import com.ddalggak.finalproject.global.mail.emailAuthCode.EmailAuthCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final UserRepository userRepository;
	private final EmailAuthCodeRepository emailAuthCodeRepository;
	@Autowired
	private JavaMailSender javaMailSender;

	// 인증 코드 발송
	@Transactional
	public ResponseEntity<SuccessResponseDto> sendRandomCode(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		String emailAuthCode = randomCode();
		// 회원 중복 확인
		if (optionalUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATE_MEMBER);
		}
		// email 인증 mail 작성
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setTo(email);
		simpleMessage.setSubject("Welcome To DDAL-KKAK");
		simpleMessage.setText(emailAuthCode);
		javaMailSender.send(simpleMessage);
		// 인증할 정보 저장
		EmailAuthCode saveCode = new EmailAuthCode(email, emailAuthCode);
		emailAuthCodeRepository.save(saveCode);
		return SuccessResponseDto.toResponseEntity(SuccessCode.SUCCESS_SEND);
	}

	// 프로젝트 초대 코드 발송
	@Transactional
	public Map<String, Object> sendProjectInviteCode(String projectInviteCode, List<String> emails) {
		// 회원일 경우 return 할 정보
		List<UserResponseDto> userResponseDtoList = new ArrayList<>();
		// 회원이 아닐 경우 return 할 정보
		List<String> readyToInviteEmails = new ArrayList<>();
		for (String email : emails) {
			User user = userRepository.findByEmail(email)
				.orElse(null);
			// 비회원일 경우 email 형식
			if (user == null) {
				readyToInviteEmails.add(email);

				SimpleMailMessage loginMessage = new SimpleMailMessage();
				loginMessage.setTo(email);
				loginMessage.setSubject("You are invited to join DDAL-GGAK's project");
				loginMessage.setText("localhost:8080/login" + " / please enter after sign up / "
					+ projectInviteCode);             //회원가입 페이지 발송 url 변경 필요
				javaMailSender.send(loginMessage);
			} else {
				// 회원일 경우 email 형식
				SimpleMailMessage simpleMessage = new SimpleMailMessage();
				simpleMessage.setTo(email);
				simpleMessage.setSubject("You are invited to join DDAL-GGAK's project");
				simpleMessage.setText(projectInviteCode);
				javaMailSender.send(simpleMessage);
				// 회원일 경우 회원 정보 return 값 설정
				UserResponseDto savedUser = UserResponseDto.builder()
					.id(user.getUserId())
					.email(user.getEmail())
					.nickname(user.getNickname())
					.thumbnail(user.getProfile())
					.role(user.getRole().toString())
					.build();
				userResponseDtoList.add(savedUser);
			}
		}
		Map<String, Object> response = new HashMap<>();
		response.put("invitedUsers", userResponseDtoList);
		response.put("invitedNonUsers", readyToInviteEmails);
		return response;
	}

	// randomCode 생성
	private String randomCode() {
		int length = 6;
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}
}
