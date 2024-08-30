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

import com.moriset.bcephal.security.SecurityFactory;
import com.moriset.bcephal.security.domain.User;


@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryIntegrationTests {
	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.user", "com.moriset.bcephal.security.domain",
			"com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config" }, exclude = {
					DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.security", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests {
	}


	@Autowired
	UserRepository userRepository;
	
	private static User user;
	
	@BeforeAll
	static void init() {
		user = new SecurityFactory().buildUser();
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getId()).isNull();
	}

	@Test
	@DisplayName("Validate save user .")
	@Order(1)
	@Commit
	public void SaveUserTest() {
		Assertions.assertThat(user).isNotNull();
		userRepository.save(user);
		Assertions.assertThat(user.getId()).isNotNull();
		Optional<User> found = userRepository.findById(user.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
		Assertions.assertThat(found.get().getName()).isEqualTo(user.getName());
	}

	
	@Test
	@DisplayName("Validate find by username ignoreCase .")
	@Order(2)
	public void findByUsernameIgnoreCaseTest() {
		User user1 = new SecurityFactory().buildUser();
		Assertions.assertThat(user1).isNotNull();
		Assertions.assertThat(user1.getUsername()).isNotNull();
		Optional<User> user_ = userRepository.findByUsernameIgnoreCase(user1.getUsername());
		Assertions.assertThat(user_.isPresent()).isTrue();
		Assertions.assertThat(user_.get().getId()).isNotNull();
		Assertions.assertThat(user_.get().getUsername()).isEqualTo(user1.getUsername());
	}
	
	@Test
	@DisplayName("Validate update user .")
	@Order(3)
	@Commit
	public void updateUserTest() {
		Assertions.assertThat(user).isNotNull();
		user.setEmail("landryndumate@gmail.com");
		userRepository.save(user);
		Assertions.assertThat(user.getId()).isNotNull();
		Optional<User> found = userRepository.findById(user.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
	}
	
	@Test
	@DisplayName("Validate find by username ignoreCase and clientId .")
	@Order(4)
	public void findByUsernameIgnoreCaseAndClientIdTest() {
		User user = new SecurityFactory().buildUser();
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getUsername()).isNotNull();
		Assertions.assertThat(user.getClientId()).isNotNull();
		Optional<User> user_ = userRepository.findByUsernameIgnoreCaseAndClientId(user.getUsername(), user.getClientId());
		Assertions.assertThat(user_.isPresent()).isTrue();
		Assertions.assertThat(user_.get().getId()).isNotNull();
		Assertions.assertThat(user_.get().getUsername()).isEqualTo(user.getUsername());
		Assertions.assertThat(user_.get().getClientId()).isEqualTo(user.getClientId());
	}
	
	
	@Test
	@DisplayName("Validate find by username and clientId .")
	@Order(5)
	public void findByUsernameAndClientIdTest() {
		User user = new SecurityFactory().buildUser();
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getUsername()).isNotNull();
		Assertions.assertThat(user.getClientId()).isNotNull();
		Optional<User> user_ = userRepository.findByUsernameAndClientId(user.getUsername(), user.getClientId());
		Assertions.assertThat(user_.isPresent()).isTrue();
		Assertions.assertThat(user_.get().getId()).isNotNull();
		Assertions.assertThat(user_.get().getUsername()).isEqualTo(user.getUsername());
		Assertions.assertThat(user_.get().getClientId()).isEqualTo(user.getClientId());
	}
	
	@Test
	@DisplayName("Validate find by clientId .")
	@Order(6)
	public void findByClientIdTest() {
		User user = new SecurityFactory().buildUser();
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getUsername()).isNotNull();
		List<User> users = userRepository.findByClientId(user.getClientId());
		Assertions.assertThat(users.isEmpty()).isFalse();
		Assertions.assertThat(users.size()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Validate delete by  client Id .")
	@Order(7)
	@Commit
	public void deleteByClientIdTest() {
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getClientId()).isNotNull();
		userRepository.deleteByClientId(user.getClientId());
		Optional<User> users = userRepository.findById(user.getId());
		Assertions.assertThat(users.isEmpty()).isTrue();
	}
	
	@Test
	@DisplayName("Validate delete user .")
	@Order(8)
	@Commit
	public void deleteUserTest() {
		Assertions.assertThat(user).isNotNull();
		userRepository.deleteById(user.getId());
		Optional<User> users = userRepository.findById(user.getId());
		Assertions.assertThat(users.isEmpty()).isTrue();
	}


}
