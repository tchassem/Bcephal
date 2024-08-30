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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinColumnRepositoryIntegrationTests {

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
	private JoinColumnRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private static JoinColumn joinColumn;
	
	private static Join join;
	
	@BeforeAll
	static void init() {
		joinColumn = CreateFactory.buildJoinColumn();
		assertThat(joinColumn).isNotNull();
		assertThat(joinColumn.getId()).isNull();
		
		assertThat(join).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinColumn")
	@Order(1)
	@Commit
	public void saveJoinColumnTest() {
		assertThat(joinColumn).isNotNull();
		assertThat(join).isNull();
		
		Join joi = CreateFactory.buildJoin();
		join = entityManager.persistAndFlush(joi);
		
		assertThat(join.getId()).isNotNull();
		joinColumn.setJoinId(join);
		repository.save(joinColumn);
		
		assertThat(joinColumn.getId()).isNotNull();
		Optional<JoinColumn> found = repository.findById(joinColumn.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(joinColumn.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinColumn")
	@Order(2)
	@Commit
	public void updateJoinColumnTest() {
		assertThat(joinColumn).isNotNull();
		assertThat(joinColumn.getId()).isNotNull();
		joinColumn.setName("New name ..");
		repository.save(joinColumn);
		
		assertThat(joinColumn.getId()).isNotNull();
		Optional<JoinColumn> found = repository.findById(joinColumn.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(joinColumn.getName());
	}
	
	@Test
	@DisplayName("Find joinColumn by id")
	@Order(3)
	public void findJoinColumnById() {
		assertThat(joinColumn).isNotNull();
		assertThat(joinColumn.getId()).isNotNull();
		
		Optional<JoinColumn> found = repository.findById(joinColumn.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(joinColumn.getName());
	}
	
	@Test
    @DisplayName("Delete joinColumn by id.")
    @Order(4)
    @Commit
	public void deleteJoinColumnById() {
    	Assertions.assertThat(joinColumn).isNotNull();	 
    	repository.deleteById(joinColumn.getId());
	    Optional<JoinColumn> notfound = repository.findById(joinColumn.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete join")
    @Order(5)
    @Commit
	public void deleteGroupOfSpot() {
    	Assertions.assertThat(join).isNotNull();
    	Assertions.assertThat(join.getId()).isNotNull();
    	entityManager.clear();
    	Join join_ = entityManager.find(Join.class, join.getId());
	    
	    if(join_ != null) {
	    	entityManager.remove(join_);
	    }	 
	    Join join__ = entityManager.find(Join.class, join.getId());
	    Assertions.assertThat(join__).isNull();
	}
}
