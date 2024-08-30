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

import com.moriset.bcephal.grid.domain.JoinColumnCalculateItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinColumnCalculateItemRepositoryIntegrationTests {

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
	private JoinColumnCalculateItemRepository repository;
	
	private static JoinColumnCalculateItem columnCalculateItem;
	
	@BeforeAll
	static void init() {
		columnCalculateItem = CreateFactory.buildJoinColumnCalculateItem();
		assertThat(columnCalculateItem).isNotNull();
		assertThat(columnCalculateItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinColumnCalculateItem")
	@Order(1)
	@Commit
	public void saveJoinColumnCalculateItemTest() {
		assertThat(columnCalculateItem).isNotNull();
		repository.save(columnCalculateItem);
		
		assertThat(columnCalculateItem.getId()).isNotNull();
		Optional<JoinColumnCalculateItem> found = repository.findById(columnCalculateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(columnCalculateItem.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinColumnCalculateItem")
	@Order(2)
	@Commit
	public void updateJoinColumnCalculateItemTest() {
		assertThat(columnCalculateItem).isNotNull();
		assertThat(columnCalculateItem.getId()).isNotNull();
		columnCalculateItem.setPosition(54);
		repository.save(columnCalculateItem);
		
		assertThat(columnCalculateItem.getId()).isNotNull();
		Optional<JoinColumnCalculateItem> found = repository.findById(columnCalculateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(columnCalculateItem.getPosition());
	}
	
	@Test
	@DisplayName("Find joinColumnCalculateItem by id")
	@Order(3)
	public void findJoinColumnCalculateItemById() {
		assertThat(columnCalculateItem).isNotNull();
		assertThat(columnCalculateItem.getId()).isNotNull();
		
		Optional<JoinColumnCalculateItem> found = repository.findById(columnCalculateItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(columnCalculateItem.getPosition());
	}
	
	@Test
    @DisplayName("Delete joinColumnCalculateItem by id.")
    @Order(4)
    @Commit
	public void deleteJoinColumnCalculateItemById() {
    	Assertions.assertThat(columnCalculateItem).isNotNull();	 
    	repository.deleteById(columnCalculateItem.getId());
	    Optional<JoinColumnCalculateItem> notfound = repository.findById(columnCalculateItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
