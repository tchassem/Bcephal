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
import com.moriset.bcephal.grid.domain.JoinKey;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinKeyRepositoryIntegrationTests {

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
	private JoinKeyRepository repository;
	
	private static JoinKey joinKey;
	
	@BeforeAll
	static void init() {
		joinKey = CreateFactory.buildJoinKey();
		assertThat(joinKey).isNotNull();
		assertThat(joinKey.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinKey")
	@Order(1)
	@Commit
	public void saveJoinKeyTest() {
		assertThat(joinKey).isNotNull();
		repository.save(joinKey);
		
		assertThat(joinKey.getId()).isNotNull();
		Optional<JoinKey> found = repository.findById(joinKey.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(joinKey.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinKey")
	@Order(2)
	@Commit
	public void updateJoinKeyTest() {
		assertThat(joinKey).isNotNull();
		assertThat(joinKey.getId()).isNotNull();
		joinKey.setGridType1(JoinGridType.GRID);
		repository.save(joinKey);
		
		assertThat(joinKey.getId()).isNotNull();
		Optional<JoinKey> found = repository.findById(joinKey.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getGridId1()).isEqualTo(joinKey.getGridId1());
	}
	
	@Test
	@DisplayName("Find joinKey by id")
	@Order(3)
	public void findJoinKeyById() {
		assertThat(joinKey).isNotNull();
		assertThat(joinKey.getId()).isNotNull();
		
		Optional<JoinKey> found = repository.findById(joinKey.getId());
		assertThat(found.isPresent()).isTrue();
	}
	
	@Test
    @DisplayName("Delete joinKey by id.")
    @Order(4)
    @Commit
	public void deleteJoinKeyById() {
    	Assertions.assertThat(joinKey).isNotNull();	 
    	repository.deleteById(joinKey.getId());
	    Optional<JoinKey> notfound = repository.findById(joinKey.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
