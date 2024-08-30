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
import com.moriset.bcephal.grid.domain.JoinUnionKey;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinUnionKeyRepositoryIntegrationTests {

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
	private JoinUnionKeyRepository repository;
	
	private static JoinUnionKey joinUnionKey;
	
	@BeforeAll
	static void init() {
		joinUnionKey = CreateFactory.buildJoinUnionKey();
		assertThat(joinUnionKey).isNotNull();
		assertThat(joinUnionKey.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinUnionKey")
	@Order(1)
	@Commit
	public void saveJoinUnionKeyTest() {
		assertThat(joinUnionKey).isNotNull();
		repository.save(joinUnionKey);
		
		assertThat(joinUnionKey.getId()).isNotNull();
		Optional<JoinUnionKey> found = repository.findById(joinUnionKey.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(joinUnionKey.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinUnionKey")
	@Order(2)
	@Commit
	public void updateJoinUnionKeyTest() {
		assertThat(joinUnionKey).isNotNull();
		assertThat(joinUnionKey.getId()).isNotNull();
		joinUnionKey.setGridType(JoinGridType.JOIN);
		repository.save(joinUnionKey);
		
		assertThat(joinUnionKey.getId()).isNotNull();
		Optional<JoinUnionKey> found = repository.findById(joinUnionKey.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getGridType()).isEqualTo(joinUnionKey.getGridType());
	}
	
	@Test
	@DisplayName("Find joinUnionKey by id")
	@Order(3)
	public void findJoinUnionKeyById() {
		assertThat(joinUnionKey).isNotNull();
		assertThat(joinUnionKey.getId()).isNotNull();
		
		Optional<JoinUnionKey> found = repository.findById(joinUnionKey.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getGridType()).isEqualTo(joinUnionKey.getGridType());
	}
	
	@Test
    @DisplayName("Delete joinUnionKey by id.")
    @Order(4)
    @Commit
	public void deleteJoinUnionKeyById() {
    	Assertions.assertThat(joinUnionKey).isNotNull();	 
    	repository.deleteById(joinUnionKey.getId());
	    Optional<JoinUnionKey> notfound = repository.findById(joinUnionKey.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
