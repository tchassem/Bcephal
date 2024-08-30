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

import com.moriset.bcephal.grid.domain.JoinColumnConditionItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;


@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinColumnConditionItemRepositoryIntegrationTests {

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
	private JoinColumnConditionItemRepository repository;
	
	private static JoinColumnConditionItem jConditionItem;
	
	@BeforeAll
	static void init() {
		jConditionItem = CreateFactory.buildJoinColumnConditionItem();
		assertThat(jConditionItem).isNotNull();
		assertThat(jConditionItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinColumnConditionItem")
	@Order(1)
	@Commit
	public void saveJoinColumnConditionItemTest() {
		assertThat(jConditionItem).isNotNull();
		repository.save(jConditionItem);
		
		assertThat(jConditionItem.getId()).isNotNull();
		Optional<JoinColumnConditionItem> found = repository.findById(jConditionItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(jConditionItem.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinColumnConditionItem")
	@Order(2)
	@Commit
	public void updateJoinColumnConditionItemTest() {
		assertThat(jConditionItem).isNotNull();
		assertThat(jConditionItem.getId()).isNotNull();
		jConditionItem.setPosition(54);
		repository.save(jConditionItem);
		
		assertThat(jConditionItem.getId()).isNotNull();
		Optional<JoinColumnConditionItem> found = repository.findById(jConditionItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(jConditionItem.getPosition());
	}
	
	@Test
	@DisplayName("Find joinColumnConditionItem by id")
	@Order(3)
	public void findJoinColumnConditionItemById() {
		assertThat(jConditionItem).isNotNull();
		assertThat(jConditionItem.getId()).isNotNull();
		
		Optional<JoinColumnConditionItem> found = repository.findById(jConditionItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(jConditionItem.getPosition());
	}
	
	@Test
    @DisplayName("Delete joinColumnConditionItem by id.")
    @Order(4)
    @Commit
	public void deleteJoinColumnConditionItemById() {
    	Assertions.assertThat(jConditionItem).isNotNull();	 
    	repository.deleteById(jConditionItem.getId());
	    Optional<JoinColumnConditionItem> notfound = repository.findById(jConditionItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
