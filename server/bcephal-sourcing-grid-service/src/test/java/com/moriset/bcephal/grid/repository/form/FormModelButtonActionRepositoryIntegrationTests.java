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

import com.moriset.bcephal.grid.domain.form.FormModelButtonAction;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelButtonActionRepositoryIntegrationTests {

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
	private FormModelButtonActionRepository repository;
	
	private static FormModelButtonAction formModelButtonAction;
	
	@BeforeAll
	static void init() {
		formModelButtonAction = CreateFactory.buildFormModelButtonAction();
		assertThat(formModelButtonAction).isNotNull();
		assertThat(formModelButtonAction.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelButtonAction")
	@Order(1)
	@Commit
	public void saveFormModelButtonActionTest() {
		assertThat(formModelButtonAction).isNotNull();
		repository.save(formModelButtonAction);
		
		assertThat(formModelButtonAction.getId()).isNotNull();
		Optional<FormModelButtonAction> found = repository.findById(formModelButtonAction.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelButtonAction.getId());
		assertThat(found.get().getStringValue()).isEqualTo(formModelButtonAction.getStringValue());
	}
	
	@Test
	@DisplayName("Update an existing formModelButtonAction")
	@Order(2)
	@Commit
	public void updateFormModelButtonActionTest() {
		assertThat(formModelButtonAction).isNotNull();
		assertThat(formModelButtonAction.getId()).isNotNull();
		formModelButtonAction.setStringValue("String value ...");
		repository.save(formModelButtonAction);
		
		assertThat(formModelButtonAction.getId()).isNotNull();
		Optional<FormModelButtonAction> found = repository.findById(formModelButtonAction.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelButtonAction.getStringValue());
	}
	
	@Test
	@DisplayName("Find formModelButtonAction by id")
	@Order(3)
	public void findFormModelButtonActionById() {
		assertThat(formModelButtonAction).isNotNull();
		assertThat(formModelButtonAction.getId()).isNotNull();
		
		Optional<FormModelButtonAction> found = repository.findById(formModelButtonAction.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(formModelButtonAction.getStringValue());
	}
	
	@Test
    @DisplayName("Delete formModelButtonAction by id.")
    @Order(4)
    @Commit
	public void deleteFormModelButtonActionById() {
    	Assertions.assertThat(formModelButtonAction).isNotNull();	 
    	repository.deleteById(formModelButtonAction.getId());
	    Optional<FormModelButtonAction> notfound = repository.findById(formModelButtonAction.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
