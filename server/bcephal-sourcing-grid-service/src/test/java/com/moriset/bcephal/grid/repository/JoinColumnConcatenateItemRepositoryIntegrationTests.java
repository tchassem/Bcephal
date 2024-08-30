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

import com.moriset.bcephal.grid.domain.JoinColumnConcatenateItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinColumnConcatenateItemRepositoryIntegrationTests {

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
	private JoinColumnConcatenateItemRepository repository;
	
	private static JoinColumnConcatenateItem concatenateItem;
	
	@BeforeAll
	static void init() {
		concatenateItem = CreateFactory.buildJoinColumnConcatenateItem();
		assertThat(concatenateItem).isNotNull();
		assertThat(concatenateItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinColumnConcatenateItem")
	@Order(1)
	@Commit
	public void saveJoinColumnConcatenateItemTest() {
		assertThat(concatenateItem).isNotNull();
		repository.save(concatenateItem);
		
		assertThat(concatenateItem.getId()).isNotNull();
		Optional<JoinColumnConcatenateItem> found = repository.findById(concatenateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(concatenateItem.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinColumnConcatenateItem")
	@Order(2)
	@Commit
	public void updateJoinColumnConcatenateItemTest() {
		assertThat(concatenateItem).isNotNull();
		assertThat(concatenateItem.getId()).isNotNull();
		concatenateItem.setPosition(54);
		repository.save(concatenateItem);
		
		assertThat(concatenateItem.getId()).isNotNull();
		Optional<JoinColumnConcatenateItem> found = repository.findById(concatenateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(concatenateItem.getPosition());
	}
	
	@Test
	@DisplayName("Find joinColumnConcatenateItem by id")
	@Order(3)
	public void findJoinColumnConcatenateItemById() {
		assertThat(concatenateItem).isNotNull();
		assertThat(concatenateItem.getId()).isNotNull();
		
		Optional<JoinColumnConcatenateItem> found = repository.findById(concatenateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(concatenateItem.getPosition());
	}
	
	@Test
    @DisplayName("Delete joinColumnConcatenateItem by id.")
    @Order(4)
    @Commit
	public void deleteJoinColumnConcatenateItemById() {
    	Assertions.assertThat(concatenateItem).isNotNull();	 
    	repository.deleteById(concatenateItem.getId());
	    Optional<JoinColumnConcatenateItem> notfound = repository.findById(concatenateItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
