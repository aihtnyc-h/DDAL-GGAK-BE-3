package com.ddalggak.finalproject.global.nginx;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

public class ProfileControllerUnitTest {

	@Test
	public void real_profile_search() {
		//given
		String expectedProfile = "real";
		MockEnvironment env = new MockEnvironment();
		env.addActiveProfile(expectedProfile);
		env.addActiveProfile("oauth");
		env.addActiveProfile("real-db");

		ProfileController controller = new ProfileController(env);

		//when
		String profile = controller.profile();

		//then
		assertThat(profile).isEqualTo(expectedProfile);
	}

	@Test
	public void real_profile_or_real1_profile() {
		//given
		String expectedProfile = "oauth";
		MockEnvironment env = new MockEnvironment();
		env.addActiveProfile(expectedProfile);
		env.addActiveProfile("real-db");

		ProfileController controller = new ProfileController(env);

		//when
		String profile = controller.profile();

		//then
		assertThat(profile).isEqualTo(expectedProfile);
	}

	@Test
	public void active_profile_or_default() {
		//given
		String expectedProfile = "default";
		MockEnvironment env = new MockEnvironment();

		ProfileController controller = new ProfileController(env);

		//when
		String profile = controller.profile();

		//then
		assertThat(profile).isEqualTo(expectedProfile);
	}
}