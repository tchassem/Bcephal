package com.moriset.bcephal.security.service;

import java.util.Locale;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.security.SecurityFactory;
import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.utils.BcephalException;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "serv" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.security",
			"com.moriset.bcephal.multitenant.jpa", }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class, })
	@EntityScan(basePackages = { "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "serv" })
	static class ApplicationTests {
	}

	@Autowired
	private UserService userService;

	private static User user;


	@BeforeAll
	static void init() {
		user = new SecurityFactory().buildUser();
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getId()).isNull();
	}

	@Test
	@DisplayName("Validate user bean.")
	@Order(1)
	public void validateTest() {
		Assertions.assertThat(user).isNotNull();
	}

	
	@Test
	@DisplayName("Validate save  user by user, clientId, locale and projectCode.")
	@Order(3)
	@Commit
	public void saveUserTest() {
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getClientId()).isNotNull();
		Assertions.assertThat(new SecurityFactory().getProjectCode()).isNotNull();
		userService.save(user, user.getClientId(), Locale.FRENCH, new SecurityFactory().getProjectCode());
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getId()).isNotNull();
	}

	@Test
	@DisplayName("Validate find by name.")
	@Order(4)
	public void findByNameTest() {
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getId()).isNotNull();
		Assertions.assertThat(user.getUsername()).isNotNull();

		User user_ = userService.findByName(user.getUsername());
		Assertions.assertThat(user_).isNotNull();
		Assertions.assertThat(user_.getUsername()).isEqualTo(user.getUsername());

	}

	@Test
	@DisplayName("Validate find by name and clientId.")
	@Order(5)
	public void findByName2Test() {
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getId()).isNotNull();
		Assertions.assertThat(user.getUsername()).isNotNull();
		Assertions.assertThat(user.getClientId()).isNotNull();

		User user_ = userService.findByName(user.getUsername(), user.getClientId());
		Assertions.assertThat(user_).isNotNull();
		Assertions.assertThat(user_.getUsername()).isEqualTo(user.getUsername());
	}

	@Test
	@DisplayName("Validate get by  user Id.")
	@Order(6)
	public void getByIdTest() {
		Assertions.assertThat(user).isNotNull();
		userService.getById(user.getId());
		User found = userService.getById(user.getId());
		Assertions.assertThat(found).isNotNull();
	}

	@Test
	@DisplayName("Validate delete user.")
	@Order(15)
	@Commit
	public void deleteUserTest() {
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getId()).isNotNull();
		userService.delete(user);
		User found = userService.getById(user.getId());
		Assertions.assertThat(found).isNull();
	}

	@Test
	@DisplayName("Validate delete user by client Id.")
	@Order(16)
	@Commit
	public void deleteByClientIdTest() {
		Assertions.assertThat(user).isNotNull();
		Assertions.assertThat(user.getClientId()).isNotNull();
		userService.deleteByClientId(user.getClientId());
		User found = userService.getById(user.getId());
		Assertions.assertThat(found).isNull();
	}

	@Test
	@DisplayName("Validate try to build admin user .")
	@Order(17)
	@Commit
	public void tryToBuildAdminUserTest() {
		User admin = new SecurityFactory().buildAdminUser();
		Assertions.assertThat(admin).isNotNull();
		Assertions.assertThat(admin.getUsername()).isNotNull();
		try {
			User user_ = userService.tryToBuildAdminUser(admin.getUsername());
			if (user_ != null) {
				Assertions.assertThat(user_.getId()).isNotNull();
				Assertions.assertThat(user_.getUsername()).isEqualTo(admin.getUsername());
			}

		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "unable.to.save.admin.user");
		}

	}

}
