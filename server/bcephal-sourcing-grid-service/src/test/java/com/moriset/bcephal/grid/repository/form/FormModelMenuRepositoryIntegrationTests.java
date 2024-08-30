package com.moriset.bcephal.grid.repository.form;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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

import com.moriset.bcephal.grid.domain.form.FormModelMenu;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelMenuRepositoryIntegrationTests {

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
	private FormModelMenuRepository repository;
	
	private static FormModelMenu formModelMenu;
	
	@BeforeAll
	static void init() {
		formModelMenu = CreateFactory.buildFormModelMenu();
		assertThat(formModelMenu).isNotNull();
		assertThat(formModelMenu.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelMenu")
	@Order(1)
	@Commit
	public void saveFormModelMenuTest() {
		assertThat(formModelMenu).isNotNull();
		repository.save(formModelMenu);
		
		assertThat(formModelMenu.getId()).isNotNull();
		Optional<FormModelMenu> found = repository.findById(formModelMenu.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelMenu.getId());
		assertThat(found.get().getName()).isEqualTo(formModelMenu.getName());
	}
	
	@Test
	@DisplayName("Update an existing formModelMenu")
	@Order(2)
	@Commit
	public void updateFormModelMenuTest() {
		assertThat(formModelMenu).isNotNull();
		assertThat(formModelMenu.getId()).isNotNull();
		formModelMenu.setName("New name ..");
		repository.save(formModelMenu);
		
		assertThat(formModelMenu.getId()).isNotNull();
		Optional<FormModelMenu> found = repository.findById(formModelMenu.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModelMenu.getName());
	}
	
	@Test
	@DisplayName("Find formModelMenu by id")
	@Order(3)
	public void findFormModelMenuById() {
		assertThat(formModelMenu).isNotNull();
		assertThat(formModelMenu.getId()).isNotNull();
		
		Optional<FormModelMenu> found = repository.findById(formModelMenu.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModelMenu.getName());
	}

	@Test
	@DisplayName("Find formModelMenu by active")
	@Order(4)
	public void findFormModelMenuByActive() {
		assertThat(formModelMenu).isNotNull();
		assertThat(formModelMenu.getId()).isNotNull();
		
		List<FormModelMenu> result = repository.findByActive(true);
		assertThat(result.size() == 1).isTrue();
		assertThat(result.get(0).getName()).isEqualTo(formModelMenu.getName());
	}
	
	@Test
    @DisplayName("Delete formModelMenu by id.")
    @Order(5)
    @Commit
	public void deleteFormModelMenuById() {
    	Assertions.assertThat(formModelMenu).isNotNull();	
    	repository.delete(formModelMenu);
	    Optional<FormModelMenu> notfound = repository.findById(formModelMenu.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
