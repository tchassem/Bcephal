/**
 * 
 */
package com.moriset.bcephal.project.domain;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.security.domain.Project;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * @author Joseph Wambo
 *
 */
public class ProjectTests {

static Validator validator;
	
	@BeforeAll 
	static void initValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();		
		Assertions.assertThat(validator).isNotNull();
	}
	
	
	@Test
	@DisplayName("Validate project JSON.")
	void validateProjectJson() throws JsonProcessingException {
		Project project = buildProject();
		Assertions.assertThat(project).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(project);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		Project data = mapper.readValue(json, Project.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getCode()).isEqualTo(project.getCode());
		Assertions.assertThat(data.getName()).isEqualTo(project.getName());
		Assertions.assertThat(data.getSubscriptionId()).isNull();
		Assertions.assertThat(data.isDefaultProject()).isEqualTo(project.isDefaultProject());
	}
	
	
	@Test
	@DisplayName("Validate project code.")
	public void validateProjectCode() {
		Project project = buildProject();
		Assertions.assertThat(project).isNotNull();
		project.setCode(null);	
		Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);	
		Assertions.assertThat(constraintViolations.size()).isEqualTo(1);	
		Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.code.validation.null.message}");
        
        project.setCode("");		
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(1);
        Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.code.validation.size.message}");
        
        project.setCode("12");		
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(1);
        Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.code.validation.size.message}");
        
        
        project.setCode("");
        for(int i = 1; i < 55; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
       
        
        project.setCode("");
        for(int i = 1; i <= 3; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
		
		project.setCode("");
		for(int i = 1; i <= 20; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
		
		project.setCode("");
		for(int i = 1; i <= 50; i++) {
        	project.setCode(project.getCode().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
		
	}
	
	@Test
	@DisplayName("Validate project name.")
	void validateProjectName() {
		Project project = buildProject();
		Assertions.assertThat(project).isNotNull();
		
		project.setName(null);		
		Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);	
		Assertions.assertThat(constraintViolations.size()).isEqualTo(1);	
		Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.name.validation.null.message}");
		
        project.setName("");		
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(1);	
		Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.name.validation.size.message}");
        
        project.setName("12");		
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(1);	
		Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.name.validation.size.message}");
                
        project.setName("");
        for(int i = 1; i < 110; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(1);	
		Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.name.validation.size.message}");
        
        project.setName("");
        for(int i = 1; i <= 3; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);	
		
		project.setName("");
		for(int i = 1; i <= 50; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);	
		
		project.setName("");
		for(int i = 1; i <= 100; i++) {
        	project.setName(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);	
		
	}
	
	@Test
	@DisplayName("Validate project description.")
	void validateProjectDescription() {
		Project project = buildProject();
		Assertions.assertThat(project).isNotNull();
		
		project.setDescription(null);		
		Set<ConstraintViolation<Project>> constraintViolations = validator.validate(project);		
		 Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
        
        project.setDescription("");		
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
        		
		project.setDescription("");
		for(int i = 1; i <= 100; i++) {
        	project.setDescription(project.getName().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
		
		project.setDescription("");
		for(int i = 1; i <= 255; i++) {
        	project.setDescription(project.getDescription().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(0);
		
		project.setDescription("");
        for(int i = 1; i < 300; i++) {
        	project.setDescription(project.getDescription().concat("A"));
        }
        constraintViolations = validator.validate(project);
        Assertions.assertThat(constraintViolations.size()).isEqualTo(1);	
		Assertions.assertThat( constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{project.description.validation.size.message}");		
	}
	
	
	Project buildProject() {
		Project project = Project.builder()
				.code("" + System.currentTimeMillis())
				.name("My first project")
				.subscriptionId(1L)
				.client("Default Client")
				.defaultProject(false)
				.build();
		return project;
	}
	
	
}
