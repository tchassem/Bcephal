package com.moriset.bcephal.security.service;

import java.util.List;
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
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.security.SecurityFactory;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileProject;
import com.moriset.bcephal.security.domain.ProfileType;
import com.moriset.bcephal.security.repository.ProfileProjectRepository;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "serv" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileServiceIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.security",
			"com.moriset.bcephal.multitenant.jpa", }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class, })
	@EntityScan(basePackages = { "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "serv" })
	static class ApplicationTests {
	}

	@Autowired
	private ProfileService profileService;

	@Autowired
	ProfileProjectRepository profileProjectRepository;

	@Autowired
	protected MessageSource messageSource;

	private static Profile profile;
	
	private static Profile profile_;

	@BeforeAll
	static void init() {
		profile = new SecurityFactory().buildProfile();
		profile_ = new SecurityFactory().buildProfile1();
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getId()).isNull();
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getId()).isNull();
	}

	@Test
	@DisplayName("Validate profile bean.")
	@Order(1)
	@Commit
	public void validateTest() {
		Profile profile = new SecurityFactory().buildProfile();

		/** validate name à revoir **/

		profile.setType(null);
//		org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
//			profileService.save(profile, Locale.FRENCH);
//		});
		var throwable = org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
			profileService.save(profile,profile.getClientId(), Locale.FRENCH, new SecurityFactory().getProjectCode());
		});
		Assertions.assertThat(throwable).asString().contains("unable.to.save.model");

	}

	@Test
	@DisplayName("Save a new profile.")
	@Order(2)
	@Commit
	public void saveprofileTest() {
		Assertions.assertThat(profile).isNotNull();
		profile.setType(ProfileType.GUEST);		
	    profileService.save(profile, profile.getClientId(), Locale.FRENCH, new SecurityFactory().getProjectCode());
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getId()).isNotNull();
	}

//	@Test
//	@DisplayName("Validate update an existing profile.")
//	@Order(3)
//	@Commit
//	public void updateProfileTest() {
//		Assertions.assertThat(profile).isNotNull();
//		Assertions.assertThat(profile.getId()).isNotNull();
//		profile.setType(ProfileType.USER);
//		profileService.save(profile, profile.getClientId(), Locale.FRENCH, new SecurityFactory().getProjectCode());
//		Assertions.assertThat(profile.getId()).isNotNull();
//		Profile found = profileService.getById(profile.getId());
//		Assertions.assertThat(found).isNotNull();
//		Assertions.assertThat(found.getType()).isEqualTo(profile.getType());
//	}

	@Test
	@DisplayName("Validate find by client.")
	@Order(4)
	@Commit
	public void findByClientTest() {
		Assertions.assertThat(profile.getId()).isNotNull();
		Assertions.assertThat(profile.getClient()).isNotNull();
		List<Profile> founds = profileService.findByClient(profile.getClient());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Validate get by id.")
	@Order(5)
	@Commit
	public void getByIdTest() {
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getId()).isNotNull();
		Assertions.assertThat(profile.getClientId()).isNotNull();
		ProfileProject profileProject = new SecurityFactory().buildprofileProject();
		profileProject.setProfileId(profile.getId());
		profileProject.setClientId(profile.getClientId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject).isNotNull();
		Assertions.assertThat(profileProject.getId()).isNotNull();
		Profile found = profileService.getById(profile.getId(), profile.getClientId(), profileProject.getProjectCode());
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getId()).isNotNull();
	}

	@Test
	@DisplayName("Delete by clientId.")
	@Order(8)
	@Commit
	public void deleteByClientIdTest() {
		Assertions.assertThat(profile).isNotNull();
		Assertions.assertThat(profile.getClientId()).isNotNull();
		profileService.deleteByClientId(profile.getClientId());
		Profile found = profileService.getById(profile.getId());
		Assertions.assertThat(found).isNull();
	}
	
	@Test
	@DisplayName("Save another profile.")
	@Order(9)
	@Commit
	public void saveprofile_Test() {
		Assertions.assertThat(profile_).isNotNull();
		profile_ = profileService.save(profile_, profile_.getClientId(), Locale.FRENCH, new SecurityFactory().getProjectCode());
		Assertions.assertThat(profile_).isNotNull();
		Assertions.assertThat(profile_.getId()).isNotNull();
	}

//	@Test
//	@DisplayName("Delete profile.")
//	@Order(10)
//	@Commit
//	public void deleteProfile_Test() {
//		Assertions.assertThat(profile_).isNotNull();
//		Assertions.assertThat(profile_.getId()).isNotNull();
//		//profile.setCode(null);// for keycloack and groupsResource
//		profileService.delete(profile_);
//		Profile found = profileService.getById(profile_.getId());
//		Assertions.assertThat(found).isNull();
//	}

}
