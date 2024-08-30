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

import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.JoinUnionKeyItem;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinUnionKeyItemRepositoryIntegrationTests {

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
	private JoinUnionKeyItemRepository repository;
	
	private static JoinUnionKeyItem joinUnionKeyItem;
	
	@BeforeAll
	static void init() {
		joinUnionKeyItem = CreateFactory.buildJoinUnionKeyItem();
		assertThat(joinUnionKeyItem).isNotNull();
		assertThat(joinUnionKeyItem.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinUnionKeyItem")
	@Order(1)
	@Commit
	public void saveJoinUnionKeyItemTest() {
		assertThat(joinUnionKeyItem).isNotNull();
		repository.save(joinUnionKeyItem);
		
		assertThat(joinUnionKeyItem.getId()).isNotNull();
		Optional<JoinUnionKeyItem> found = repository.findById(joinUnionKeyItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(joinUnionKeyItem.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinUnionKeyItem")
	@Order(2)
	@Commit
	public void updateJoinUnionKeyItemTest() {
		assertThat(joinUnionKeyItem).isNotNull();
		assertThat(joinUnionKeyItem.getId()).isNotNull();
		joinUnionKeyItem.setGridType(JoinGridType.JOIN);
		repository.save(joinUnionKeyItem);
		
		assertThat(joinUnionKeyItem.getId()).isNotNull();
		Optional<JoinUnionKeyItem> found = repository.findById(joinUnionKeyItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getGridType()).isEqualTo(joinUnionKeyItem.getGridType());
	}
	
	@Test
	@DisplayName("Find joinUnionKeyItem by id")
	@Order(3)
	public void findJoinUnionKeyItemById() {
		assertThat(joinUnionKeyItem).isNotNull();
		assertThat(joinUnionKeyItem.getId()).isNotNull();
		
		Optional<JoinUnionKeyItem> found = repository.findById(joinUnionKeyItem.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getGridType()).isEqualTo(joinUnionKeyItem.getGridType());
	}
	
	@Test
    @DisplayName("Delete joinUnionKeyItem by id.")
    @Order(4)
    @Commit
	public void deleteJoinUnionKeyItemById() {
    	Assertions.assertThat(joinUnionKeyItem).isNotNull();	 
    	repository.deleteById(joinUnionKeyItem.getId());
	    Optional<JoinUnionKeyItem> notfound = repository.findById(joinUnionKeyItem.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
