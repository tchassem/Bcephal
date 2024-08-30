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

public class ProjectBrowserDataIntegrationTests {

	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate projectBrowserData JSON.")
	void validateProjectBrowserDataJson() throws JsonProcessingException {
		ProjectBrowserData projectBrowserData = buildProjectBrowserData();
		Assertions.assertThat(projectBrowserData).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(projectBrowserData);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		ProjectBrowserData data = mapper.readValue(json, ProjectBrowserData.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getCode()).isEqualTo(projectBrowserData.getCode());
		Assertions.assertThat(data.getName()).isEqualTo(projectBrowserData.getName());
		Assertions.assertThat(data.getSubscriptionId()).isNull();
		Assertions.assertThat(data.isDefaultProject()).isEqualTo(projectBrowserData.isDefaultProject());
	}
	
	@Test
	@DisplayName("Validate projectBrowserData code.")
	void validateProjectBrowserDataCode() throws JsonProcessingException {
		ProjectBrowserData projectBrowserData = new ProjectFactory().buildProjectBrowserData();
		
		projectBrowserData.setCode(null);		
		Set<ConstraintViolation<ProjectBrowserData>> constraintViolations = validator.validate(projectBrowserData);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        projectBrowserData.setCode("");		
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        projectBrowserData.setCode("12");		
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        
        projectBrowserData.setCode("");
        for(int i = 1; i < 55; i++) {
        	projectBrowserData.setCode(projectBrowserData.getCode().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.code.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        projectBrowserData.setCode("");
        for(int i = 1; i <= 3; i++) {
        	projectBrowserData.setCode(projectBrowserData.getCode().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(0, constraintViolations.size());
		
		projectBrowserData.setCode("");
		for(int i = 1; i <= 20; i++) {
        	projectBrowserData.setCode(projectBrowserData.getCode().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(0, constraintViolations.size());
		
		projectBrowserData.setCode("");
		for(int i = 1; i <= 50; i++) {
        	projectBrowserData.setCode(projectBrowserData.getCode().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(0, constraintViolations.size());		
	}

	@Test
	@DisplayName("Validate projectBrowserData name.")
	void validateProjectName() throws JsonProcessingException {
		ProjectBrowserData projectBrowserData = new ProjectFactory().buildProjectBrowserData();
		
		projectBrowserData.setName(null);		
		Set<ConstraintViolation<ProjectBrowserData>> constraintViolations = validator.validate(projectBrowserData);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        projectBrowserData.setName("");		
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        projectBrowserData.setName("12");		
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        
        projectBrowserData.setName("");
        for(int i = 1; i < 105; i++) {
        	projectBrowserData.setName(projectBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{project.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        projectBrowserData.setName("");
        for(int i = 1; i <= 3; i++) {
        	projectBrowserData.setName(projectBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(0, constraintViolations.size());
		
		projectBrowserData.setName("");
		for(int i = 1; i <= 20; i++) {
        	projectBrowserData.setName(projectBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(0, constraintViolations.size());
		
		projectBrowserData.setName("");
		for(int i = 1; i <= 100; i++) {
        	projectBrowserData.setName(projectBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(projectBrowserData);
		assertEquals(0, constraintViolations.size());		
		
	}
	
	ProjectBrowserData buildProjectBrowserData() {
		ProjectBrowserData projectBrowserData = new ProjectBrowserData();
		projectBrowserData.setCode("" + System.currentTimeMillis());
		projectBrowserData.setName("My first project");
		projectBrowserData.setCreationDate(new Timestamp(System.currentTimeMillis()));
		return projectBrowserData;
	}

}
