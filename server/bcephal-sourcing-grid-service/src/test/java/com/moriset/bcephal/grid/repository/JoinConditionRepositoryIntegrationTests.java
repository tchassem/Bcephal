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

import com.moriset.bcephal.grid.domain.JoinCondition;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinConditionRepositoryIntegrationTests {

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
	private JoinConditionRepository repository;
	
	private static JoinCondition condition;
	
	@BeforeAll
	static void init() {
		condition = CreateFactory.buildJoinCondition();
		assertThat(condition).isNotNull();
		assertThat(condition.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new condition")
	@Order(1)
	@Commit
	public void saveConditionTest() {
		assertThat(condition).isNotNull();
		repository.save(condition);
		
		assertThat(condition.getId()).isNotNull();
		Optional<JoinCondition> found = repository.findById(condition.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(condition.getId());
	}
	
	@Test
	@DisplayName("Update an existing condition")
	@Order(2)
	@Commit
	public void updateConditionTest() {
		assertThat(condition).isNotNull();
		assertThat(condition.getId()).isNotNull();
		condition.setVerb("Or");
		repository.save(condition);
		
		assertThat(condition.getId()).isNotNull();
		Optional<JoinCondition> found = repository.findById(condition.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getVerb()).isEqualTo(condition.getVerb());
	}
	
	@Test
	@DisplayName("Find condition by id")
	@Order(3)
	public void findconditionById() {
		assertThat(condition).isNotNull();
		assertThat(condition.getId()).isNotNull();
		
		Optional<JoinCondition> found = repository.findById(condition.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getVerb()).isEqualTo(condition.getVerb());
	}
	
	@Test
    @DisplayName("Delete condition by id.")
    @Order(4)
    @Commit
	public void deleteconditionById() {
    	Assertions.assertThat(condition).isNotNull();	 
    	repository.deleteById(condition.getId());
	    Optional<JoinCondition> notfound = repository.findById(condition.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
