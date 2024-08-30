package com.moriset.bcephal.initiation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.Optional;

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

import com.moriset.bcephal.domain.parameter.IncrementalNumber;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IncrementalNumberRepositoryIntegrationTest {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
@ActiveProfiles(profiles = { "int", "repo" })
static class ApplicationTests { }
	
	@Autowired IncrementalNumberRepository incrementalNumberRepository;
	private static IncrementalNumber incrementalNumber;
	
	@BeforeAll
	static void init() {
		incrementalNumber = IncrementalNumber.builder().name("newIncrem")
				.creationDate(new Timestamp(System.currentTimeMillis())).modificationDate(new Timestamp(System.currentTimeMillis())).build();
		
	}
	
	@Test
	@DisplayName("save new incrementalNumber")
	@Order(1)
	@Commit
	void saveNewIncrementalNumberTest() {
		assertThat(incrementalNumber).isNotNull();
		IncrementalNumber newIncrementalNumber = incrementalNumberRepository.save(incrementalNumber);
		Optional<IncrementalNumber> found = incrementalNumberRepository.findById(newIncrementalNumber.getId());
		assertThat(found).isNotNull();
		assertEquals(found.get().getId(), incrementalNumber.getId());
		
	}
	
	@Test
	@DisplayName("update existing incrementalNumber")
	@Order(2)
	@Commit
	void updateIncrementalNumberTest() {
		assertThat(incrementalNumber).isNotNull();
		incrementalNumber.setName("updateIncrem");
		IncrementalNumber updatedIncrementalNumber = incrementalNumberRepository.save(incrementalNumber);
		Optional<IncrementalNumber> found = incrementalNumberRepository.findById(updatedIncrementalNumber.getId());
		assertThat(found).isNotNull();
		assertEquals(found.get().getName(), incrementalNumber.getName());
		
	}
	
	@Test
	@DisplayName("find incrementalNumber by id")
	@Order(3)
	@Commit
	void findIncrementalNumberByIdTest() {
		assertThat(incrementalNumber).isNotNull();
		Optional<IncrementalNumber> foundIncrementalNumber = incrementalNumberRepository.findById(incrementalNumber.getId());
		assertThat(foundIncrementalNumber).isNotNull();
		assertEquals(foundIncrementalNumber.get().getId(), incrementalNumber.getId());
		
	}
	
	
	@Test
	@DisplayName("find incrementalNumber by name")
	@Order(3)
	@Commit
	void findIncrementalNumberByNameTest() {
		assertThat(incrementalNumber).isNotNull();
		IncrementalNumber foundIncrementalNumber = incrementalNumberRepository.findByName(incrementalNumber.getName());
		assertThat(foundIncrementalNumber).isNotNull();
		assertEquals(foundIncrementalNumber.getName(), incrementalNumber.getName());
		
	}
	
	
	@Test
	@DisplayName("delete incrementalNumber")
	@Order(10)
	@Commit
	void deleteIncrementalNumberTest() {
		assertThat(incrementalNumber).isNotNull();
	    incrementalNumberRepository.delete(incrementalNumber);
		Optional<IncrementalNumber> found = incrementalNumberRepository.findById(incrementalNumber.getId());
		assertThat(found).isEmpty();
	}
	
}
