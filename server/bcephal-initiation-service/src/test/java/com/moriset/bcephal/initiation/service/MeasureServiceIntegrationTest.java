package com.moriset.bcephal.initiation.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Measure;
import com.moriset.bcephal.initiation.domain.api.MeasureApi;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "serv" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MeasureServiceIntegrationTest {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
			"com.moriset.bcephal.config" }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
	@ActiveProfiles(profiles = { "int", "serv" })
	static class ApplicationTests {
	}

	@Autowired
	MultiTenantInterceptor Interceptor;
	private String EntityTenantId = "bcephal_test";

	@Autowired
	MeasureService measureService;

	private static Measure measure;

	private static Measure measure2;

	@BeforeAll
	static void init() {

		measure = Measure.builder().name("MeasureTest15")
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
	}

	@Test
	@DisplayName("create a new measure")
	@Order(1)
	//@Commit
	void createNewMeasure() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		MeasureApi measureApi = new MeasureApi();
		measureApi.setName("Measure API");
		measure2 = measureService.createMeasure(measureApi, Locale.FRENCH);		
		assertThat(measure2).isNotNull();
		assertThat(measure2.isPersistent()).isTrue();
		assertThat(measureApi.getName()).isEqualTo(measure2.getName());
	}

	@Test
	@DisplayName("save a new measure")
	@Order(2)
	//@Commit
	void saveNewMeasure() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(measure).isNotNull();

		measure = measureService.save(measure, Locale.getDefault());
		Measure found = measureService.getById(measure.getId());
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(measure.getId());
		assertThat(measure.isPersistent()).isTrue();
	}

	@Test
	@DisplayName("delete measure")
	@Order(10)
	//@Commit
	void deleteMeasure() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(measure).isNotNull();

		measureService.deleteMeasure(measure, Locale.getDefault());
		Measure found = measureService.getById(measure.getId());
		assertThat(found).isNull();
		
		assertThat(measure2).isNotNull();
		measureService.deleteMeasure(measure2, Locale.getDefault());
	}
}
