package com.moriset.bcephal.grid.repository.form;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.grid.domain.form.FormModelFieldReferenceCondition;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelFieldReferenceConditionRepositoryIntegrationTests {

	@SpringBootApplication(
			scanBasePackages = {
					"com.moriset.bcephal.sourcing.grid"
			})
	@EntityScan(
			basePackages = { 
					"com.moriset.bcephal.domain",
					"com.moriset.bcephal.grid.domain"
					})
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {};
	
	@Autowired
	private FormModelFieldReferenceConditionRepository repository;
	
	private static FormModelFieldReferenceCondition formModelFieldReferenceCondition;
	
	@BeforeAll
	static void init() {
		formModelFieldReferenceCondition = CreateFactory.buildFormModelFieldReferenceCondition();
		assertThat(formModelFieldReferenceCondition).isNotNull();
		assertThat(formModelFieldReferenceCondition.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelFieldReferenceCondition")
	@Order(1)
	@Commit
	public void saveFormModelFieldReferenceConditionTest() {
		assertThat(formModelFieldReferenceCondition).isNotNull();
		repository.save(formModelFieldReferenceCondition);
		
		assertThat(formModelFieldReferenceCondition.getId()).isNotNull();
		Optional<FormModelFieldReferenceCondition> found = repository.findById(formModelFieldReferenceCondition.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelFieldReferenceCondition.getId());
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldReferenceCondition.getStringValue());
	}
	
	@Test
	@DisplayName("Update an existing formModelFieldReferenceCondition")
	@Order(2)
	@Commit
	public void updateFormModelFieldReferenceConditionTest() {
		assertThat(formModelFieldReferenceCondition).isNotNull();
		assertThat(formModelFieldReferenceCondition.getId()).isNotNull();
		formModelFieldReferenceCondition.setStringValue("String value ...");
		repository.save(formModelFieldReferenceCondition);
		
		assertThat(formModelFieldReferenceCondition.getId()).isNotNull();
		Optional<FormModelFieldReferenceCondition> found = repository.findById(formModelFieldReferenceCondition.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldReferenceCondition.getStringValue());
	}
	
	@Test
	@DisplayName("Find formModelFieldReference by id")
	@Order(3)
	public void findFormModelFieldReferenceConditionById() {
		assertThat(formModelFieldReferenceCondition).isNotNull();
		assertThat(formModelFieldReferenceCondition.getId()).isNotNull();
		
		Optional<FormModelFieldReferenceCondition> found = repository.findById(formModelFieldReferenceCondition.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldReferenceCondition.getStringValue());
	}
	
	@Test
    @DisplayName("Delete formModelFieldReference by id.")
    @Order(4)
    @Commit
	public void deleteFormModelFieldReferenceConditionById() {
    	Assertions.assertThat(formModelFieldReferenceCondition).isNotNull();	 
    	repository.deleteById(formModelFieldReferenceCondition.getId());
	    Optional<FormModelFieldReferenceCondition> notfound = repository.findById(formModelFieldReferenceCondition.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
