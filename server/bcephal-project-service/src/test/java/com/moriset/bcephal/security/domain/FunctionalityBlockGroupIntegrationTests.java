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

public class FunctionalityBlockGroupIntegrationTests {
	
	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}	
	
	@Test
	@DisplayName("Validate functionalityBlockGroup JSON.")
	void validateFunctionalityBlockGroupJson() throws JsonProcessingException {
		FunctionalityBlockGroup functionalityBlockGroup = buildFunctionalityBlockGroup();
		Assertions.assertThat(functionalityBlockGroup).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(functionalityBlockGroup);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		FunctionalityBlockGroup data = mapper.readValue(json, FunctionalityBlockGroup.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getName()).isEqualTo(functionalityBlockGroup.getName());
	}	
	
	@Test
	@DisplayName("Validate functionalityBlockGroup projectId.")
	void validateFunctionalityBlockGroupProjectId() throws JsonProcessingException {
		FunctionalityBlockGroup functionalityBlockGroup =  buildFunctionalityBlockGroup();	
		
		functionalityBlockGroup.setProjectId(null);		
		Set<ConstraintViolation<FunctionalityBlockGroup>> constraintViolations = validator.validate(functionalityBlockGroup);	
		assertEquals(1, constraintViolations.size());
	}
	
	@Test
	@DisplayName("Validate functionalityBlockGroup username.")
	void validateFunctionalityBlockGroupUsername() throws JsonProcessingException {
		FunctionalityBlockGroup functionalityBlockGroup =  buildFunctionalityBlockGroup();	
		
		functionalityBlockGroup.setUsername(null);		
		Set<ConstraintViolation<FunctionalityBlockGroup>> constraintViolations = validator.validate(functionalityBlockGroup);	
		assertEquals(2, constraintViolations.size());
		
		functionalityBlockGroup.setUsername("");		
        constraintViolations = validator.validate(functionalityBlockGroup);
        assertEquals(1, constraintViolations.size());
	}
	
	FunctionalityBlockGroup buildFunctionalityBlockGroup() {
		FunctionalityBlockGroup functionalityBlockGroup = new FunctionalityBlockGroup();	
		functionalityBlockGroup.setProjectId(functionalityBlockGroup.getProjectId());	
		functionalityBlockGroup.setUsername("MyUsername");
		return functionalityBlockGroup;
	}
}