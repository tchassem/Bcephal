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

public class UserInfoIntegrationTests {

	static Validator validator;

	@BeforeAll
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate userInfo JSON.")
	void validateClientJson() throws JsonProcessingException {
		UserInfo userInfo = new SecurityFactory().buildUserInfo();
		Assertions.assertThat(userInfo).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(userInfo);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		UserInfo data = mapper.readValue(json, UserInfo.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getType()).isEqualTo(userInfo.getType());
		Assertions.assertThat(data.getUsername()).isEqualTo(userInfo.getUsername());
		Assertions.assertThat(data.getName()).isEqualTo(userInfo.getName());
	}
	
	
}
