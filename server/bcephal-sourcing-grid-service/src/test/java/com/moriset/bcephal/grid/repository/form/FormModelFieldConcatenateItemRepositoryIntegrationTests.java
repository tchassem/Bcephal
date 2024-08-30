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

import com.moriset.bcephal.grid.domain.form.FormModelFieldConcatenateItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelFieldConcatenateItemRepositoryIntegrationTests {

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
	private FormModelFieldConcatenateItemRepository repository;
	
	private static FormModelFieldConcatenateItem formModelFieldConcatenateItem;
	
	@BeforeAll
	static void init() {
		formModelFieldConcatenateItem = CreateFactory.buildFormModelFieldConcatenateItem();
		assertThat(formModelFieldConcatenateItem).isNotNull();
		assertThat(formModelFieldConcatenateItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelFieldConcatenateItem")
	@Order(1)
	@Commit
	public void saveFormModelFieldConcatenateItemTest() {
		assertThat(formModelFieldConcatenateItem).isNotNull();
		repository.save(formModelFieldConcatenateItem);
		
		assertThat(formModelFieldConcatenateItem.getId()).isNotNull();
		Optional<FormModelFieldConcatenateItem> found = repository.findById(formModelFieldConcatenateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelFieldConcatenateItem.getId());
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldConcatenateItem.getStringValue());
	}
	
	@Test
	@DisplayName("Update an existing formModelFieldConcatenateItem")
	@Order(2)
	@Commit
	public void updateFormModelFieldConcatenateItemTest() {
		assertThat(formModelFieldConcatenateItem).isNotNull();
		assertThat(formModelFieldConcatenateItem.getId()).isNotNull();
		formModelFieldConcatenateItem.setStringValue("String value ...");
		repository.save(formModelFieldConcatenateItem);
		
		assertThat(formModelFieldConcatenateItem.getId()).isNotNull();
		Optional<FormModelFieldConcatenateItem> found = repository.findById(formModelFieldConcatenateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldConcatenateItem.getStringValue());
	}
	
	@Test
	@DisplayName("Find formModelFieldConcatenateItem by id")
	@Order(3)
	public void findFormModelFieldConcatenateItemById() {
		assertThat(formModelFieldConcatenateItem).isNotNull();
		assertThat(formModelFieldConcatenateItem.getId()).isNotNull();
		
		Optional<FormModelFieldConcatenateItem> found = repository.findById(formModelFieldConcatenateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelFieldConcatenateItem.getStringValue());
	}
	
	@Test
    @DisplayName("Delete formModelFieldConcatenateItem by id.")
    @Order(4)
    @Commit
	public void deleteFormModelFieldConcatenateItemById() {
    	Assertions.assertThat(formModelFieldConcatenateItem).isNotNull();	 
    	repository.deleteById(formModelFieldConcatenateItem.getId());
	    Optional<FormModelFieldConcatenateItem> notfound = repository.findById(formModelFieldConcatenateItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
