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

import com.moriset.bcephal.grid.domain.form.FormModelFieldReference;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelFieldReferenceRepositoryIntegrationTests {

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
	private FormModelFieldReferenceRepository repository;
	
	private static FormModelFieldReference formModelFieldReference;
	
	@BeforeAll
	static void init() {
		formModelFieldReference = CreateFactory.buildFormModelFieldReference();
		assertThat(formModelFieldReference).isNotNull();
		assertThat(formModelFieldReference.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelFieldReference")
	@Order(1)
	@Commit
	public void saveFormModelFieldReferenceTest() {
		assertThat(formModelFieldReference).isNotNull();
		repository.save(formModelFieldReference);
		
		assertThat(formModelFieldReference.getId()).isNotNull();
		Optional<FormModelFieldReference> found = repository.findById(formModelFieldReference.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelFieldReference.getId());
		assertThat(found.get().getFormula()).isEqualTo(formModelFieldReference.getFormula());
	}
	
	@Test
	@DisplayName("Update an existing formModelFieldReference")
	@Order(2)
	@Commit
	public void updateFormModelFieldReferenceTest() {
		assertThat(formModelFieldReference).isNotNull();
		assertThat(formModelFieldReference.getId()).isNotNull();
		formModelFieldReference.setFormula("AVERAGE");
		repository.save(formModelFieldReference);
		
		assertThat(formModelFieldReference.getId()).isNotNull();
		Optional<FormModelFieldReference> found = repository.findById(formModelFieldReference.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getFormula()).isEqualTo(formModelFieldReference.getFormula());
	}
	
	@Test
	@DisplayName("Find formModelFieldReference by id")
	@Order(3)
	public void findFormModelFieldReferenceById() {
		assertThat(formModelFieldReference).isNotNull();
		assertThat(formModelFieldReference.getId()).isNotNull();
		
		Optional<FormModelFieldReference> found = repository.findById(formModelFieldReference.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getFormula()).isEqualTo(formModelFieldReference.getFormula());
	}
	
	@Test
    @DisplayName("Delete formModelFieldReference by id.")
    @Order(4)
    @Commit
	public void deleteFormModelFieldReferenceById() {
    	Assertions.assertThat(formModelFieldReference).isNotNull();	 
    	repository.deleteById(formModelFieldReference.getId());
	    Optional<FormModelFieldReference> notfound = repository.findById(formModelFieldReference.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
