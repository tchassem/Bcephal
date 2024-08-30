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

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.grid.domain.form.FormModelSpot;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelSpotRepositoryIntegrationTests {

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
	private FormModelSpotRepository repository;
	
	private static FormModelSpot formModelSpot;
	
	@BeforeAll
	static void init() {
		formModelSpot = CreateFactory.buildFormModelSpot();
		assertThat(formModelSpot).isNotNull();
		assertThat(formModelSpot.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModelSpot")
	@Order(1)
	@Commit
	public void saveFormModelSpotTest() {
		assertThat(formModelSpot).isNotNull();
		repository.save(formModelSpot);
		
		assertThat(formModelSpot.getId()).isNotNull();
		Optional<FormModelSpot> found = repository.findById(formModelSpot.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getFormula()).isEqualTo(formModelSpot.getFormula());
	}
	
	@Test
	@DisplayName("Update an existing formModelSpot")
	@Order(2)
	@Commit
	public void updateJoinColumnConditionItemTest() {
		assertThat(formModelSpot).isNotNull();
		assertThat(formModelSpot.getId()).isNotNull();
		formModelSpot.setDataSourceType(DataSourceType.JOIN);;
		repository.save(formModelSpot);
		
		assertThat(formModelSpot.getId()).isNotNull();
		Optional<FormModelSpot> found = repository.findById(formModelSpot.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getDataSourceType()).isEqualTo(formModelSpot.getDataSourceType());
	}
	
	@Test
	@DisplayName("Find formModelSpot by id")
	@Order(3)
	public void findFormModelSpotById() {
		assertThat(formModelSpot).isNotNull();
		assertThat(formModelSpot.getId()).isNotNull();
		
		Optional<FormModelSpot> found = repository.findById(formModelSpot.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getFormula()).isEqualTo(formModelSpot.getFormula());
	}
	
	@Test
    @DisplayName("Delete formModelSpot by id.")
    @Order(4)
    @Commit
	public void deleteFormModelSpotById() {
    	Assertions.assertThat(formModelSpot).isNotNull();	 
    	repository.deleteById(formModelSpot.getId());
	    Optional<FormModelSpot> notfound = repository.findById(formModelSpot.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
