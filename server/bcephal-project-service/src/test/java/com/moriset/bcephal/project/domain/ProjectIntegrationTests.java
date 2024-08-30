/**
 * 
 */
package com.moriset.bcephal.project.domain;

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
import com.moriset.bcephal.security.domain.Project;

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
        for(int i = 1; i < 300; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
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
	
	Project buildProject() {
		Project project = new Project();
		project.setCode("" + System.currentTimeMillis());
		project.setName("My first project");
		project.setCreationDate(new Timestamp(System.currentTimeMillis()));
		//project.setDescription(project. toString());
		return project;
	}
	
}
