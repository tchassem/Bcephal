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
import com.moriset.bcephal.security.SecurityFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * 
 */
public class ClientIntegrationTests {

	static Validator validator;

	@BeforeAll
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate client JSON.")
	void validateClientJson() throws JsonProcessingException {
		Client client = buildClient();
		Assertions.assertThat(client).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(client);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Client data = mapper.readValue(json, Client.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getClientId()).isEqualTo(client.getClientId());
		Assertions.assertThat(data.getCode()).isEqualTo(client.getCode());
		Assertions.assertThat(data.getAddress()).isEqualTo(client.getAddress());
		Assertions.assertThat(data.getNature()).isEqualTo(client.getNature());
		Assertions.assertThat(data.getStatus()).isEqualTo(client.getStatus());
	
		
	}
	
	@Test
	@DisplayName("Validate client name.")
	void validateClientName() throws JsonProcessingException {
		Client client = new SecurityFactory().buildClient();

		client.setName(null);
		Set<ConstraintViolation<Client>> constraintViolations = validator.validate(client);	
		assertEquals(1, constraintViolations.size());
        assertEquals("{client.name.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
     
        client.setName("");		
        constraintViolations = validator.validate(client);
		assertEquals(0, constraintViolations.size());
        
        client.setName("AB");		
        constraintViolations = validator.validate(client);
		assertEquals(0, constraintViolations.size());
        
        client.setName("");
        for(int i = 1; i < 105; i++) {
        	client.setName(client.getName().concat("A"));
        }
        constraintViolations = validator.validate(client);
		assertEquals(1, constraintViolations.size());
        assertEquals("{client.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        client.setName("");
        for(int i = 1; i <= 15; i++) {
        	client.setName(client.getName().concat("A"));
        }
        constraintViolations = validator.validate(client);
		assertEquals(0, constraintViolations.size());
		
		client.setName("");
		for(int i = 1; i <= 45; i++) {
			client.setName(client.getName().concat("A"));
        }
        constraintViolations = validator.validate(client);
		assertEquals(0, constraintViolations.size());
		
		client.setName("");
		for(int i = 1; i <= 75; i++) {
			client.setName(client.getName().concat("A"));
        }
        constraintViolations = validator.validate(client);
		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	@DisplayName("Validate client nature.")
	void validateClientNature() throws JsonProcessingException {
		Client client = new SecurityFactory().buildClient();

		client.setNature(null);
		Set<ConstraintViolation<Client>> constraintViolations = validator.validate(client);		
		assertEquals(1, constraintViolations.size());
		assertEquals("{client.nature.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
		
	}
	
	@Test
	@DisplayName("Validate client status.")
	void validateClientStatus() throws JsonProcessingException {
		Client client = new SecurityFactory().buildClient();

		client.setStatus(null);
		Set<ConstraintViolation<Client>> constraintViolations = validator.validate(client);		
		assertEquals(1, constraintViolations.size());
		assertEquals("{client.status.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
		
	}
	
	@Test
	@DisplayName("Validate client type.")
	void validateClientType() throws JsonProcessingException {
		Client client = new SecurityFactory().buildClient();

		client.setType(null);
		Set<ConstraintViolation<Client>> constraintViolations = validator.validate(client);		
		assertEquals(1, constraintViolations.size());
		assertEquals("{client.type.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
		
	}
	
	@Test
	@DisplayName("Validate client email address.")
	void validateClientEmailAddress() throws JsonProcessingException {
		Client client = new SecurityFactory().buildClient();
		
		Address address = client.getAddress();
		address.setEmail(null);
		client.setAddress(address);
		Set<ConstraintViolation<Client>> constraintViolations = validator.validate(client);		
		assertEquals(0, constraintViolations.size());
		
		address.setEmail("abc");
		client.setAddress(address);
		constraintViolations = validator.validate(client);		
		assertEquals(1, constraintViolations.size());
		assertEquals("{email.validation.message}", constraintViolations.iterator().next().getMessageTemplate());
		
		address.setEmail("nd+@ln.org");
		client.setAddress(address);
		constraintViolations = validator.validate(client);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{email.validation.message}", constraintViolations.iterator().next().getMessageTemplate());
        
		
	}
	
	public Client buildClient() {
		
		Client client = Client.builder().build();
		client.setName("Bcephal");
		client.setClientId("" + System.currentTimeMillis());
		client.setCode("Client code");
		client.setAddress(builAddress());
		client.setNature(ClientNature.COMPANY);
		client.setStatus(ClientStatus.ACTIVE);
		client.setType(ClientType.PRIVILEGE);
		return client;
	}
	
	public Address builAddress() {
		
		Address address = new Address();
		address.setEmail("abc@gmail.com");
		address.setCity("y");
		address.setCountry("cam");
		return address;
	}
}
