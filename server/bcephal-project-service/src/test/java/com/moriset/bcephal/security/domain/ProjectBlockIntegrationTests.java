/**
 * 
 */
package com.moriset.bcephal.security.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ProjectBlockIntegrationTests {

	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate projectBlock JSON.")
	void validateProjectBlockJson() throws JsonProcessingException {
		ProjectBlock projectBlock = buildProjectBlock();
		Assertions.assertThat(projectBlock).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(projectBlock);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		ProjectBlock data = mapper.readValue(json, ProjectBlock.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getName()).isEqualTo(projectBlock.getName());
	}	
	
	@Test
	@DisplayName("Validate projectBlock projectId.")
	void validateProjectBlockProjectId() throws JsonProcessingException {
		ProjectBlock projectBlock =  buildProjectBlock();	
		
		projectBlock.setProjectId(null);		
		Set<ConstraintViolation<ProjectBlock>> constraintViolations = validator.validate(projectBlock);	
		assertEquals(2, constraintViolations.size());
	}
	
	@Test
	@DisplayName("Validate projectBlock subcriptionId.")
	void validateProjectBlockSubcriptionId() throws JsonProcessingException {
		ProjectBlock projectBlock =  buildProjectBlock();	
		
		projectBlock.setSubcriptionId(null);		
		Set<ConstraintViolation<ProjectBlock>> constraintViolations = validator.validate(projectBlock);	
		assertEquals(2, constraintViolations.size());
	}
	
	@Test
	@DisplayName("Validate projectBlock code.")
	void validateProjectBlockCode() throws JsonProcessingException {
		ProjectBlock projectBlock =  buildProjectBlock();	
		
		projectBlock.setCode(null);		
		Set<ConstraintViolation<ProjectBlock>> constraintViolations = validator.validate(projectBlock);	
		assertEquals(3, constraintViolations.size());
	}
	
	@Test
	@DisplayName("Validate projectBlock username.")
	void validateProjectBlockUsername() throws JsonProcessingException {
		ProjectBlock projectBlock =  buildProjectBlock();	
		
		projectBlock.setUsername(null);		
		Set<ConstraintViolation<ProjectBlock>> constraintViolations = validator.validate(projectBlock);	
		assertEquals(3, constraintViolations.size());
		
		projectBlock.setUsername("");		
        constraintViolations = validator.validate(projectBlock);
        assertEquals(2, constraintViolations.size());
	}
	
	ProjectBlock buildProjectBlock() {
		ProjectBlock projectBlock = new ProjectBlock();
		projectBlock.setProjectId(projectBlock.getProjectId());
		projectBlock.setSubcriptionId(projectBlock.getSubcriptionId());
		projectBlock.setCode("" + System.currentTimeMillis());
		projectBlock.setUsername("MyUsername");
		return projectBlock;
	}
	

}
