package com.moriset.bcephal.settings.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "serv" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IncrementalNumberServiceIntegration {
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.settings", "com.moriset.bcephal.multitenant.jpa",
			"com.moriset.bcephal.config" }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.settings.domain" })
	@ActiveProfiles(profiles = { "int", "serv" })
	static class ApplicationTests {
	}

	@Autowired
	MultiTenantInterceptor Interceptor;
	private String EntityTenantId = "bcephal_test";

	@Autowired
	IncrementalNumberService incrementalNumberService;
	private static IncrementalNumber incrementalNumber;

	@BeforeAll
	static void init() {
		incrementalNumber = IncrementalNumber.builder().name("IncrementalNumberTest")
				.creationDate(new Timestamp(System.currentTimeMillis()))
				.modificationDate(new Timestamp(System.currentTimeMillis()))
				.build();
	}

	@Test
	@DisplayName("Save a new Label")
	@Order(1)
	@Commit
	void saveNewLabel() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(incrementalNumber).isNotNull();

		incrementalNumberService.save(incrementalNumber, Locale.getDefault());
		assertThat(incrementalNumber.getId()).isNotNull();
		IncrementalNumber found = incrementalNumberService.getById(incrementalNumber.getId());
		assertThat(incrementalNumber.getName()).isEqualTo(found.getName());

	}

}
