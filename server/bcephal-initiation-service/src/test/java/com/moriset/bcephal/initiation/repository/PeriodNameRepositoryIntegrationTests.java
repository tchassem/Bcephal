package com.moriset.bcephal.initiation.repository;

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

import com.moriset.bcephal.initiation.domain.PeriodName;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PeriodNameRepositoryIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
			"com.moriset.bcephal.config" })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests {
	}
	
	private static PeriodName periodName;

	@Autowired
	PeriodNameRepository periodNameRepository;
	
	

	@BeforeAll
	static void init() {
		periodName = PeriodName.builder().name("Period for test").position(0).build();
		Assertions.assertThat(periodName).isNotNull();
		Assertions.assertThat(periodName.getId()).isNull();
	}

	@Test
	@DisplayName("save a new period")
	@Order(1)
	@Commit
	void savePeriodTest() {
		periodNameRepository.save(periodName);
		assertThat(periodName.getId()).isNotNull();
		Optional<PeriodName> periodFound = periodNameRepository.findById(periodName.getId());
		assertThat(periodFound.isPresent()).isTrue();
	}

}
