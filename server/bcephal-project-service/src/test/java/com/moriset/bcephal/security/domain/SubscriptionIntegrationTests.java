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
import com.moriset.bcephal.project.ProjectFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class SubscriptionIntegrationTests {

	static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		assertNotNull(validator);
	}
	
	@Test
	@DisplayName("Validate subscription JSON.")
	void validateSubscriptionJson() throws JsonProcessingException {
		Subscription subscription = buildSubscription();
		Assertions.assertThat(subscription).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(subscription);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Subscription data = mapper.readValue(json, Subscription.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getName()).isEqualTo(subscription.getName());
	}	
	
	@Test
	@DisplayName("Validate subscription name.")
	void validateSubscriptionName() throws JsonProcessingException {
		Subscription subscription = new ProjectFactory().buildSubscription();
		
		subscription.setName(null);		
		Set<ConstraintViolation<Subscription>> constraintViolations = validator.validate(subscription);		
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.null.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        subscription.setName("");		
        constraintViolations = validator.validate(subscription);
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        subscription.setName("12");		
        constraintViolations = validator.validate(subscription);
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        
        subscription.setName("");
        for(int i = 1; i < 105; i++) {
        	subscription.setName(subscription.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscription);
		assertEquals(1, constraintViolations.size());
        assertEquals("{subscription.name.validation.size.message}", constraintViolations.iterator().next().getMessageTemplate());
        
        subscription.setName("");
        for(int i = 1; i <= 3; i++) {
        	subscription.setName(subscription.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscription);
		assertEquals(0, constraintViolations.size());
		
		subscription.setName("");
		for(int i = 1; i <= 20; i++) {
			subscription.setName(subscription.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscription);
		assertEquals(0, constraintViolations.size());
		
		subscription.setName("");
		for(int i = 1; i <= 100; i++) {
			subscription.setName(subscription.getName().concat("A"));
        }
        constraintViolations = validator.validate(subscription);
		assertEquals(0, constraintViolations.size());		
		
	}
	
	Subscription buildSubscription() {
		Subscription subscription = new Subscription();
		subscription.setName("My first subscription");
		return subscription;
	}
}
