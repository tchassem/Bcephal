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

import com.moriset.bcephal.grid.domain.form.FormModelFieldCalculateItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelFieldCalculateItemRepositoryIntegrationTests {

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
	private FormModelFieldCalculateItemRepository repository;
	
	private static FormModelFieldCalculateItem formModelFieldCalculateItem;
	
	@BeforeAll
	static void init() {
		formModelFieldCalculateItem = CreateFactory.buildFormModelFieldCalculateItem();
		assertThat(formModelFieldCalculateItem).isNotNull();
		assertThat(formModelFieldCalculateItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelFieldCalculateItem")
	@Order(1)
	@Commit
	public void saveFormModelFieldCalculateItemTest() {
		assertThat(formModelFieldCalculateItem).isNotNull();
		repository.save(formModelFieldCalculateItem);
		
		assertThat(formModelFieldCalculateItem.getId()).isNotNull();
		Optional<FormModelFieldCalculateItem> found = repository.findById(formModelFieldCalculateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(formModelFieldCalculateItem.getId());
		assertThat(found.get().getSign()).isEqualTo(formModelFieldCalculateItem.getSign());
	}
	
	@Test
	@DisplayName("Update an existing formModelFieldCalculateItem")
	@Order(2)
	@Commit
	public void updateFormModelFieldCalculateItemTest() {
		assertThat(formModelFieldCalculateItem).isNotNull();
		assertThat(formModelFieldCalculateItem.getId()).isNotNull();
		formModelFieldCalculateItem.setSign("-");
		repository.save(formModelFieldCalculateItem);
		
		assertThat(formModelFieldCalculateItem.getId()).isNotNull();
		Optional<FormModelFieldCalculateItem> found = repository.findById(formModelFieldCalculateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getSign()).isEqualTo(formModelFieldCalculateItem.getSign());
	}
	
	@Test
	@DisplayName("Find formModelFieldCalculateItem by id")
	@Order(3)
	public void findFormModelFieldCalculateItemById() {
		assertThat(formModelFieldCalculateItem).isNotNull();
		assertThat(formModelFieldCalculateItem.getId()).isNotNull();
		
		Optional<FormModelFieldCalculateItem> found = repository.findById(formModelFieldCalculateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getSign()).isEqualTo(formModelFieldCalculateItem.getSign());
	}
	
	@Test
    @DisplayName("Delete formModelFieldCalculateItem by id.")
    @Order(4)
    @Commit
	public void deleteFormModelFieldCalculateItemById() {
    	Assertions.assertThat(formModelFieldCalculateItem).isNotNull();	 
    	repository.deleteById(formModelFieldCalculateItem.getId());
	    Optional<FormModelFieldCalculateItem> notfound = repository.findById(formModelFieldCalculateItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
