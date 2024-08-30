package com.moriset.bcephal.project.service;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.project.ProjectFactory;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.service.ProjectService;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectServiceIntegrationTests {

	
	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.project", 
					"com.moriset.bcephal.security",
					"com.moriset.bcephal.multitenant.jpa", 
					"com.moriset.bcephal.config" 
					}, 
			exclude = {
					DataSourceAutoConfiguration.class, 
					HibernateJpaAutoConfiguration.class
					}
			)
	@EntityScan(basePackages = { "com.moriset.bcephal.project.domain", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = {"int", "serv"})
	static class ApplicationTests {}
	
	
	@Autowired
    private ProjectService projectService;
	
	private static Project project;
    
    @BeforeAll
    static void init(){
    	project = new ProjectFactory().buildProject();
    	Assertions.assertThat(project).isNotNull();
    	Assertions.assertThat(project.getId()).isNull();
    }
    
    
    
    
    @Test
    @DisplayName("Validate project bean.")
    @Order(1)
	public void validateTest() {
	    Project project = new ProjectFactory().buildProject();
	    project.setCode(null);
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	projectService.save(project);
	    });
	    
	    project.setCode("");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	projectService.save(project);
	    });
	    	    
	    project.setCode("12");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	projectService.save(project);
	    });
	    
	    project.setCode("");
        for(int i = 1; i < 55; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
//        org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//        	projectService.save(project);
//	    });
	    
	}
    
    @Test
    @DisplayName("Save a new project")
    @Order(2)
    @Commit
	public void saveProjectTest() {
    	Assertions.assertThat(project).isNotNull();
    	projectService.save(project);
	 
	    Assertions.assertThat(project.getId()).isNotNull();
	    Optional<Project> found = projectService.findById(project.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getCode()).isEqualTo(project.getCode());
	}
    
    @Test
    @DisplayName("Update an existing project")
    @Order(3)
    @Commit
	public void updateProjectTest() {
    	Assertions.assertThat(project).isNotNull();
    	Assertions.assertThat(project.getId()).isNotNull();
    	project.setDescription("New description...");
    	projectService.save(project);
	    Assertions.assertThat(project.getId()).isNotNull();
	    Optional<Project> found = projectService.findById(project.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getDescription()).isEqualTo(project.getDescription());
	}
        
    @Test
    @DisplayName("Find project by code")
    @Order(4)
	public void findProjectByCode() {
    	Assertions.assertThat(project).isNotNull();	 
    	Optional<ProjectBrowserData> found = projectService.findByCode(project.getCode());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getCode()).isEqualTo(project.getCode());
	}
    
    @Test
    @DisplayName("Find project by name")
    @Order(5)
	public void findProjectByName() {
    	Assertions.assertThat(project).isNotNull();	 
	    List<Project> found = projectService.findByName(project.getName());	 
	    Assertions.assertThat(found.isEmpty()).isFalse();
	    Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).getCode()).isEqualTo(project.getCode());
	}
    
    
    @Test
    @DisplayName("Delete project by code.")
    @Order(10)
    @Commit
	public void deleteProjectByCode() {
    	Assertions.assertThat(project).isNotNull();	 
	    projectService.deleteById(project.getId());	
	    
	    Optional<Project> found = projectService.findById(project.getId());	 	 
	    Assertions.assertThat(found.isEmpty()).isTrue();
	}
	
}
