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

import com.moriset.bcephal.grid.domain.JoinColumnProperties;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinColumnPropertiesRepositoryIntegrationTests {

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
	private JoinColumnPropertiesRepository repository;
	
	private static JoinColumnProperties joinColumnProperties;
	
	@BeforeAll
	static void init() {
		joinColumnProperties = CreateFactory.buildJoinColumnProperties();
		assertThat(joinColumnProperties).isNotNull();
		assertThat(joinColumnProperties.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinColumnProperties")
	@Order(1)
	@Commit
	public void saveJoinColumnPropertiesTest() {
		assertThat(joinColumnProperties).isNotNull();
		repository.save(joinColumnProperties);
		
		assertThat(joinColumnProperties.getId()).isNotNull();
		Optional<JoinColumnProperties> found = repository.findById(joinColumnProperties.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(joinColumnProperties.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinColumnProperties")
	@Order(2)
	@Commit
	public void updateJoinColumnPropertiesTest() {
		assertThat(joinColumnProperties).isNotNull();
		assertThat(joinColumnProperties.getId()).isNotNull();
		joinColumnProperties.setColumnId(23L);
		repository.save(joinColumnProperties);
		
		assertThat(joinColumnProperties.getId()).isNotNull();
		Optional<JoinColumnProperties> found = repository.findById(joinColumnProperties.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getColumnId()).isEqualTo(joinColumnProperties.getColumnId());
	}
	
	@Test
	@DisplayName("Find joinColumnProperties by id")
	@Order(3)
	public void findJoinColumnPropertiesById() {
		assertThat(joinColumnProperties).isNotNull();
		assertThat(joinColumnProperties.getId()).isNotNull();
		
		Optional<JoinColumnProperties> found = repository.findById(joinColumnProperties.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getColumnId()).isEqualTo(joinColumnProperties.getColumnId());
	}
	
	@Test
    @DisplayName("Delete joinColumnProperties by id.")
    @Order(4)
    @Commit
	public void deleteJoinColumnPropertiesById() {
    	Assertions.assertThat(joinColumnProperties).isNotNull();	 
    	repository.deleteById(joinColumnProperties.getId());
	    Optional<JoinColumnProperties> notfound = repository.findById(joinColumnProperties.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
