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

public class UserIntegrationTests {

	static Validator validator;

	@BeforeAll
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate user JSON.")
	void validateUserJson() throws JsonProcessingException {
		User user = new SecurityFactory().buildUser();
		Assertions.assertThat(user).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		User data = mapper.readValue(json, User.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getName()).isEqualTo(user.getName());
		Assertions.assertThat(data.getUserId()).isEqualTo(user.getUserId());
	}
	
	@Test
	@DisplayName("Validate user name.")
	void validateClientName() throws JsonProcessingException {
		User user = new SecurityFactory().buildUser();

		user.setName(null);
		Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{client.name.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
     
        user.setName("");		
        constraintViolations = validator.validate(user);
		assertEquals(0, constraintViolations.size());
        
		user.setName("AB");		
        constraintViolations = validator.validate(user);
		assertEquals(0, constraintViolations.size());
        
		user.setName("");
        for(int i = 1; i < 105; i++) {
        	user.setName(user.getName().concat("A"));
        }
        constraintViolations = validator.validate(user);
		assertEquals(1, constraintViolations.size());
        assertEquals("{client.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        user.setName("");
        for(int i = 1; i <= 15; i++) {
        	user.setName(user.getName().concat("A"));
        }
        constraintViolations = validator.validate(user);
		assertEquals(0, constraintViolations.size());
		
		user.setName("");
		for(int i = 1; i <= 45; i++) {
			user.setName(user.getName().concat("A"));
        }
        constraintViolations = validator.validate(user);
		assertEquals(0, constraintViolations.size());
		
		user.setName("");
		for(int i = 1; i <= 75; i++) {
			user.setName(user.getName().concat("A"));
        }
        constraintViolations = validator.validate(user);
		assertEquals(0, constraintViolations.size());
	}
}
