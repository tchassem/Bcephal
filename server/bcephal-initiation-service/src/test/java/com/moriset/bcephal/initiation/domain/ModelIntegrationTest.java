package com.moriset.bcephal.initiation.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ModelIntegrationTest {
	
static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("validation model name")
	void validateAttributName() throws JsonProcessingException{
		Model model = Model.builder().name("model name").build();
		
		model.setName("");
		Set<ConstraintViolation<Model>> constraintViolations = validator.validate(model);
		//Assertion.assertEquals(1, constraintViolations.size());
		Assertions.assertSame(1, constraintViolations.size());
		
		
	}

	
}
