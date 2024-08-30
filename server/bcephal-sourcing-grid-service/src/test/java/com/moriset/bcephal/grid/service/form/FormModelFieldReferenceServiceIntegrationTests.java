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

import com.moriset.bcephal.grid.domain.form.FormModelFieldReference;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelFieldReferenceServiceIntegrationTests {

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
	private FormModelFieldReferenceService service;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	private String tenantId = "bcephal_test";
	
	private static FormModelFieldReference formModelFieldReference;

	private static Locale local;
	
	@BeforeAll
    static void init(){
		local = Locale.getDefault(); 
		formModelFieldReference = CreateFactory.buildFormModelFieldReference();
    	assertThat(formModelFieldReference).isNotNull();
    	assertThat(formModelFieldReference.getId()).isNull();
	}
	
	@Test
    @DisplayName("Save a new formModelFieldReference.")
    @Order(1)
    @Commit
	public void saveFormModelFieldReferenceTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModelFieldReference).isNotNull();
    	service.save(formModelFieldReference, local);
	 
	    assertThat(formModelFieldReference.getId()).isNotNull();
	    FormModelFieldReference found = service.getById(formModelFieldReference.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getFormula()).isEqualTo(formModelFieldReference.getFormula());
	}
	
	@Test
    @DisplayName("Update an existing formModelFieldReference.")
    @Order(2)
    @Commit
	public void updateFormModelSpotTest() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModelFieldReference).isNotNull();
    	assertThat(formModelFieldReference.getId()).isNotNull();
    	
    	formModelFieldReference.setFormula("AVERAGE");
    	service.save(formModelFieldReference, local);
    	
	    assertThat(formModelFieldReference.getId()).isNotNull();
	    FormModelFieldReference found = service.getById(formModelFieldReference.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getFormula()).isEqualTo(formModelFieldReference.getFormula());
	}
	
	@Test
    @DisplayName("Delete formModelFieldReference.")
    @Order(3)
    @Commit
	public void deleteFormModelFieldReference() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModelFieldReference).isNotNull();	 	
    	service.delete(formModelFieldReference);
	    FormModelFieldReference found = service.getById(formModelFieldReference.getId()); 	 
	    assertThat(found == null).isTrue();
	}
}
