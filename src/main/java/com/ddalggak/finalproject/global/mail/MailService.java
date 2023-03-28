package com.ddalggak.finalproject.global.mail;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

	@Autowired
	private JavaMailSender javaMailSender;
	private final UserRepository userRepository;
	private final RandomCodeRepository randomCodeRepository;

	public void sendMail(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isPresent()) {
			throw new UserException(ErrorCode.DUPLICATE_MEMBER);
		}

		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setTo(email);
		simpleMessage.setSubject("Welcome To DDAL-KKAK");
		simpleMessage.setText(randomCode());
		javaMailSender.send(simpleMessage);
		String randomCode = randomCode();

		RandomCode auth = new RandomCode(email, randomCode);
		randomCodeRepository.save(auth);
	}

	private String randomCode() {
		int leftLimit = 48;
		int rightLimit = 122;
		int targetStringLength = 6;
		Random random = new Random();
		String randomCode = random.ints(leftLimit, rightLimit + 1)
			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			.limit(targetStringLength)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
		return randomCode;
	}

}
