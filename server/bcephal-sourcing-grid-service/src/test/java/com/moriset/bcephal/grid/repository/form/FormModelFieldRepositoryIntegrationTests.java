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

import com.moriset.bcephal.grid.domain.form.FormModelField;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelFieldRepositoryIntegrationTests {

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
	private FormModelFieldRepository repository;
	
	private static FormModelField formModelField;
	
	@BeforeAll
	static void init() {
		formModelField = CreateFactory.buildFormModelField();
		assertThat(formModelField).isNotNull();
		assertThat(formModelField.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelField")
	@Order(1)
	@Commit
	public void saveFormModelFieldTest() {
		assertThat(formModelField).isNotNull();
		repository.save(formModelField);
		
		assertThat(formModelField.getId()).isNotNull();
		Optional<FormModelField> found = repository.findById(formModelField.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelField.getId());
		assertThat(found.get().getName()).isEqualTo(formModelField.getName());
	}
	
	@Test
	@DisplayName("Update an existing formModelField")
	@Order(2)
	@Commit
	public void updateFormModelFieldTest() {
		assertThat(formModelField).isNotNull();
		assertThat(formModelField.getId()).isNotNull();
		formModelField.setName("New name ...");
		repository.save(formModelField);
		
		assertThat(formModelField.getId()).isNotNull();
		Optional<FormModelField> found = repository.findById(formModelField.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModelField.getName());
	}
	
	@Test
	@DisplayName("Find formModelField by id")
	@Order(3)
	public void findFormModelFieldById() {
		assertThat(formModelField).isNotNull();
		assertThat(formModelField.getId()).isNotNull();
		
		Optional<FormModelField> found = repository.findById(formModelField.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModelField.getName());
	}
	
	@Test
    @DisplayName("Delete formModelField by id.")
    @Order(4)
    @Commit
	public void deleteFormModelFieldById() {
    	Assertions.assertThat(formModelField).isNotNull();	 
    	repository.deleteById(formModelField.getId());
	    Optional<FormModelField> notfound = repository.findById(formModelField.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
