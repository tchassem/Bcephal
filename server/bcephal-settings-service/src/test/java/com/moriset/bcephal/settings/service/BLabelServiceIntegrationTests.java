package com.moriset.bcephal.settings.service;

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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.BLabel;
import com.moriset.bcephal.domain.BLabels;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "serv" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BLabelServiceIntegrationTests {

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

	private static BLabel bLabel;
	private static BLabels bLabels;
	@Autowired
	BLabelService bLabelService;

	@BeforeAll
	static void init() {
		bLabel = BLabel.builder().value("LabelTest").build();
		bLabels = BLabels.builder().lang("Francais").labelItemListChangeHandler(new ListChangeHandler<>()).build();

	}

	@Test
	@DisplayName("Save a new Label")
	@Order(1)
	@Commit
	void saveNewLabel() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(bLabel).isNotNull();
		
		bLabelService.save(bLabel, Locale.getDefault());
		assertThat(bLabel.getId()).isNotNull();
		BLabel found = bLabelService.getById(bLabel.getId());
		assertThat(bLabel.getValue()).isEqualTo(found.getValue());
		
	}

	
	//@Test
	@DisplayName("Save Labels")
	@Order(2)
	@Commit
	void saveLabels() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(bLabel).isNotNull();
		
		bLabelService.save(bLabels, Locale.getDefault());
		assertThat(bLabels.getId()).isNotNull();
		BLabels found = bLabelService.getLabels(bLabels.getLang(), Locale.getDefault());
		assertThat(found).isNotNull();
		
	}
	
	@Test
	@DisplayName("delete Label")
	@Order(10)
	@Commit
	void deleteLabel() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(bLabel).isNotNull();
		
		bLabelService.delete(bLabel);
		assertThat(bLabel.getId()).isNotNull();
		BLabel found = bLabelService.getById(bLabel.getId());
		assertThat(found).isNull();
		
	}
}
