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

public class SubscriptionBrowserDataIntegrationTests {

	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate subscriptionBrowserData JSON.")
	void validateSubscriptionBrowserDataJson() throws JsonProcessingException {
		SubscriptionBrowserData subscriptionBrowserData = buildSubscriptionBrowserData();
		Assertions.assertThat(subscriptionBrowserData).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(subscriptionBrowserData);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Subscription data = mapper.readValue(json, Subscription.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getName()).isEqualTo(subscriptionBrowserData.getName());
	}	
	
	@Test
	@DisplayName("Validate subscriptionBrowserData name.")
	void validateSubscriptionBrowserDataName() throws JsonProcessingException {
		SubscriptionBrowserData subscriptionBrowserData = buildSubscriptionBrowserData();
		
		subscriptionBrowserData.setName(null);		
		Set<ConstraintViolation<SubscriptionBrowserData>> constraintViolations = validator.validate(subscriptionBrowserData);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        subscriptionBrowserData.setName("");		
        constraintViolations = validator.validate(subscriptionBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        subscriptionBrowserData.setName("12");		
        constraintViolations = validator.validate(subscriptionBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        
        subscriptionBrowserData.setName("");
        for(int i = 1; i < 105; i++) {
        	subscriptionBrowserData.setName(subscriptionBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscriptionBrowserData);
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        subscriptionBrowserData.setName("");
        for(int i = 1; i <= 3; i++) {
        	subscriptionBrowserData.setName(subscriptionBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscriptionBrowserData);
		assertEquals(0, constraintViolations.size());
		
		subscriptionBrowserData.setName("");
		for(int i = 1; i <= 20; i++) {
			subscriptionBrowserData.setName(subscriptionBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscriptionBrowserData);
		assertEquals(0, constraintViolations.size());
		
		subscriptionBrowserData.setName("");
		for(int i = 1; i <= 100; i++) {
			subscriptionBrowserData.setName(subscriptionBrowserData.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscriptionBrowserData);
		assertEquals(0, constraintViolations.size());		
		
	}
	
	SubscriptionBrowserData buildSubscriptionBrowserData() {
		SubscriptionBrowserData subscriptionBrowserData = new SubscriptionBrowserData();
		subscriptionBrowserData.setName("My first subscriptionBrowserData");
		return subscriptionBrowserData;
	}

}
