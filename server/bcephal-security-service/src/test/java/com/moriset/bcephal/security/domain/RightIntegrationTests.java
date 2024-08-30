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
import com.moriset.bcephal.security.SecurityFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class RightIntegrationTests {

	static Validator validator;

	@BeforeAll
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate right JSON.")
	void validateClientJson() throws JsonProcessingException {
		Right right = new SecurityFactory().buildRight();
		Assertions.assertThat(right).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(right);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Right data = mapper.readValue(json, Right.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getLevel()).isEqualTo(right.getLevel());
		Assertions.assertThat(data.getFunctionality()).isEqualTo(right.getFunctionality());

	}
	
	@Test
	@DisplayName("Validate right rightlevel.")
	void validateRightLevel() throws JsonProcessingException {
		Right right = new SecurityFactory().buildRight();
		
		right.setLevel(null);
		Set<ConstraintViolation<Right>> constraintViolations = validator.validate(right);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{right.level.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
	}
	
	
	@Test
	@DisplayName("Validate right functionality.")
	void validateRightFunctionality() throws JsonProcessingException {
		Right right = new SecurityFactory().buildRight();
		
		right.setFunctionality(null);
		Set<ConstraintViolation<Right>> constraintViolations = validator.validate(right);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{right.functionality.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
	   
        right.setFunctionality("agt");
	    constraintViolations = validator.validate(right);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{right.functionality.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        right.setFunctionality("");
        for(int i=1 ; i <=5; i++) {
        	right.setFunctionality(right.getFunctionality().concat("A"));
        }
        constraintViolations = validator.validate(right);	
		assertEquals(0, constraintViolations.size());
       

        right.setFunctionality("");
        for(int i=1 ; i <=200; i++) {
        	right.setFunctionality(right.getFunctionality().concat("A"));
        }
        constraintViolations = validator.validate(right);	
		assertEquals(0, constraintViolations.size());
       
	   
        right.setFunctionality("");
        for(int i=0 ; i < 260; i++) {
        	right.setFunctionality(right.getFunctionality().concat("A"));
        }
        constraintViolations = validator.validate(right);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{right.functionality.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
	   

        right.setFunctionality("");
        for(int i=1 ; i < 100; i++) {
        	right.setFunctionality(right.getFunctionality().concat("A"));
        }
        constraintViolations = validator.validate(right);	
		assertEquals(0, constraintViolations.size());
	}
	
}
