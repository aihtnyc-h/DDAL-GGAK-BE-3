package com.ddalggak.finalproject.global.mail;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.user.entity.User;
import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCode;
import com.ddalggak.finalproject.global.mail.randomCode.RandomCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final UserRepository userRepository;
	private final RandomCodeRepository randomCodeRepository;
	@Autowired
	private JavaMailSender javaMailSender;

	@Transactional
	public void sendMail(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		String randomCode = randomCode();

		if (optionalUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATE_MEMBER);
		}

		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setTo(email);
		simpleMessage.setSubject("Welcome To DDAL-KKAK");
		simpleMessage.setText(randomCode);
		javaMailSender.send(simpleMessage);

		RandomCode auth = new RandomCode(email, randomCode);
		randomCodeRepository.save(auth);
	}

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
