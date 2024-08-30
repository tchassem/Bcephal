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

import com.moriset.bcephal.grid.domain.form.FormModelFieldValidationItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelFieldValidationItemRepositoryIntegrationTests {

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
	private FormModelFieldValidationItemRepository repository;
	
	private static FormModelFieldValidationItem formModelFieldValidationItem;
	
	@BeforeAll
	static void init() {
		formModelFieldValidationItem = CreateFactory.buildFormModelFieldValidationItem();
		assertThat(formModelFieldValidationItem).isNotNull();
		assertThat(formModelFieldValidationItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelFieldValidationItem")
	@Order(1)
	@Commit
	public void saveFormModelFieldValidationItemTest() {
		assertThat(formModelFieldValidationItem).isNotNull();
		repository.save(formModelFieldValidationItem);
		
		assertThat(formModelFieldValidationItem.getId()).isNotNull();
		Optional<FormModelFieldValidationItem> found = repository.findById(formModelFieldValidationItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelFieldValidationItem.getId());
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldValidationItem.getStringValue());
	}
	
	@Test
	@DisplayName("Update an existing formModelFieldValidationItem")
	@Order(2)
	@Commit
	public void updateFormModelFieldValidationItemTest() {
		assertThat(formModelFieldValidationItem).isNotNull();
		assertThat(formModelFieldValidationItem.getId()).isNotNull();
		formModelFieldValidationItem.setStringValue("New string value name ..");
		repository.save(formModelFieldValidationItem);
		
		assertThat(formModelFieldValidationItem.getId()).isNotNull();
		Optional<FormModelFieldValidationItem> found = repository.findById(formModelFieldValidationItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldValidationItem.getStringValue());
	}
	
	@Test
	@DisplayName("Find formModelFieldValidationItem by id")
	@Order(3)
	public void findFormModelFieldValidationItemById() {
		assertThat(formModelFieldValidationItem).isNotNull();
		assertThat(formModelFieldValidationItem.getId()).isNotNull();
		
		Optional<FormModelFieldValidationItem> found = repository.findById(formModelFieldValidationItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldValidationItem.getStringValue());
	}
	
	@Test
    @DisplayName("Delete formModelFieldValidationItem by id.")
    @Order(4)
    @Commit
	public void deleteFormModelFieldValidationItemById() {
    	Assertions.assertThat(formModelFieldValidationItem).isNotNull();	 
    	repository.deleteById(formModelFieldValidationItem.getId());
	    Optional<FormModelFieldValidationItem> notfound = repository.findById(formModelFieldValidationItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
