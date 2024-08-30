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

import com.moriset.bcephal.grid.domain.JoinColumnConditionItemOperand;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinColumnConditionItemOperandRepositoryIntegrationTests {

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
	private JoinColumnConditionItemOperandRepository repository;
	
	private static JoinColumnConditionItemOperand itemOperand;
	
	@BeforeAll
	static void init() {
		itemOperand = CreateFactory.buildJoinColumnConditionItemOperand();
		assertThat(itemOperand).isNotNull();
		assertThat(itemOperand.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinColumnConditionItemOperand")
	@Order(1)
	@Commit
	public void saveJoinColumnConditionItemOperandTest() {
		assertThat(itemOperand).isNotNull();
		repository.save(itemOperand);
		
		assertThat(itemOperand.getId()).isNotNull();
		Optional<JoinColumnConditionItemOperand> found = repository.findById(itemOperand.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(itemOperand.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinColumnConditionItemOperand")
	@Order(2)
	@Commit
	public void updateJoinColumnConditionItemOperandTest() {
		assertThat(itemOperand).isNotNull();
		assertThat(itemOperand.getId()).isNotNull();
		itemOperand.setPosition(54);
		repository.save(itemOperand);
		
		assertThat(itemOperand.getId()).isNotNull();
		Optional<JoinColumnConditionItemOperand> found = repository.findById(itemOperand.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(itemOperand.getPosition());
	}
	
	@Test
	@DisplayName("Find joinColumnConditionItemOperand by id")
	@Order(3)
	public void findJoinColumnConditionItemOperandById() {
		assertThat(itemOperand).isNotNull();
		assertThat(itemOperand.getId()).isNotNull();
		
		Optional<JoinColumnConditionItemOperand> found = repository.findById(itemOperand.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getPosition()).isEqualTo(itemOperand.getPosition());
	}
	
	@Test
    @DisplayName("Delete joinColumnConditionItemOperand by id.")
    @Order(4)
    @Commit
	public void deleteJoinColumnConditionItemOperandById() {
    	Assertions.assertThat(itemOperand).isNotNull();	 
    	repository.deleteById(itemOperand.getId());
	    Optional<JoinColumnConditionItemOperand> notfound = repository.findById(itemOperand.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
