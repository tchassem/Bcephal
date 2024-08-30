package com.moriset.bcephal.security.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.security.SecurityFactory;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ProfileUserIntegrationTests {

	static Validator validator;

	@BeforeAll
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate  profileUser JSON.")
	void validateClientJson() throws JsonProcessingException {
		ProfileUser  profileUser = new SecurityFactory().buildProfileUser();
		Assertions.assertThat(profileUser).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(profileUser);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		ProfileUser data = mapper.readValue(json, ProfileUser.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getClientId()).isEqualTo(profileUser.getClientId());
		Assertions.assertThat(data.getUserId()).isEqualTo(profileUser.getUserId());
		Assertions.assertThat(data.getProfileId()).isEqualTo(profileUser.getProfileId());
	}
}
