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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.security.SecurityFactory;
import com.moriset.bcephal.security.domain.Right;
import com.moriset.bcephal.security.domain.RightLevel;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RightRepositoryIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.right", "com.moriset.bcephal.security.domain",
			"com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config" }, exclude = {
					DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.security", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests {
	}

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	RightRepository rightRepository;

	@Autowired
	ProfileRepository profileRepository;

	private static Right right;

	@BeforeAll
	static void init() {
		right = new SecurityFactory().buildRight();
		Assertions.assertThat(right).isNotNull();
		Assertions.assertThat(right.getId()).isNull();
	}

	@Test
	@DisplayName("Validate right bean.")
	@Order(1)
	public void validateTest() {

		Right right = new SecurityFactory().buildRight();
		right.setFunctionality(null);
		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(right);
			entityManager.flush();
		});

		right.setFunctionality("");
		for (int i = 1; i < 5; i++) {
			right.setFunctionality(right.getFunctionality().concat("F"));
		}
		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(right);
			entityManager.flush();
		});

		right.setFunctionality("");
		for (int i = 1; i < 270; i++) {
			right.setFunctionality(right.getFunctionality().concat("F"));
		}
		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(right);
			entityManager.flush();
		});

		right.setFunctionality("");
		for (int i = 1; i < 6; i++) {
			right.setFunctionality(right.getFunctionality().concat("F"));
		}
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
			entityManager.persist(right);
			entityManager.flush();
		});

		right.setFunctionality("");
		for (int i = 1; i < 256; i++) {
			right.setFunctionality(right.getFunctionality().concat("F"));
		}
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
			entityManager.persist(right);
			entityManager.flush();
		});

		right.setFunctionality("");
		for (int i = 1; i < 100; i++) {
			right.setFunctionality(right.getFunctionality().concat("F"));
		}
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
			entityManager.persist(right);
			entityManager.flush();
		});

		right.setLevel(null);
		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(right);
			entityManager.flush();
		});

	}

	@Test
	@DisplayName("Validate save right .")
	@Order(2)
	public void SaveRightTest() {
		Assertions.assertThat(right).isNotNull();
		rightRepository.save(right);
		Assertions.assertThat(right.getId()).isNotNull();
		Optional<Right> found = rightRepository.findById(right.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getFunctionality()).isEqualTo(right.getFunctionality());
		Assertions.assertThat(found.get().getProjectCode()).isEqualTo(right.getProjectCode());
	}

	@Test
	@DisplayName("Validate update right .")
	@Order(3)
	public void updateRightTest() {
		Assertions.assertThat(right).isNotNull();
		right.setLevel(RightLevel.CREATE);
		rightRepository.save(right);
		Assertions.assertThat(right.getId()).isNotNull();
		Optional<Right> found = rightRepository.findById(right.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getLevel()).isEqualTo(right.getLevel());
		Assertions.assertThat(found.get().getProjectCode()).isEqualTo(right.getProjectCode());
	}

	@Test
	@DisplayName("Validate find by profile Id.")
	@Order(4)
	public void findByProfileIdTest() {
		com.moriset.bcephal.security.domain.Profile profile = new SecurityFactory().buildProfile();
		profile = profileRepository.save(profile);
		Assertions.assertThat(profile).isNotNull();
		List<Right> rights = rightRepository.findByProfileId(profile.getId());
		Assertions.assertThat(rights).isNotNull();
		profileRepository.delete(profile);
	}

	@Test
	@DisplayName("Validate find by profileId and objectId is null or functionality .")
	@Order(5)
	public void findByProfileIdAndObjectIdIsNullOrFunctionalityLikeTest() {
		com.moriset.bcephal.security.domain.Profile profile = new SecurityFactory().buildProfile();
		profile = profileRepository.save(profile);
		Assertions.assertThat(profile).isNotNull();
		right.setProfileId(profile.getId());
		rightRepository.save(right);
		List<Right> rights = rightRepository.findByProfileIdAndObjectIdIsNullOrFunctionalityLike(right.getProfileId(),
				right.getProjectCode(), right.getFunctionality());
		Assertions.assertThat(rights.isEmpty()).isFalse();
	}

	@Test
	@DisplayName("Validate find by profileId, functionality , objectId , projectCode.")
	@Order(6)
	public void findByProfileIdAndFunctionalityAndObjectIdAndProjectCodeTest() {
		com.moriset.bcephal.security.domain.Profile profile = new SecurityFactory().buildProfile();
		profile = profileRepository.save(profile);
		Assertions.assertThat(profile).isNotNull();
		right.setProfileId(profile.getId());
		right.setObjectId(3L);
		rightRepository.save(right);
		List<Right> rights = rightRepository.findByProfileIdAndFunctionalityAndObjectIdAndProjectCode(
				right.getProfileId(), right.getFunctionality(), right.getObjectId(), right.getProjectCode());
		Assertions.assertThat(rights.isEmpty()).isFalse();
	}

	@Test
	@DisplayName("Validate find by profileId, functionality , projectCode.")
	@Order(7)
	public void findByProfileIdAndFunctionalityAndProjectCodeTest() {
		com.moriset.bcephal.security.domain.Profile profile = new SecurityFactory().buildProfile();
		profile = profileRepository.save(profile);
		Assertions.assertThat(profile).isNotNull();
		right.setProfileId(profile.getId());
		rightRepository.save(right);
		List<Right> rights = rightRepository.findByProfileIdAndFunctionalityAndProjectCode(right.getProfileId(),
				right.getFunctionality(), right.getProjectCode());
		Assertions.assertThat(rights.isEmpty()).isFalse();
	}

	@Test
	@DisplayName("Validate find by profileId , projectCode.")
	@Order(8)
	public void findByProfileIdAndProjectCodeTest() {
		com.moriset.bcephal.security.domain.Profile profile = new SecurityFactory().buildProfile();
		profile = profileRepository.save(profile);
		Assertions.assertThat(profile).isNotNull();
		right.setProfileId(profile.getId());
		rightRepository.save(right);
		List<Right> rights = rightRepository.findByProfileIdAndProjectCode(right.getProfileId(),
				right.getProjectCode());
		Assertions.assertThat(rights.isEmpty()).isFalse();
	}

	@Test
	@DisplayName("Validate find by profileId , projectCode, functionality .")
	@Order(9)
	public void findByProfileIdAndProjectCodeAndFunctionalityLikeTest() {
		com.moriset.bcephal.security.domain.Profile profile = new SecurityFactory().buildProfile();
		profile = profileRepository.save(profile);
		Assertions.assertThat(profile).isNotNull();
		right.setProfileId(profile.getId());
		rightRepository.save(right);
		List<Right> rights = rightRepository.findByProfileIdAndProjectCodeAndFunctionalityLike(right.getProfileId(),
				right.getProjectCode(), right.getFunctionality());
		Assertions.assertThat(rights.isEmpty()).isFalse();
	}

	@Test
	@DisplayName("Validate delete right by profileId.")
	@Order(10)
	public void DeleteRightByProfileIdTest() {
		Assertions.assertThat(right).isNotNull();
		Assertions.assertThat(right.getProfileId()).isNotNull();
		rightRepository.deleteByProfileId(right.getProfileId());
		List<Right> rights = rightRepository.findByProfileId(right.getProfileId());
		Assertions.assertThat(rights.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("Validate delete right .")
	@Order(15)
	public void DeleteRightTest() {
		Assertions.assertThat(right).isNotNull();
		rightRepository.deleteById(right.getId());
		Optional<Right> rights = rightRepository.findById(right.getId());
		Assertions.assertThat(rights.isEmpty()).isTrue();
	}

}
