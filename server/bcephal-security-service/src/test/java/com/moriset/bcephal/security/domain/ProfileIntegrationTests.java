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

public class ProfileIntegrationTests {


	static Validator validator;

	@BeforeAll
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate profile JSON.")
	void validateClientJson() throws JsonProcessingException {
		Profile profile = new SecurityFactory().buildProfile();
		Assertions.assertThat(profile).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(profile);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Profile data = mapper.readValue(json, Profile.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getType()).isEqualTo(profile.getType());
		Assertions.assertThat(data.getName()).isEqualTo(profile.getName());
	}
	
	@Test
	@DisplayName("Validate profile type.")
	void validateRightLevel() throws JsonProcessingException {
		
		Profile profile = new SecurityFactory().buildProfile();
		profile.setType(null);
		Set<ConstraintViolation<Profile>> constraintViolations = validator.validate(profile);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{profil.type.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
	}
	
	@Test
	@DisplayName("Validate profile name.")
	void validateClientName() throws JsonProcessingException {
		Profile profile = new SecurityFactory().buildProfile();;

		profile.setName(null);
		Set<ConstraintViolation<Profile>> constraintViolations = validator.validate(profile);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{client.name.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
     
        profile.setName("");		
        constraintViolations = validator.validate(profile);
		assertEquals(0, constraintViolations.size());
        
		profile.setName("AB");		
        constraintViolations = validator.validate(profile);
		assertEquals(0, constraintViolations.size());
        
		profile.setName("");
        for(int i = 1; i < 105; i++) {
        	profile.setName(profile.getName().concat("A"));
        }
        constraintViolations = validator.validate(profile);
		assertEquals(1, constraintViolations.size());
        assertEquals("{client.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        profile.setName("");
        for(int i = 1; i <= 15; i++) {
        	profile.setName(profile.getName().concat("A"));
        }
        constraintViolations = validator.validate(profile);
		assertEquals(0, constraintViolations.size());
		
		profile.setName("");
		for(int i = 1; i <= 45; i++) {
			profile.setName(profile.getName().concat("A"));
        }
        constraintViolations = validator.validate(profile);
		assertEquals(0, constraintViolations.size());
		
		profile.setName("");
		for(int i = 1; i <= 75; i++) {
			profile.setName(profile.getName().concat("A"));
        }
        constraintViolations = validator.validate(profile);
		assertEquals(0, constraintViolations.size());
	}
	
	
}
