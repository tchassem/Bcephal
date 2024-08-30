package com.moriset.bcephal.security.repository;

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
import com.moriset.bcephal.security.domain.UserInfo;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserInfoRepositoryIntegrationTests {
	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.userInfo", "com.moriset.bcephal.security.domain",
			"com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config" }, exclude = {
					DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.security", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests {
	}


	@Autowired
	UserInfoRepository userInfoRepository;
	
	private static UserInfo userInfo;
	
	@BeforeAll
	static void init() {
		userInfo = new SecurityFactory().buildUserInfo();
		Assertions.assertThat(userInfo).isNotNull();
		Assertions.assertThat(userInfo.getId()).isNull();
	}

	@Test
	@DisplayName("Validate save userInfo .")
	@Order(1)
	@Commit
	public void SaveUserInfoTest() {
		Assertions.assertThat(userInfo).isNotNull();
		userInfoRepository.save(userInfo);
		Assertions.assertThat(userInfo.getId()).isNotNull();
		Optional<UserInfo> found = userInfoRepository.findById(userInfo.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getName()).isEqualTo(userInfo.getName());
		Assertions.assertThat(found.get().getUsername()).isEqualTo(userInfo.getUsername());
		Assertions.assertThat(found.get().getType()).isEqualTo(userInfo.getType());
	}

	
	@Test
	@DisplayName("Validate find by username ignoreCase .")
	@Order(2)
	public void findByUsernameIgnoreCaseTest() {
		UserInfo userInfo = new SecurityFactory().buildUserInfo();
		Assertions.assertThat(userInfo).isNotNull();
		Assertions.assertThat(userInfo.getUsername()).isNotNull();
		Optional<UserInfo> userInfo_ = userInfoRepository.findByUsernameIgnoreCase(userInfo.getUsername());
		Assertions.assertThat(userInfo_.isPresent()).isTrue();
		Assertions.assertThat(userInfo_.get().getId()).isNotNull();
		Assertions.assertThat(userInfo_.get().getUsername()).isEqualTo(userInfo.getUsername());
	}
	
	@Test
	@DisplayName("Validate find by username ignoreCase .")
	@Order(3)
	public void findByUsernameTest() {
		UserInfo userInfo = new SecurityFactory().buildUserInfo();
		Assertions.assertThat(userInfo).isNotNull();
		Assertions.assertThat(userInfo.getUsername()).isNotNull();
		Optional<UserInfo> userInfo_ = userInfoRepository.findByUsername(userInfo.getUsername());
		Assertions.assertThat(userInfo_.isPresent()).isTrue();
		Assertions.assertThat(userInfo_.get().getId()).isNotNull();
		Assertions.assertThat(userInfo_.get().getUsername()).isEqualTo(userInfo.getUsername());
	}
	
	@Test
	@DisplayName("Validate find by username and clientId .")
	@Order(4)
	public void findByUsernameAndClientIdTest() {
		UserInfo userInfo = new SecurityFactory().buildUserInfo();
		Assertions.assertThat(userInfo).isNotNull();
		Assertions.assertThat(userInfo.getUsername()).isNotNull();
		Assertions.assertThat(userInfo.getClientId()).isNotNull();
		Optional<UserInfo> userInfo_ = userInfoRepository.findByUsernameAndClientId(userInfo.getUsername(), userInfo.getClientId());
		Assertions.assertThat(userInfo_.isPresent()).isTrue();
		Assertions.assertThat(userInfo_.get().getId()).isNotNull();
		Assertions.assertThat(userInfo_.get().getUsername()).isEqualTo(userInfo.getUsername());
	}
	
	@Test
	@DisplayName("Validate update userInfo .")
	@Order(5)
	@Commit
	public void updateUserInfoTest() {
		Assertions.assertThat(userInfo).isNotNull();
		userInfo.setFirstName("AABC");
		userInfoRepository.save(userInfo);
		Assertions.assertThat(userInfo.getId()).isNotNull();
		Optional<UserInfo> found = userInfoRepository.findById(userInfo.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getFirstName()).isEqualTo(userInfo.getFirstName());
	}
	

	
	@Test
	@DisplayName("Validate delete userInfo .")
	@Order(8)
	@Commit
	public void deleteUserInfoTest() {
		Assertions.assertThat(userInfo).isNotNull();
		userInfoRepository.deleteById(userInfo.getId());
		Optional<UserInfo> userInfo_ = userInfoRepository.findById(userInfo.getId());
		Assertions.assertThat(userInfo_.isEmpty()).isTrue();
	}


}
