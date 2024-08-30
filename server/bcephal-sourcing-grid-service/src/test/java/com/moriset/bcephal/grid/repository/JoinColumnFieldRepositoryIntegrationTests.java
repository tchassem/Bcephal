package com.moriset.bcephal.grid.repository;

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

import com.moriset.bcephal.grid.domain.JoinColumnField;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinColumnFieldRepositoryIntegrationTests {

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
	private JoinColumnFieldRepository repository;
	
	private static JoinColumnField joinColumnField;
	
	@BeforeAll
	static void init() {
		joinColumnField = CreateFactory.buildJoinColumnField();
		assertThat(joinColumnField).isNotNull();
		assertThat(joinColumnField.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinColumnField")
	@Order(1)
	@Commit
	public void saveJoinColumnFieldTest() {
		assertThat(joinColumnField).isNotNull();
		repository.save(joinColumnField);
		
		assertThat(joinColumnField.getId()).isNotNull();
		Optional<JoinColumnField> found = repository.findById(joinColumnField.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(joinColumnField.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinColumnField")
	@Order(2)
	@Commit
	public void updateJoinColumnFieldTest() {
		assertThat(joinColumnField).isNotNull();
		assertThat(joinColumnField.getId()).isNotNull();
		joinColumnField.setStringValue("New string value");
		repository.save(joinColumnField);
		
		assertThat(joinColumnField.getId()).isNotNull();
		Optional<JoinColumnField> found = repository.findById(joinColumnField.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(joinColumnField.getStringValue());
	}
	
	@Test
	@DisplayName("Find joinColumnField by id")
	@Order(3)
	public void findJoinColumnFieldById() {
		assertThat(joinColumnField).isNotNull();
		assertThat(joinColumnField.getId()).isNotNull();
		
		Optional<JoinColumnField> found = repository.findById(joinColumnField.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(joinColumnField.getStringValue());
	}
	
	@Test
    @DisplayName("Delete joinColumnField by id.")
    @Order(4)
    @Commit
	public void deleteJoinColumnFieldById() {
    	Assertions.assertThat(joinColumnField).isNotNull();	 
    	repository.deleteById(joinColumnField.getId());
	    Optional<JoinColumnField> notfound = repository.findById(joinColumnField.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
