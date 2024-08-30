package com.moriset.bcephal.grid.service.form;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;
import com.moriset.bcephal.utils.FunctionalityCodes;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelServiceIntegrationTests {

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
	private FormModelService formModelService;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	private String tenantId = "bcephal_test";
	
	private static FormModel formModel;
	
	private static FormModel formModelException;
	
	private static FormModel item;
	
	private static Locale local;
	
	private static String functionalityCode;
	
	@BeforeAll
    static void init(){
		
		local = Locale.getDefault(); 
		
		formModel = CreateFactory.buildFormModel();
    	assertThat(formModel).isNotNull();
    	assertThat(formModel.getId()).isNull();
    	
    	assertThat(item).isNull();
    	assertThat(functionalityCode).isNull();
    	
    	formModelException = CreateFactory.buildFormModel();
    	assertThat(formModelException).isNotNull();
    }
	
	@Test
    @DisplayName("Get functionality code.")
    @Order(1)
	public void getBrowserFunctionalityCodeTest() throws Exception {
    	assertThat(functionalityCode).isNull();
    	functionalityCode = formModelService.getBrowserFunctionalityCode();
	 
	    assertThat(functionalityCode).isNotNull();
	    assertThat(functionalityCode).isEqualTo(FunctionalityCodes.SOURCING_MODEL_FORM);
	}
	
	@Test
    @DisplayName("Get new item.")
    @Order(2)
	public void getNewItemTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(item).isNull();
    	item = formModelService.getNewItem();
	 
	    assertThat(item).isNotNull();
	    assertThat(item.getName().startsWith("Form Model ")).isTrue();
	}
	
	
	@Test
    @DisplayName("Save a new formModel.")
    @Order(3)
    @Commit
	public void saveFormModelTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModel).isNotNull();
    	formModelService.save(formModel, local);
	 
	    assertThat(formModel.getId()).isNotNull();
	    FormModel found = formModelService.getById(formModel.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getName()).isEqualTo(formModel.getName());
	}
	
	@Test
    @DisplayName("Throw exception wen a null object formModel saved.")
    @Order(4)
	public void saveFormModelIfNullObjectTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		
		assertThat(formModelException).isNotNull();
		formModelException.setName(null);
		assertThat(formModelException.getName()).isNull();
    	assertThrows(NoSuchMessageException.class, () -> {
    		formModelService.save(formModelException, local);
    	});
    	
    	formModelException = null;
    	assertThat(formModelException).isNull();
    	assertThrows(NoSuchMessageException.class, () -> {
    		formModelService.save(formModelException, local);
    	});
	}
    
    @Test
    @DisplayName("Update an existing formModel.")
    @Order(5)
    @Commit
	public void updateFormModelTest() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModel).isNotNull();
    	assertThat(formModel.getId()).isNotNull();
    	
    	formModel.setDescription("New description...");
    	formModelService.save(formModel, local);
    	
	    assertThat(formModel.getId()).isNotNull();
	    FormModel found = formModelService.getById(formModel.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getDescription()).isEqualTo(formModel.getDescription());
	}
    
    @Test
	@DisplayName("Find formModel by id")
	@Order(6)
	public void findFormModelById() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(formModel).isNotNull();
		assertThat(formModel.getId()).isNotNull();
		
		FormModel found = formModelService.getById(formModel.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(formModel.getName());
	}
    
    @Test
    @DisplayName("Delete formModel.")
    @Order(7)
    @Commit
	public void deleteFormModel() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(formModel).isNotNull();	 	
    	formModelService.delete(formModel);
	    FormModel found = formModelService.getById(formModel.getId());	 	 
	    assertThat(found == null).isTrue();
	}
}
