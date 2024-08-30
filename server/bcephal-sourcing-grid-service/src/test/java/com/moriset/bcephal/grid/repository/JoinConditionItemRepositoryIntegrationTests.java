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

import com.moriset.bcephal.grid.domain.JoinConditionItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinConditionItemRepositoryIntegrationTests {

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
	private JoinConditionItemRepository repository;
	
	private static JoinConditionItem conditionItem;
	
	@BeforeAll
	static void init() {
		conditionItem = CreateFactory.buildJoinConditionItem();
		assertThat(conditionItem).isNotNull();
		assertThat(conditionItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new conditionItem")
	@Order(1)
	@Commit
	public void saveConditionItemTest() {
		assertThat(conditionItem).isNotNull();
		repository.save(conditionItem);
		
		assertThat(conditionItem.getId()).isNotNull();
		Optional<JoinConditionItem> found = repository.findById(conditionItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(conditionItem.getId());
	}
	
	@Test
	@DisplayName("Update an existing conditionItem")
	@Order(2)
	@Commit
	public void updateConditionItemTest() {
		assertThat(conditionItem).isNotNull();
		assertThat(conditionItem.getId()).isNotNull();
		conditionItem.setStringValue("New string");
		repository.save(conditionItem);
		
		assertThat(conditionItem.getId()).isNotNull();
		Optional<JoinConditionItem> found = repository.findById(conditionItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(conditionItem.getStringValue());
	}
	
	@Test
	@DisplayName("Find conditionItem by id")
	@Order(3)
	public void findJoinConditionItemById() {
		assertThat(conditionItem).isNotNull();
		assertThat(conditionItem.getId()).isNotNull();
		
		Optional<JoinConditionItem> found = repository.findById(conditionItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStringValue()).isEqualTo(conditionItem.getStringValue());
	}
	
	@Test
    @DisplayName("Delete conditionItem by id.")
    @Order(4)
    @Commit
	public void deleteJoinConditionItemById() {
    	Assertions.assertThat(conditionItem).isNotNull();	 
    	repository.deleteById(conditionItem.getId());
	    Optional<JoinConditionItem> notfound = repository.findById(conditionItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
