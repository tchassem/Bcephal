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
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileProject;
import com.moriset.bcephal.security.domain.Project;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileProjectRepositoryIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.profileProject",
			"com.moriset.bcephal.security.domain", "com.moriset.bcephal.multitenant.jpa",
			"com.moriset.bcephal.config" }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.security", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests {
	}

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	ProfileProjectRepository profileProjectRepository;

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ProjectRepository projectRepository;

	private static ProfileProject profileProject;

	@BeforeAll
	static void init() {
		profileProject = new SecurityFactory().buildprofileProject();
		Assertions.assertThat(profileProject).isNotNull();
		Assertions.assertThat(profileProject.getId()).isNull();
	}

	@Test
	@DisplayName("Validate profileProject bean.")
	@Order(1)
	public void validateTest() {

		ProfileProject profileProject = new SecurityFactory().buildprofileProject();
		entityManager.persist(profileProject);
		entityManager.flush();
		Assertions.assertThat(profileProject.getId()).isNotNull();
	}

	@Test
	@DisplayName("Validate save profileProject.")
	@Order(2)
	public void saveProfileProjectTest() {
		Assertions.assertThat(profileProject).isNotNull();
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();
		Optional<ProfileProject> found = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getProjectCode()).isEqualTo(profileProject.getProjectCode());
		Assertions.assertThat(found.get().getPosition()).isEqualTo(profileProject.getPosition());
	}

	@Test
	@DisplayName("Validate update profileProject .")
	@Order(3)
	public void updateProfileProjectTest() {

		Assertions.assertThat(profileProject).isNotNull();
		profileProject.setPosition(7);
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();
		Optional<ProfileProject> found = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
		Assertions.assertThat(found.get().getPosition()).isEqualTo(profileProject.getPosition());
		Assertions.assertThat(found.get().getProjectCode()).isEqualTo(profileProject.getProjectCode());
	}

	@Test
	@DisplayName("Validate find profileProject By ProfileId .")
	@Order(4)
	public void findByProfileIdTest() {

		Profile profile = new SecurityFactory().buildProfile();
		profileRepository.save(profile);
		Assertions.assertThat(profile.getId()).isNotNull();

		profileProject.setProfileId(profile.getId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		List<ProfileProject> founds = profileProjectRepository.findByProfileId(profileProject.getProfileId());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);

		profileRepository.deleteById(profile.getId());
		Optional<Profile> profiles = profileRepository.findById(profile.getId());
		Assertions.assertThat(profiles.isEmpty()).isTrue();

	}

	@Test
	@DisplayName("Validate find profileProject By ProjectId .")
	@Order(5)
	public void findByProjectIdTest() {

		Optional<Project> project = projectRepository.findByCode("1703147080276");
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.get().getId()).isNotNull();
		profileProject.setProjectId(project.get().getId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();
		List<ProfileProject> founds = profileProjectRepository.findByProjectId(profileProject.getProjectId());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);

	}

	@Test
	@DisplayName("Validate find find By ProjectCode .")
	@Order(6)
	public void findByProjectCodeTest() {

		Optional<Project> project = projectRepository.findByCode("1703147080276");
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.get().getId()).isNotNull();
		Assertions.assertThat(project.get().getCode()).isNotNull();
		profileProject.setProjectCode(project.get().getCode());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		List<ProfileProject> founds = profileProjectRepository.findByProjectCode(profileProject.getProjectCode());
		Assertions.assertThat(founds).isNotEmpty();
		Assertions.assertThat(founds.size()).isGreaterThan(0);

	}

	@Test
	@DisplayName("Validate delete By ProfileId And ProjectId .")
	@Order(7)
	public void deleteByProfileIdAndProjectIdTest() {

		Optional<Project> project = projectRepository.findByCode("1703147080276");
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.get().getId()).isNotNull();
		profileProject.setProjectId(project.get().getId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		Profile profile = new SecurityFactory().buildProfile();
		profileRepository.save(profile);
		Assertions.assertThat(profile.getId()).isNotNull();
		profileProject.setProfileId(profile.getId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		profileProjectRepository.deleteByProfileIdAndProjectId(profileProject.getProfileId(),
				profileProject.getProjectId());
		Optional<ProfileProject> found = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(found.isEmpty()).isTrue();

		profileRepository.deleteById(profile.getId());
		Optional<Profile> profiles = profileRepository.findById(profile.getId());
		Assertions.assertThat(profiles.isEmpty()).isTrue();

	}

	@Test
	@DisplayName("Validate delete By ProfileId And ProjectCode .")
	@Order(8)
	public void deleteByProfileIdAndProjectCodeTest() {

		Optional<Project> project = projectRepository.findByCode("1703147080276");
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.get().getId()).isNotNull();
		Assertions.assertThat(project.get().getCode()).isNotNull();
		profileProject.setProjectCode(project.get().getCode());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		Profile profile = new SecurityFactory().buildProfile();
		profileRepository.save(profile);
		Assertions.assertThat(profile.getId()).isNotNull();
		profileProject.setProfileId(profile.getId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		profileProjectRepository.deleteByProfileIdAndProjectCode(profileProject.getProfileId(),
				profileProject.getProjectCode());
		Optional<ProfileProject> found = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(found.isEmpty()).isTrue();

		profileRepository.deleteById(profile.getId());
		Optional<Profile> profiles = profileRepository.findById(profile.getId());
		Assertions.assertThat(profiles.isEmpty()).isTrue();

	}

	@Test
	@DisplayName("Validate delete By ProfileId .")
	@Order(9)
	public void deleteByProfileIdTest() {
		Assertions.assertThat(profileProject).isNotNull();
		Assertions.assertThat(profileProject.getId()).isNotNull();

		Profile profile = new SecurityFactory().buildProfile();
		profileRepository.save(profile);
		Assertions.assertThat(profile.getId()).isNotNull();
		profileProject.setProfileId(profile.getId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		profileProjectRepository.deleteByProfileId(profileProject.getProfileId());
		Optional<ProfileProject> found = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(found.isEmpty()).isTrue();

		profileRepository.deleteById(profile.getId());
		Optional<Profile> profiles = profileRepository.findById(profile.getId());
		Assertions.assertThat(profiles.isEmpty()).isTrue();

	}

	@Test
	@DisplayName("Validate delete By ProjectId .")
	@Order(10)
	public void deleteByProjectIdTest() {

		Optional<Project> project = projectRepository.findByCode("1703147080276");
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.get().getId()).isNotNull();
		profileProject.setProjectId(project.get().getId());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		profileProjectRepository.deleteByProjectId(profileProject.getProjectId());

		Optional<ProfileProject> found = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(found.isEmpty()).isTrue();

	}

	@Test
	@DisplayName("Validate delete By ProfileCode .")
	@Order(11)
	public void deleteByProjectCodeTest() {

		Optional<Project> project = projectRepository.findByCode("1703147080276");
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.get().getId()).isNotNull();
		Assertions.assertThat(project.get().getCode()).isNotNull();
		profileProject.setProjectCode(project.get().getCode());
		profileProjectRepository.save(profileProject);
		Assertions.assertThat(profileProject.getId()).isNotNull();

		profileProjectRepository.deleteByProjectCode(profileProject.getProjectCode());

		Optional<ProfileProject> found = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(found.isEmpty()).isTrue();

	}

	@Test
	@DisplayName("Validate delete profileProject .")
	@Order(15)
	public void DeleteProfileProjectTest() {
		Assertions.assertThat(profileProject).isNotNull();
		profileProjectRepository.deleteById(profileProject.getId());
		Optional<ProfileProject> profileProjects = profileProjectRepository.findById(profileProject.getId());
		Assertions.assertThat(profileProjects.isEmpty()).isTrue();
	}
}
