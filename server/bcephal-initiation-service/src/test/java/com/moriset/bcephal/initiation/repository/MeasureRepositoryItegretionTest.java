package com.moriset.bcephal.initiation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Measure;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MeasureRepositoryItegretionTest {
	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
@ActiveProfiles(profiles = { "int", "repo" })
static class ApplicationTests { }
	@Autowired MeasureRepository measureRepository;
	
	private static Measure measure;
	
	@BeforeAll static void init() {

		measure = Measure.builder()
				.name("newMeasue")
				.childrenListChangeHandler(new ListChangeHandler<>())
				.build();
		//measure = new InitiationFactory().buildMeasure();
	}

	
	@Test 
	@DisplayName("save a new measure test")
	@Order(1)
	@Commit
	void saveNewMeasure() {
		assertThat(measure).isNotNull();
		Measure newMeasure = measureRepository.save(measure);
		Optional<Measure> found = measureRepository.findById(measure.getId());
		assertThat(found).isNotEmpty();
		assertEquals(found.get().getId(), newMeasure.getId());
	}
	
	@Test 
	@DisplayName("update existing measure test")
	@Order(2)
	@Commit
	void updateMeasure() {
		assertThat(measure).isNotNull();
		measure.setName("updatedMesure");
		Measure measureUpdated = measureRepository.save(measure);
		assertEquals(measureUpdated.getName(), measure.getName());
	}
	
	@Test 
	@DisplayName("find measure by id test")
	@Order(3)
	@Commit
	void findMeasureById() {
		assertThat(measure).isNotNull();
		Optional<Measure> measureFound = measureRepository.findById(measure.getId());
		assertThat(measureFound).isNotEmpty();
		assertEquals(measureFound.get().getId(), measure.getId());
	}
	
	
	
	@Test 
	@DisplayName("delete measure test")
	@Order(10)
	@Commit
	void deleteMeasureTest() {
		assertThat(measure).isNotNull();
		measureRepository.delete(measure);
		Optional<Measure> found = measureRepository.findById(measure.getId());
		assertThat(found).isEmpty();
		
	}
}
