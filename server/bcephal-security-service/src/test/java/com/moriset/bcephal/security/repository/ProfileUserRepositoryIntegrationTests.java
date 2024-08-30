package com.moriset.bcephal.security.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.security.SecurityFactory;
import com.moriset.bcephal.security.domain.ProfileUser;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileUserRepositoryIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.profileUser",
			"com.moriset.bcephal.security.domain", "com.moriset.bcephal.multitenant.jpa",
			"com.moriset.bcephal.config" }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.security", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests {
	}

	@Autowired
	ProfileUserRepository profileUserRepository;

	private static ProfileUser profileUser;

	@BeforeAll
	static void init() {
		profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getId()).isNull();
	}

	@Test
	@DisplayName("Validate save profileUser.")
	@Order(1)
	@Commit
	public void SaveUserInfoTest() {
		Assertions.assertThat(profileUser).isNotNull();
		profileUserRepository.save(profileUser);
		Assertions.assertThat(profileUser.getId()).isNotNull();
		Optional<ProfileUser> found = profileUserRepository.findById(profileUser.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getClientId()).isEqualTo(profileUser.getClientId());
		Assertions.assertThat(found.get().getProfileId()).isEqualTo(profileUser.getProfileId());
		Assertions.assertThat(found.get().getUserId()).isEqualTo(profileUser.getUserId());
	}

	@Test
	@DisplayName("Validate find by profileId.")
	@Order(2)
	public void findByProfileIdTest() {
		ProfileUser profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getProfileId()).isNotNull();
		List<ProfileUser> profileUsers = profileUserRepository.findByProfileId(profileUser.getProfileId());
		Assertions.assertThat(profileUsers.isEmpty()).isFalse();
		Assertions.assertThat(profileUsers.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate find by userId.")
	@Order(3)
	public void findByUserIdTest() {
		ProfileUser profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getUserId()).isNotNull();
		List<ProfileUser> profileUsers = profileUserRepository.findByUserId(profileUser.getUserId());
		Assertions.assertThat(profileUsers.isEmpty()).isFalse();
		Assertions.assertThat(profileUsers.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate get profiles by cliendId and userId.")
	@Order(4)
	public void getProfilesByClientIdAndUserIdTest() {
		ProfileUser profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getClientId()).isNotNull();
		Assertions.assertThat(profileUser.getUserId()).isNotNull();
		List<Nameable> nameables = profileUserRepository.getProfilesByClientIdAndUserId(profileUser.getClientId(),
				profileUser.getUserId());
		Assertions.assertThat(nameables.isEmpty()).isFalse();
		Assertions.assertThat(nameables.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate get users by profileId.")
	@Order(5)
	public void getUsersByProfileIdTest() {
		ProfileUser profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getProfileId()).isNotNull();
		List<Nameable> nameables = profileUserRepository.getUsersByProfileId(profileUser.getProfileId());
		Assertions.assertThat(nameables.isEmpty()).isFalse();
		Assertions.assertThat(nameables.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate find by profileId , userId and clientId.")
	@Order(6)
	public void findByProfileIdAndUserIdAndClientIdTest() {
		ProfileUser profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getProfileId()).isNotNull();
		Assertions.assertThat(profileUser.getUserId()).isNotNull();
		Assertions.assertThat(profileUser.getClientId()).isNotNull();
		List<ProfileUser> profileUsers = profileUserRepository.findByProfileIdAndUserIdAndClientId(
				profileUser.getProfileId(), profileUser.getUserId(), profileUser.getClientId());
		Assertions.assertThat(profileUsers.isEmpty()).isFalse();
		Assertions.assertThat(profileUsers.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate find by profileId and userId.")
	@Order(7)
	public void findByProfileIdAndUserIdTest() {
		ProfileUser profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getProfileId()).isNotNull();
		Assertions.assertThat(profileUser.getUserId()).isNotNull();
		List<ProfileUser> profileUsers = profileUserRepository.findByProfileIdAndUserId(profileUser.getProfileId(),
				profileUser.getUserId());
		Assertions.assertThat(profileUsers.isEmpty()).isFalse();
		Assertions.assertThat(profileUsers.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate delete profileId and userId.")
	@Order(8)
	@Commit
	public void deleteByProfileIdAndUserIdTest() {
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getProfileId()).isNotNull();
		Assertions.assertThat(profileUser.getUserId()).isNotNull();
		profileUserRepository.deleteByProfileIdAndUserId(profileUser.getProfileId(), profileUser.getUserId());
		Optional<ProfileUser> profileUser_ = profileUserRepository.findById(profileUser.getId());
		Assertions.assertThat(profileUser_.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("Validate delete profileId.")
	@Order(9)
	@Commit
	public void deleteByProfileIdTest() {
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getProfileId()).isNotNull();
		profileUserRepository.deleteByProfileId(profileUser.getProfileId());
		Optional<ProfileUser> profileUser_ = profileUserRepository.findById(profileUser.getId());
		Assertions.assertThat(profileUser_.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("Validate delete userId.")
	@Order(10)
	@Commit
	public void deleteByUserIdTest() {
		Assertions.assertThat(profileUser).isNotNull();
		Assertions.assertThat(profileUser.getUserId()).isNotNull();
		profileUserRepository.deleteByUserId(profileUser.getUserId());
		Optional<ProfileUser> profileUser_ = profileUserRepository.findById(profileUser.getId());
		Assertions.assertThat(profileUser_.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("Validate delete profileUser.")
	@Order(11)
	@Commit
	public void deleteProfileUserTest() {
		Assertions.assertThat(profileUser).isNotNull();
		profileUserRepository.deleteById(profileUser.getId());
		Optional<ProfileUser> profileUser_ = profileUserRepository.findById(profileUser.getId());
		Assertions.assertThat(profileUser_.isEmpty()).isTrue();
	}
}
