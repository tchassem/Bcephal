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

public class UserSessionLogIntegrationTests {

	static Validator validator;

	@BeforeAll
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate userSessionLog JSON.")
	void validateUserSessionLogJson() throws JsonProcessingException {
		UserSessionLog userSessionLog = new SecurityFactory().buildUserSessionLog();
		Assertions.assertThat(userSessionLog).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(userSessionLog);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		UserSessionLog data = mapper.readValue(json, UserSessionLog.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getName()).isEqualTo(userSessionLog.getName());
	}
	

}
