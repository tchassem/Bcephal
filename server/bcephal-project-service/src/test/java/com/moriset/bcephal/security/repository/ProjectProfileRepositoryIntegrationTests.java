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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.project.ProjectFactory;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.ProjectProfile;


@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectProfileRepositoryIntegrationTests {
	
	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.ProjectProfile", 
					"com.moriset.bcephal.security",
					"com.moriset.bcephal.multitenant.jpa", 
					"com.moriset.bcephal.config" 
					}
			)
	@EntityScan(basePackages = { "com.moriset.bcephal.project.domain", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {}
	 
    @Autowired
    private ProjectProfileRepository projectProfileRepository;
    
    @Autowired
    private ProjectRepository projectRepository;    
    
    private static ProjectProfile projectProfile;
    
    private static Project project;
    
    @BeforeAll
    static void init(){
    	projectProfile = new ProjectFactory().buildProjectProfile();
    	
    	project = new ProjectFactory().buildProject();
		projectProfile.setProjectId(project.getId());
		projectProfile.setProjectCode(project.getCode());
   	
    	Assertions.assertThat(projectProfile).isNotNull();
    	Assertions.assertThat(projectProfile.getId()).isNull();
    }
    
//    @Test
//    @DisplayName("Validate project bean.")
//    @Order(1)
//	public void validateTest() {
//	    Project project = new ProjectFactory().buildProject();
//	    project.setCode(null);
//	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(project);
//	    	entityManager.flush();
//	    });
//	    
//	    project.setCode("");
//	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(project);
//	    	entityManager.flush();
//	    });
//	    	    
//	    project.setCode("12");
//	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(project);
//	    	entityManager.flush();
//	    });
//	    
//	    project.setCode("");
//        for(int i = 1; i < 55; i++) {
//        	project.setCode(project.getCode().concat("A"));
//        }
//        org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(project);
//	    	entityManager.flush();
//	    });
//	    
//	}
    
    @Test
    @DisplayName("Save a new project profile")
    @Order(2)
    @Commit
	public void saveProjectProfileTest() {
    	Assertions.assertThat(projectProfile).isNotNull();
	    projectProfileRepository.save(projectProfile);
	 
	    Assertions.assertThat(projectProfile.getId()).isNotNull();
	    Optional<ProjectProfile> found = projectProfileRepository.findById(projectProfile.getId());	 
	    project.setId(found.get().getId());
	    projectRepository.save(project);
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getId()).isEqualTo(projectProfile.getId());
	}    
    
    @Test
    @DisplayName("Update an existing projectProfile")
    @Order(3)
    @Commit
	public void updateProjectProfileTest() {
    	Assertions.assertThat(projectProfile).isNotNull();
    	Assertions.assertThat(projectProfile.getId()).isNotNull();
    	projectProfileRepository.save(projectProfile);
	    Assertions.assertThat(projectProfile.getId()).isNotNull();
	    Optional<ProjectProfile> found = projectProfileRepository.findById(projectProfile.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getId()).isEqualTo(projectProfile.getId());
	}
        
    @Test
    @DisplayName("Find project profile by ProfileId")
    @Order(4)
	public void findProjectProfileByProfileId() {
    	Assertions.assertThat(projectProfile).isNotNull();	 
	    List<ProjectProfile> found = projectProfileRepository.findByProfileId(projectProfile.getProfileId());	 
		Assertions.assertThat(found.isEmpty()).isFalse();
		Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).getProfileId()).isEqualTo(projectProfile.getProfileId());
	}
    
//    @Test
//    @DisplayName("Find project by name")
//    @Order(5)
//	public void findProjectByName() {
//    	Assertions.assertThat(project).isNotNull();	 
//	    List<Project> found = projectRepository.findByName(project.getName());	 
//	    Assertions.assertThat(found.isEmpty()).isFalse();
//	    Assertions.assertThat(found.size()).isEqualTo(1);
//	    Assertions.assertThat(found.get(0).getCode()).isEqualTo(project.getCode());
//	}
    
    
    @Test
    @DisplayName("Delete project profile")
    @Order(10)
    @Commit
	public void deleteProjectProfileByCode() {
    	Assertions.assertThat(projectProfile).isNotNull();
    	projectRepository.delete(project);
	    projectProfileRepository.delete(projectProfile);	 	    
	    
	    Optional<ProjectProfile> found = projectProfileRepository.findById(projectProfile.getId());	 
	    Assertions.assertThat(found.isEmpty()).isTrue();
    }     
	
}
