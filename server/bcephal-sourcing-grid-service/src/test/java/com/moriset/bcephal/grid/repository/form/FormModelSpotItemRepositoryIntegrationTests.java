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

import com.moriset.bcephal.grid.domain.form.FormModelSpotItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelSpotItemRepositoryIntegrationTests {

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
	private FormModelSpotItemRepository repository;
	
	private static FormModelSpotItem item;
	
	@BeforeAll
	static void init() {
		item = CreateFactory.buildFormModelSpotItem();
		assertThat(item).isNotNull();
		assertThat(item.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new FormModelSpotItem")
	@Order(1)
	@Commit
	public void saveFormModelSpotItemTest() {
		assertThat(item).isNotNull();
		repository.save(item);
		
		assertThat(item.getId()).isNotNull();
		Optional<FormModelSpotItem> found = repository.findById(item.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(item.getStringValue());
	}
	
	@Test
	@DisplayName("Update an existing FormModelSpotItem")
	@Order(2)
	@Commit
	public void updateFormModelSpotItemTest() {
		assertThat(item).isNotNull();
		assertThat(item.getId()).isNotNull();
		item.setStringValue("Value ...");;
		repository.save(item);
		
		assertThat(item.getId()).isNotNull();
		Optional<FormModelSpotItem> found = repository.findById(item.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(item.getStringValue());
	}
	
	@Test
	@DisplayName("Find FormModelSpotItem by id")
	@Order(3)
	public void findFormModelSpotItemById() {
		assertThat(item).isNotNull();
		assertThat(item.getId()).isNotNull();
		
		Optional<FormModelSpotItem> found = repository.findById(item.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(item.getStringValue());
	}
	
	@Test
    @DisplayName("Delete FormModelSpotItem by id.")
    @Order(4)
    @Commit
	public void deleteFormModelSpotItemById() {
    	Assertions.assertThat(item).isNotNull();	 
    	repository.deleteById(item.getId());
	    Optional<FormModelSpotItem> notfound = repository.findById(item.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
