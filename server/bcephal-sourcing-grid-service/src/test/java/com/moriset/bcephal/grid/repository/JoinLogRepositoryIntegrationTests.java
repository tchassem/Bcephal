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

import com.moriset.bcephal.grid.domain.JoinLog;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinLogRepositoryIntegrationTests {

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
	private JoinLogRepository repository;
	
	private static JoinLog joinLog;
	
	@BeforeAll
	static void init() {
		joinLog = CreateFactory.buildJoinLog();
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new joinLog")
	@Order(1)
	@Commit
	public void savejoinLogTest() {
		assertThat(joinLog).isNotNull();
		repository.save(joinLog);
		
		assertThat(joinLog.getId()).isNotNull();
		Optional<JoinLog> found = repository.findById(joinLog.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getId()).isEqualTo(joinLog.getId());
	}
	
	@Test
	@DisplayName("Update an existing joinLog")
	@Order(2)
	@Commit
	public void updatejoinLogTest() {
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getId()).isNotNull();
		joinLog.setName("New name ..");
		repository.save(joinLog);
		
		assertThat(joinLog.getId()).isNotNull();
		Optional<JoinLog> found = repository.findById(joinLog.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(joinLog.getName());
	}
	
	@Test
	@DisplayName("Find joinLog by id")
	@Order(3)
	public void findjoinLogById() {
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getId()).isNotNull();
		
		Optional<JoinLog> found = repository.findById(joinLog.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(joinLog.getName());
	}
	
	@Test
    @DisplayName("Delete joinLog by id.")
    @Order(4)
    @Commit
	public void deletejoinLogById() {
    	Assertions.assertThat(joinLog).isNotNull();	 
    	repository.deleteById(joinLog.getId());
	    Optional<JoinLog> notfound = repository.findById(joinLog.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}
