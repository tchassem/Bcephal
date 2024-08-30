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

public class FunctionalityBlockIntegrationTests {
	
	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate functionalityBlock JSON.")
	void validateFunctionalityBlockJson() throws JsonProcessingException {
		FunctionalityBlock functionalityBlock = buildFunctionalityBlock();
		Assertions.assertThat(functionalityBlock).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(functionalityBlock);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		FunctionalityBlock data = mapper.readValue(json, FunctionalityBlock.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getName()).isEqualTo(functionalityBlock.getName());
	}	
	
	@Test
	@DisplayName("Validate functionalityBlock projectId.")
	void validateFunctionalityBlockProjectId() throws JsonProcessingException {
		FunctionalityBlock functionalityBlock =  buildFunctionalityBlock();	
		
		functionalityBlock.setProjectId(null);		
		Set<ConstraintViolation<FunctionalityBlock>> constraintViolations = validator.validate(functionalityBlock);	
		assertEquals(1, constraintViolations.size());
	}

	@Test
	@DisplayName("Validate functionalityBlock code.")
	void validateFunctionalityBlockCode() throws JsonProcessingException {
		FunctionalityBlock functionalityBlock =  buildFunctionalityBlock();	
		
		functionalityBlock.setCode(null);		
		Set<ConstraintViolation<FunctionalityBlock>> constraintViolations = validator.validate(functionalityBlock);	
		assertEquals(2, constraintViolations.size());
		
		functionalityBlock.setCode("");		
        constraintViolations = validator.validate(functionalityBlock);
        assertEquals(1, constraintViolations.size());
	}
	
	@Test
	@DisplayName("Validate functionalityBlock username.")
	void validateFunctionalityBlockUsername() throws JsonProcessingException {
		FunctionalityBlock functionalityBlock =  buildFunctionalityBlock();	
		
		functionalityBlock.setUsername(null);		
		Set<ConstraintViolation<FunctionalityBlock>> constraintViolations = validator.validate(functionalityBlock);	
		assertEquals(2, constraintViolations.size());
		
		functionalityBlock.setUsername("");		
        constraintViolations = validator.validate(functionalityBlock);
        assertEquals(1, constraintViolations.size());
	}
	
	FunctionalityBlock buildFunctionalityBlock() {
		FunctionalityBlock functionalityBlock = new FunctionalityBlock();	
		functionalityBlock.setProjectId(functionalityBlock.getProjectId());	
		functionalityBlock.setCode("" + System.currentTimeMillis());
		functionalityBlock.setUsername("MyUsername");
		return functionalityBlock;
	}
}