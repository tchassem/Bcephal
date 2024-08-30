/**
 * 
 */
package com.moriset.bcephal.security.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Timestamp;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.project.ProjectFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ProjectIntegrationTests {

	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate project JSON.")
	void validateProjectJson() throws JsonProcessingException {
		Project project = buildProject();
		Assertions.assertThat(project).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(project);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Project data = mapper.readValue(json, Project.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getCode()).isEqualTo(project.getCode());
		Assertions.assertThat(data.getName()).isEqualTo(project.getName());
		Assertions.assertThat(data.getSubscriptionId()).isNull();
		Assertions.assertThat(data.isDefaultProject()).isEqualTo(project.isDefaultProject());
	}
	
	@Test
	@DisplayName("Validate project code.")
	void validateProjectCode() throws JsonProcessingException {
		Project project = new ProjectFactory().buildProject();
		
		project.setCode(null);		
		Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        project.setCode("");		
        constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        project.setCode("12");		
        constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        
        project.setCode("");
        for(int i = 1; i < 55; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(0, constraintViolations.size());
       // assertEquals("{project.code.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        project.setCode("");
        for(int i = 1; i <= 3; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(0, constraintViolations.size());
		
		project.setCode("");
		for(int i = 1; i <= 20; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(0, constraintViolations.size());
		
		project.setCode("");
		for(int i = 1; i <= 50; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(0, constraintViolations.size());		
	}

	@Test
	@DisplayName("Validate project name.")
	void validateProjectName() throws JsonProcessingException {
		Project project = new ProjectFactory().buildProject();
		
		project.setName(null);		
		Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        project.setName("");		
        constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        project.setName("12");		
        constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        
        project.setName("");
        for(int i = 1; i < 105; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        project.setName("");
        for(int i = 1; i <= 3; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(0, constraintViolations.size());
		
		project.setName("");
		for(int i = 1; i <= 20; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(0, constraintViolations.size());
		
		project.setName("");
		for(int i = 1; i <= 100; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(0, constraintViolations.size());		
		
	}
	
	@Test
	@DisplayName("Validate project description.")
	void validateProjectDescription() throws JsonProcessingException {
		Project project = new ProjectFactory().buildProject();
        
        project.setDescription("");
        for(int i = 1; i < 260; i++) {
        	project.setDescription(project.getDescription().concat("A"));
        }
        Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.description.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
		
	}
	
	@Test
	@DisplayName("Validate project path.")
	void validateProjectPath() throws JsonProcessingException {
		Project project = new ProjectFactory().buildProject();
        
        project.setPath("");
        for(int i = 1; i < 260; i++) {
        	project.setPath(project.getPath().concat("A"));
        }
        Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.path.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());		
	}
	
	@Test
	@DisplayName("Validate project dbname.")
	void validateProjectDbname() throws JsonProcessingException {
		Project project = new ProjectFactory().buildProject();
        
        project.setDbname("");
        for(int i = 1; i < 105; i++) {
        	project.setDbname(project.getDbname().concat("A"));
        }
        Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.dbname.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
		
	}
	
	@Test
	@DisplayName("Validate project username.")
	void validateProjectUsername() throws JsonProcessingException {
		Project project = new ProjectFactory().buildProject();
        
        project.setUsername("");
        for(int i = 1; i < 55; i++) {
        	project.setUsername(project.getUsername().concat("A"));
        }
        Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.user.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
		
	}
	
	Project buildProject() {
		Project project = new Project();
		project.setCode("" + System.currentTimeMillis());
		project.setName("My first project");
		project.setCreationDate(new Timestamp(System.currentTimeMillis()));
		//project.setDescription(project. toString());
		return project;
	}
	
}
