package com.ddalggak.finalproject.domain.user.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserTest {

	@Autowired
	EntityManager em;

	@Test
	public void serializationTest() {
		//given
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setEmail("kimdaehyun132@daum.net");
		userRequestDto.setPassword("Rlaeogusrhwk1!");
		User user = User.builder()
			.email(userRequestDto.getEmail())
			.nickname("kimdaehyun132")
			.password(userRequestDto.getPassword())
			.build();
		em.persist(user);
		em.flush();
		em.clear();

		//when
		byte[] serializedMember;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(user);
				serializedMember = baos.toByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String encodeString = Base64.getEncoder().encodeToString(serializedMember);
		byte[] decodeMember = Base64.getDecoder().decode(encodeString);

		try (ByteArrayInputStream bais = new ByteArrayInputStream(decodeMember)) {
			try (ObjectInputStream ois = new ObjectInputStream(bais)) {
				Object objectMember = ois.readObject();
				User newUser = (User)objectMember;
				System.out.println("newUser = " + newUser);
				System.out.println("newUser.getUserId() = " + newUser.getUserId());
				System.out.println("newUser.getEmail() = " + newUser.getEmail());
				System.out.println("user = " + user);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		//then

	}

}