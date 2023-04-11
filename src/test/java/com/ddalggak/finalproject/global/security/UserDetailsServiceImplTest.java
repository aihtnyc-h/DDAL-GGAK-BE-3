package com.ddalggak.finalproject.global.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ddalggak.finalproject.domain.user.dto.UserRequestDto;
import com.ddalggak.finalproject.domain.user.entity.User;

@SpringBootTest
@Transactional
class UserDetailsServiceImplTest {

	@Autowired
	EntityManager em;

	//userDetailsImpl이 직렬화, 역직렬화가 가능한지 테스트 해본다.
	@Test
	public void serializableTest() {
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

		User found = em.createQuery("select u from com.ddalggak.finalproject.domain.user.entity.User u", User.class)
			.getSingleResult();

		Assertions.assertThat(found.getEmail()).isEqualTo(userRequestDto.getEmail());
		System.out.println("found.getCreatedAt() = " + found.getCreatedAt());
		System.out.println("found.getUserId() = " + found.getUserId());
		try {
			// user
			UserDetailsImpl userDetails = UserDetailsImpl.create(user);
			// serialize
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(userDetails);
			oos.close();
			byte[] serializedUserDetails = baos.toByteArray();

			// deserialize
			ByteArrayInputStream bais = new ByteArrayInputStream(serializedUserDetails);
			ObjectInputStream ois = new ObjectInputStream(bais);
			UserDetailsImpl deserializedUserDetails = (UserDetailsImpl)ois.readObject();
			ois.close();

			System.out.println("userDetails = " + userDetails);
			System.out.println("deserializedUserDetails = " + deserializedUserDetails);

			// serialize와 deserialize한 객체가 같은지 확인
			if (userDetails.equals(deserializedUserDetails)) {
				System.out.println("직렬화 성공");
			} else {
				System.out.println("직렬화 실패");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("직렬화 실패1" + e.getMessage());
		}

		//when

		//then

	}

}