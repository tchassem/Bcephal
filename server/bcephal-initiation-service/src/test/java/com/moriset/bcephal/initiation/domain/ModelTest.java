package com.moriset.bcephal.initiation.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ModelTest {

	
	Model buildModel() {
		Model model = new Model();
		model.setPosition(1);
		model.setName("My first Model");
		model.setCreationDate(new Timestamp(new Date().getTime()));
		return model;
	}
	
	
	@Test
	@DisplayName("Validate Model JSON.")
	void validateModelJson() throws JsonProcessingException {
		Model model = buildModel();
		
		ObjectMapper mapper = new ObjectMapper();
		
		assertTrue(mapper.canSerialize(Model.class));
		
		String json = mapper.writeValueAsString(model);
		assertNotNull(json);
		assertTrue(json.length() > 0);
		
		Model newProject = mapper.readValue(json, Model.class);
		assertNotNull(newProject);
		assertEquals(model.getId(), newProject.getId());
		assertEquals(model.getPosition(), newProject.getPosition());
		assertEquals(model.getName(), newProject.getName());
		
	}
}
