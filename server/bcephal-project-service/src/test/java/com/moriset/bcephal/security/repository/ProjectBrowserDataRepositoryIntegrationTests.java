/**
 * 
 */
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
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.project.ProjectFactory;
import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.domain.ProjectProfile;

import jakarta.validation.ConstraintViolationException;


@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ProjectBrowserDataRepositoryIntegrationTests {
	
	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.ProjectBrowserData", 
					"com.moriset.bcephal.security",
					"com.moriset.bcephal.multitenant.jpa", 
					"com.moriset.bcephal.config" 
					}
			)
	@EntityScan(basePackages = { "com.moriset.bcephal.project.domain", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {}
	
	@Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectBrowserDataRepository projectBrowserDataRepository;
    @Autowired
    private ProjectProfileRepository projectProfileRepository;

    private static ProjectBrowserData projectBrowserData;
    private static ProjectProfile projectProfile;
    
    @BeforeAll
    static void init(){
    	projectBrowserData = new ProjectFactory().buildProjectBrowserData();    	
    	
    	projectProfile = new ProjectProfile();
    	projectProfile.setProjectCode("" + System.currentTimeMillis());
    	projectProfile.setProfileId(1L);
    	
    	Assertions.assertThat(projectBrowserData).isNotNull();
    	Assertions.assertThat(projectBrowserData.getId()).isNull();
    }
    
    @Test
    @DisplayName("Validate projectBrowserData bean.")
    @Order(1)
	public void validateTest() {
	    ProjectBrowserData projectBrowserData = new ProjectBrowserData();
	    projectBrowserData.setCode(null);
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(projectBrowserData);
	    	entityManager.flush();
	    });
	    
	    projectBrowserData.setCode("");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(projectBrowserData);
	    	entityManager.flush();
	    });
	    	    
	    projectBrowserData.setCode("12");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(projectBrowserData);
	    	entityManager.flush();
	    });
	    
	    projectBrowserData.setCode("");
        for(int i = 1; i < 55; i++) {
        	projectBrowserData.setCode(projectBrowserData.getCode().concat("A"));
        }
        org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(projectBrowserData);
	    	entityManager.flush();
	    });
	    
	}      
    
    @Test
    @DisplayName("Save a new projectBrowserData")
    @Order(2)
    @Commit
	public void saveProjectBrowserDataTest() {
    	Assertions.assertThat(projectBrowserData).isNotNull();
	    projectBrowserDataRepository.save(projectBrowserData);	 
	    Assertions.assertThat(projectBrowserData.getId()).isNotNull();
	    Optional<ProjectBrowserData> found = projectBrowserDataRepository.findById(projectBrowserData.getId());
	    projectProfile.setProjectId(found.get().getId());
	    projectProfileRepository.save(projectProfile);
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getCode()).isEqualTo(projectBrowserData.getCode());
	}
    
    @Test
    @DisplayName("Update an existing projectBrowserData")
    @Order(3)
    @Commit
	public void updateProjectBrowserDataTest() {
    	Assertions.assertThat(projectBrowserData).isNotNull();
    	Assertions.assertThat(projectBrowserData.getId()).isNotNull();
    	projectBrowserData.setDefaultProject(!projectBrowserData.isDefaultProject());
    	projectBrowserDataRepository.save(projectBrowserData);
	    Assertions.assertThat(projectBrowserData.getId()).isNotNull();
	    Optional<ProjectBrowserData> found = projectBrowserDataRepository.findById(projectBrowserData.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().isDefaultProject()).isEqualTo(projectBrowserData.isDefaultProject());
	}
    
	@Test
	@DisplayName("Find projectBrowserData by code")
	@Order(4)
	public void findProjectBrowserDataByCode() {
		Assertions.assertThat(projectBrowserData).isNotNull();	 
	    Optional<ProjectBrowserData> found = projectBrowserDataRepository.findByCode(projectBrowserData.getCode());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getCode()).isEqualTo(projectBrowserData.getCode());
	}
	
	@Test
	@DisplayName("Find projectBrowserData by name ignore case")
	@Order(5)
	public void findProjectBrowserDataByNameIgnoreCase() {
		Assertions.assertThat(projectBrowserData).isNotNull();	 
		List<ProjectBrowserData> found = projectBrowserDataRepository.findByNameIgnoreCase(projectBrowserData.getName());
	    Assertions.assertThat(found.isEmpty()).isFalse();
	    Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).getName()).isEqualTo(projectBrowserData.getName());
	}
	
	@Test
	@DisplayName("Find projectBrowserData by subscriptionId and default project")
	@Order(6)
	public void findProjectBrowserDataBySubscriptionIdAndDefaultProject() {
		Assertions.assertThat(projectBrowserData).isNotNull();
		List<ProjectBrowserData> found = projectBrowserDataRepository.findBySubscriptionIdAndDefaultProject(projectBrowserData.getSubscriptionId(), projectProfile.getProfileId(), projectBrowserData.isDefaultProject());
		Assertions.assertThat(found.isEmpty()).isFalse();
		Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).getSubscriptionId()).isEqualTo(projectBrowserData.getSubscriptionId());
	    Assertions.assertThat(found.get(0).isDefaultProject()).isEqualTo(projectBrowserData.isDefaultProject());
	}	
	
	@Test
	@DisplayName("Find projectBrowserData by subscriptionId and name ignore case")
	@Order(7)
	public void findProjectBrowserDataBySubscriptionIdAndNameIgnoreCase() {
		Assertions.assertThat(projectBrowserData).isNotNull();	 
		List<ProjectBrowserData> found = projectBrowserDataRepository.findBySubscriptionIdAndNameIgnoreCase(projectBrowserData.getSubscriptionId(), projectBrowserData.getName());
	    Assertions.assertThat(found.isEmpty()).isFalse();
	    Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).getSubscriptionId()).isEqualTo(projectBrowserData.getSubscriptionId());
	    Assertions.assertThat(found.get(0).getName()).isEqualTo(projectBrowserData.getName());
	}

	@Test
	@DisplayName("Find projectBrowserData by subscriptionId")
	@Order(8)
	public void findProjectBrowserDataBySubscriptionId() {
		Assertions.assertThat(projectBrowserData).isNotNull();	 
		List<ProjectBrowserData> found = projectBrowserDataRepository.findBySubscriptionId(projectBrowserData.getSubscriptionId());
		Assertions.assertThat(found.isEmpty()).isFalse();
	   // Assertions.assertThat(found.size()).isEqualTo(14);
	    Assertions.assertThat(found.get(0).getSubscriptionId()).isEqualTo(projectBrowserData.getSubscriptionId());
	}
	
	@Test
	@DisplayName("Get projectBrowserData by subscriptionId and profileId")
	@Order(9)
	public void getProjectBrowserDataBySubscriptionIdAndProfileId() {
		Assertions.assertThat(projectBrowserData).isNotNull();
		List<ProjectBrowserData> found = projectBrowserDataRepository.getBySubscriptionIdAndProfileId(projectBrowserData.getSubscriptionId(), projectProfile.getProfileId());
		Assertions.assertThat(found.isEmpty()).isFalse();
		Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).getSubscriptionId()).isEqualTo(projectBrowserData.getSubscriptionId());
	}		
	
	@Test
	@DisplayName("Get projectBrowserData by subscriptionId and profileId and project name")
	@Order(10)
	public void getProjectBrowserDataBySubscriptionIdAndProfileIdAndProjectName() {
		Assertions.assertThat(projectBrowserData).isNotNull();
		List<ProjectBrowserData> found = projectBrowserDataRepository.getBySubscriptionIdAndProfileIdAndProjectName(projectBrowserData.getSubscriptionId(), projectProfile.getProfileId(), projectBrowserData.getName());
		Assertions.assertThat(found.isEmpty()).isFalse();
		Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).getSubscriptionId()).isEqualTo(projectBrowserData.getSubscriptionId());
	    Assertions.assertThat(found.get(0).getName()).isEqualTo(projectBrowserData.getName());
	}		
	
	@Test
	@DisplayName("Find projectBrowserData by subscriptionId and code")
	@Order(11)
	public void findProjectBrowserDataBySubscriptionIdAndCode() {
		Assertions.assertThat(projectBrowserData).isNotNull();
		Optional<ProjectBrowserData> found = projectBrowserDataRepository.findBySubscriptionIdAndCode(projectBrowserData.getSubscriptionId(), projectBrowserData.getCode());
		Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getSubscriptionId()).isEqualTo(projectBrowserData.getSubscriptionId());
	    Assertions.assertThat(found.get().getCode()).isEqualTo(projectBrowserData.getCode());
	}	
	
	@Test
	@DisplayName("Find projectBrowserData by subscriptionId and Id")
	@Order(11)
	public void findProjectBrowserDataBySubscriptionIdAndId() {
		Assertions.assertThat(projectBrowserData).isNotNull();
		Optional<ProjectBrowserData> found = projectBrowserDataRepository.findBySubscriptionIdAndId(projectBrowserData.getSubscriptionId(), projectBrowserData.getId());
		Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getSubscriptionId()).isEqualTo(projectBrowserData.getSubscriptionId());
	    Assertions.assertThat(found.get().getId()).isEqualTo(projectBrowserData.getId());
	}	
    
    @Test
    @DisplayName("Delete projectBrowserData by code.")
    @Order(15)
    @Commit
	public void deleteProjectBrowserDataByCode() {
    	Assertions.assertThat(projectBrowserData).isNotNull();

	    projectProfileRepository.delete(projectProfile);
	    projectBrowserDataRepository.delete(projectBrowserData);
	    
	    Optional<ProjectBrowserData> found = projectBrowserDataRepository.findByCode(projectBrowserData.getCode());	 
	    Assertions.assertThat(found.isEmpty()).isTrue();
	}
    
}
