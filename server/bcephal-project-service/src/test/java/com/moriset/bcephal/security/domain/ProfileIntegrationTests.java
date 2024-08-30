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

public class ProfileIntegrationTests {

	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate profile JSON.")
	void validateProfileJson() throws JsonProcessingException {
		Profile profile = buildProfile();
		Assertions.assertThat(profile).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(profile);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Profile data = mapper.readValue(json, Profile.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getType()).isEqualTo(profile.getType());
	}	

	@Test
	@DisplayName("Validate profile type.")
	void validateProfileType() throws JsonProcessingException {
		Profile profile = buildProfile();
		
		profile.setType(null);		
		Set<ConstraintViolation<Profile>> constraintViolations = validator.validate(profile);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{profil.type.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
	}
	
	Profile buildProfile() {
		Profile profile = new Profile();
		profile.setType(ProfileType.ADMINISTRATOR);
		return profile;
	}
	
}
