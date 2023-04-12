package com.ddalggak.finalproject.global;

import static com.mysema.commons.lang.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WebRestControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void Profile확인 () {
		//when
		String nginx = this.restTemplate.getForObject("/nginx", String.class);

		//then
		assertThat(nginx).isEqualTo("local");
	}
}