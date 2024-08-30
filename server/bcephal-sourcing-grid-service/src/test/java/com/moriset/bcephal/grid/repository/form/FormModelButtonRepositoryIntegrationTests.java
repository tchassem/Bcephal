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

import com.moriset.bcephal.grid.domain.form.FormModelButton;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelButtonRepositoryIntegrationTests {

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
	private FormModelButtonRepository repository;
	
	private static FormModelButton formModelButton;
	
	@BeforeAll
	static void init() {
		formModelButton = CreateFactory.buildFormModelButton();
		assertThat(formModelButton).isNotNull();
		assertThat(formModelButton.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelButton")
	@Order(1)
	@Commit
	public void saveFormModelButtonTest() {
		assertThat(formModelButton).isNotNull();
		repository.save(formModelButton);
		
		assertThat(formModelButton.getId()).isNotNull();
		Optional<FormModelButton> found = repository.findById(formModelButton.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelButton.getId());
		assertThat(found.get().getName()).isEqualTo(formModelButton.getName());
	}
	
	@Test
	@DisplayName("Update an existing formModelButton")
	@Order(2)
	@Commit
	public void updateFormModelButtonTest() {
		assertThat(formModelButton).isNotNull();
		assertThat(formModelButton.getId()).isNotNull();
		formModelButton.setName("New name value ...");
		repository.save(formModelButton);
		
		assertThat(formModelButton.getId()).isNotNull();
		Optional<FormModelButton> found = repository.findById(formModelButton.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModelButton.getName());
	}
	
	@Test
	@DisplayName("Find formModelButton by id")
	@Order(3)
	public void findFormModelButtonById() {
		assertThat(formModelButton).isNotNull();
		assertThat(formModelButton.getId()).isNotNull();
		
		Optional<FormModelButton> found = repository.findById(formModelButton.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModelButton.getName());
	}
	
	@Test
    @DisplayName("Delete formModelButton by id.")
    @Order(4)
    @Commit
	public void deleteFormModelButtonById() {
    	Assertions.assertThat(formModelButton).isNotNull();	 
    	repository.deleteById(formModelButton.getId());
	    Optional<FormModelButton> notfound = repository.findById(formModelButton.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
