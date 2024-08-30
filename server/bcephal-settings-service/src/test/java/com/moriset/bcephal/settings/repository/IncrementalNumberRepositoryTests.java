package com.moriset.bcephal.settings.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IncrementalNumberRepositoryTests {

		@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.settings", "com.moriset.bcephal.multitenant.jpa",
													"com.moriset.bcephal.config" })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.setting.domain",  })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests { }



	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;

    private static IncrementalNumber incrementalNumber;

	@BeforeAll
	static void init() {
	
		incrementalNumber = IncrementalNumber.builder().name("incrementalTest")
				.currentValue(2L)
				.incrementValue(1L)
				.initialValue(0L)
				.build();
	Assertions.assertThat(incrementalNumber).isNotNull();
	Assertions.assertThat(incrementalNumber.getId()).isNull();
	}
 
//	@Test
//	@DisplayName("save a new incremental number")
//	@Order(1)
//	@Commit
//	void saveIncrmentalNbrTest() {
//		incrementalNumberRepository.save(incrementalNumber);
//		assertThat(incrementalNumber.getId()).isNotNull();
//		Optional<IncrementalNumber> incrementalNbrFound = incrementalNumberRepository.findById(incrementalNumber.getId());
//		assertThat(incrementalNbrFound.isPresent()).isTrue();
//		
//	}


}
