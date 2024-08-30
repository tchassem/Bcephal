package com.moriset.bcephal.security.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

//@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles(value = "test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectRepositoryTests {
	
//	@SpringBootApplication(
//			scanBasePackages = { 
//					"com.moriset.bcephal.project", 
//					"com.moriset.bcephal.security",
//					"com.moriset.bcephal.multitenant.jpa", 
//					"com.moriset.bcephal.config" }
//			)
//	@EntityScan(basePackages = { "com.moriset.bcephal.project.domain", "com.moriset.bcephal.security.domain" })
//	@ActiveProfiles("test")
//	static class ApplicationTests {}
//
//	@Autowired
//	private ProjectRepository projectRepository;
//	
//	
//	
//	
//	@Test
//	@DisplayName("Save project")
//	@Order(1)
//	public void saveProjectTest() {
//		Project project = buildProject();
//		Assertions.assertThat(project).isNotNull();
//		
//		Project savedProject = projectRepository.save(project);
//		Assertions.assertThat(savedProject).isNotNull();
//		Assertions.assertThat(savedProject.getId()).isGreaterThan(0);
//	}
//	
//	//@Test
//	@DisplayName("Find project by code")
//	@Order(2)
//	public void findProjectByCodeTest() {
//		Project project = buildProject();
//		Assertions.assertThat(project).isNotNull();
//		
//		Optional<Project> savedProject = projectRepository.findByCode(project.getCode());
//		Assertions.assertThat(savedProject).isNotEmpty();
//		Assertions.assertThat(savedProject.get().getName()).isEqualTo(project.getName());
//		
//		savedProject = projectRepository.findByCode(project.getCode() + "none");
//		Assertions.assertThat(savedProject).isEmpty();
//	}
//		
//	
//	Project buildProject() {
//		Project project = Project.builder()
//				.code("" + System.currentTimeMillis())
//				.name("My first project")
//				.subscriptionId(1L)
//				.client("Default Client")
//				.defaultProject(false)
//				.creationDate(new Timestamp(System.currentTimeMillis()))
//				.build();
//		return project;
//	}
	
}
