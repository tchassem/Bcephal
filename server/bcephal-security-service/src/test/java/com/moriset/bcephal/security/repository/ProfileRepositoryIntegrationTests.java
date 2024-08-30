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
import com.moriset.bcephal.security.domain.Client;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileType;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileRepositoryIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.multitenant.jpa",
			"com.moriset.bcephal.security" }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.security", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests {
	}

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ClientRepository clientRepository;

	private static Profile profile;

	@BeforeAll
	static void init() {
		profile = new SecurityFactory().buildProfile();
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getId()).isNull();
	}

//	@Test
//	@DisplayName("Validate profile bean.")
//	@Order(1)
//	public void validateTest() throws JsonMappingException, JsonProcessingException {
//		Profile profile = new SecurityFactory().buildProfile();
//		profile.setType(null);
//		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//			entityManager.persist(profile);
//			entityManager.flush();
//		});
//		profile.setType(ProfileType.ADMINISTRATOR);
//
//		profile.setName(null);
//		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//			entityManager.persist(profile);
//			entityManager.flush();
//		});
//		
//		profile.setName("");
//		for (int i = 1; i <= 100; i++) {
//			profile.setName(profile.getName().concat("A"));
//		}
//
//		entityManager.persist(profile);
//		entityManager.flush();
//		Assertions.assertThat(profile).isNotNull();
//		Assertions.assertThat(profile.getId()).isNotNull();
//
//		profile.setName("");
//		for (int i = 1; i < 105; i++) {
//			profile.setName(profile.getName().concat("A"));
//		}
//		
//		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//			entityManager.persist(profile);
//			entityManager.flush();
//		});
//
//		profile.setName("");
//		for (int i = 1; i <= 74; i++) {
//			profile.setName(profile.getName().concat("A"));
//		}
//
//		entityManager.persist(profile);
//		entityManager.flush();
//		Assertions.assertThat(profile).isNotNull();
//		Assertions.assertThat(profile.getId()).isNotNull();
//	}

	@Test
	@DisplayName("Save a new profile.")
	@Order(2)
	@Commit
	public void saveprofileTest() {
		Assertions.assertThat(profile).isNotNull();
		profileRepository.save(profile);
		Assertions.assertThat(profile.getId()).isNotNull();
		Optional<Profile> found = profileRepository.findById(profile.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getName()).isEqualTo(profile.getName());
		Assertions.assertThat(found.get().getType()).isEqualTo(profile.getType());
		Assertions.assertThat(found.get().getCode()).isEqualTo(profile.getCode());
	}

	@Test
	@DisplayName("Validate update an existing profile.")
	@Order(3)
	@Commit
	public void updateProfileTest() {
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getId()).isNotNull();
		profile.setType(ProfileType.USER);
		profileRepository.save(profile);
		Assertions.assertThat(profile.getId()).isNotNull();
		Optional<Profile> found = profileRepository.findById(profile.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getType()).isEqualTo(profile.getType());
	}

	@Test
	@DisplayName("Validate find by client.")
	@Order(4)
	@Commit
	public void findByClientTest() {
		Assertions.assertThat(profile.getId()).isNotNull();
		Assertions.assertThat(profile.getClient()).isNotNull();
		List<Profile> founds = profileRepository.findByClient(profile.getClient());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate find by clientId order by name Ascending . ")
	@Order(5)
	@Commit
	public void findByClientIdOrderByNameAscTest() {
		Assertions.assertThat(profile.getId()).isNotNull();
		Assertions.assertThat(profile.getClientId()).isNotNull();

		Client client = new SecurityFactory().buildClient();
		clientRepository.save(client);
		Assertions.assertThat(client.getId()).isNotNull();
		Assertions.assertThat(client.getClientId()).isNotNull();
		Assertions.assertThat(client.getClientId().toString()).isEqualTo(profile.getClientId().toString());

		List<Profile> founds = profileRepository.findByClientIdOrderByNameAsc(profile.getClientId());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);
		
		clientRepository.deleteById(client.getId());
		Optional<Client> found = clientRepository.findById(client.getId());
		Assertions.assertThat(found.isPresent()).isFalse();
	}

	@Test
	@DisplayName("Validate find by clientId and name. ")
	@Order(6)
	@Commit
	public void findByClientIdAndNameTest() {
		Assertions.assertThat(profile.getId()).isNotNull();
		Assertions.assertThat(profile.getClientId()).isNotNull();
		Assertions.assertThat(profile.getName()).isNotNull();

		Client client = new SecurityFactory().buildClient();
		clientRepository.save(client);
		Assertions.assertThat(client.getId()).isNotNull();
		Assertions.assertThat(client.getClientId()).isNotNull();
		Assertions.assertThat(client.getClientId().toString()).isEqualTo(profile.getClientId().toString());

		List<Profile> founds = profileRepository.findByClientIdAndName(profile.getClientId(), profile.getName());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);
		
		clientRepository.deleteById(client.getId());
		Optional<Client> found = clientRepository.findById(client.getId());
		Assertions.assertThat(found.isPresent()).isFalse();
	}

	@Test
	@DisplayName("Validate find by profile type. ")
	@Order(7)
	@Commit
	public void findByTypeTest() {
		Assertions.assertThat(profile.getId()).isNotNull();
		Assertions.assertThat(profile.getType()).isNotNull();
		List<Profile> founds = profileRepository.findByType(profile.getType());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Delete by clientId.")
	@Order(8)
	@Commit
	public void deleteByClientIdTest() {
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getClientId()).isNotNull();
		profileRepository.deleteByClientId(profile.getClientId());
		Optional<Profile> found = profileRepository.findById(profile.getId());
		Assertions.assertThat(found.isPresent()).isFalse();
	}

	@Test
	@DisplayName("Delete profile.")
	@Order(10)
	@Commit
	public void deleteProfileTest() {
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getId()).isNotNull();
		profileRepository.deleteById(profile.getId());
		Optional<Profile> found = profileRepository.findById(profile.getId());
		Assertions.assertThat(found.isPresent()).isFalse();
	}

}
