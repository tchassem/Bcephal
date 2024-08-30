package com.moriset.bcephal.grid.service.form;

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

import com.moriset.bcephal.grid.domain.form.FormModelSpot;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelSpotServiceIntegrationTests {

	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.sourcing.grid",
					"com.moriset.bcephal.multitenant.jpa",
					"com.moriset.bcephal.config"
					}, 
			exclude = {
					DataSourceAutoConfiguration.class, 
					HibernateJpaAutoConfiguration.class
					}
	)
	@EntityScan(basePackages = { 
			"com.moriset.bcephal.domain",
			"com.moriset.bcephal.grid.domain"
	})
	@ActiveProfiles(profiles = {"int", "servi"})
	static class ApplicationTests {}
	
	
	@Autowired
	private FormModelSpotService service;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	private static Locale local;
	
	private static FormModelSpot formModelSpot;
	
	private String tenantId = "bcephal_test";
	
	@BeforeAll
    static void init(){
		local = Locale.getDefault(); 
		formModelSpot = CreateFactory.buildFormModelSpot();
    	assertThat(formModelSpot).isNotNull();
    	assertThat(formModelSpot.getId()).isNull();
	}
	
	@Test
    @DisplayName("Save a new formModelSpot.")
    @Order(1)
    @Commit
	public void saveFormModelSpotTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModelSpot).isNotNull();
    	service.save(formModelSpot, local);
	 
	    assertThat(formModelSpot.getId()).isNotNull();
	    FormModelSpot found = service.getById(formModelSpot.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getFormula()).isEqualTo(formModelSpot.getFormula());
	}
	
	@Test
    @DisplayName("Update an existing formModelSpot.")
    @Order(2)
    @Commit
	public void updateFormModelSpotTest() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModelSpot).isNotNull();
    	assertThat(formModelSpot.getId()).isNotNull();
    	
    	formModelSpot.setFormula("AVERAGE");
    	service.save(formModelSpot, local);
    	
	    assertThat(formModelSpot.getId()).isNotNull();
	    FormModelSpot found = service.getById(formModelSpot.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getFormula()).isEqualTo(formModelSpot.getFormula());
	}
	
	@Test
    @DisplayName("Delete formModelSpot.")
    @Order(3)
    @Commit
	public void deleteFormModelSpot() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModelSpot).isNotNull();	 	
    	service.delete(formModelSpot);
	    FormModelSpot found = service.getById(formModelSpot.getId()); 	 
	    assertThat(found == null).isTrue();
	}
}
