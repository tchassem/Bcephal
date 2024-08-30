package com.moriset.bcephal.grid.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.Variable;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;
import com.moriset.bcephal.utils.FunctionalityCodes;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "servi" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
public class VariableServiceIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.sourcing.grid", "com.moriset.bcephal.config" })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.dimension", "com.moriset.bcephal.domain",
			"com.moriset.bcephal.domain.filters", })
	@ActiveProfiles(profiles = { "int", "servi" })
	static class ApplicationTests {
	};

	private static Locale local;
	
	@Autowired
	private VariableService service;
	
	@Autowired
	private MultiTenantInterceptor interceptor;
	
	private static Variable item;

	private static Variable variable;
	
	private static Variable variableException;
	
	private static Variable duplicateVariable;
	
	private static String functionalityCode;

	private String tenantId = "bcephal_test";

	@BeforeAll
	static void init() {

		local = Locale.getDefault();

		variable = CreateFactory.buildVariable();
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNull();
		
		duplicateVariable = CreateFactory.buildVariable();
    	assertThat(duplicateVariable).isNotNull();
    	assertThat(duplicateVariable.getId()).isNull();
    	
    	variableException = CreateFactory.buildVariable();
    	assertThat(variableException).isNotNull();
    	assertThat(variableException.getId()).isNull();
	}
	
	@Test
    @DisplayName("Get new variable.")
    @Order(1)
	public void getNewVariableTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(item).isNull();
		item = service.getNewItem();
	 
	    assertThat(item).isNotNull();
	    assertThat(item.getName().startsWith("Group ")).isTrue();
	}
	
	@Test
	@DisplayName("Get functionality code.")
	@Order(2)
	public void getBrowserFunctionalityCodeTest() throws Exception {
		assertThat(functionalityCode).isNull();
		functionalityCode = service.getBrowserFunctionalityCode();

		assertThat(functionalityCode).isNotNull();
		assertThat(functionalityCode).isEqualTo(FunctionalityCodes.VARIABLE);
	}
	
	@Test
    @DisplayName("Save a new variable.")
    @Order(3)
    @Commit
	public void saveVariableTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(variable).isNotNull();
    	
    	service.save(variable, local);
	 
	    assertThat(variable.getId()).isNotNull();
	    Variable found = service.getById(variable.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getName()).isEqualTo(variable.getName());
	}
	
	@Test
	@DisplayName("Save update variable.")
	@Order(4)
	@Commit
	public void updateVariableTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNotNull();

		variable.setName("Update name ..");
		variable.setDescription("New description...");
		service.save(variable, local);
		Variable found = service.getById(variable.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(variable.getName());
	}
	
	@Test
    @DisplayName("Return existing variable after saved.")
    @Order(5)
	public void returnExistingVariableAfterSaveTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(variableException).isNotNull();
		assertThat(variableException.getId()).isNull();
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNotNull();
		
		variableException.setName("Update name ..");
		variableException = service.save(variableException, local);
    	
		assertThat(variableException.getId()).isNotNull();
		assertThat(variableException).isEqualTo(variable);
	}
	
	@Test
	@DisplayName("Find variable by id")
	@Order(6)
	public void findVariableById() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNotNull();
		
		Variable found = service.getById(variable.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(variable.getName());
	}
	
	@Test
	@DisplayName("Get variable by name")
	@Order(7)
	public void getVariableByName() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(variable).isNotNull();
		assertThat(variable.getName()).isNotNull();
		
		Variable found = service.getByName(variable.getName());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(variable.getName());
	}
	
	@Test
	@DisplayName("Get variable by names")
	@Order(8)
	public void getVariableByNames() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(variable).isNotNull();
		assertThat(variable.getName()).isNotNull();
		
		List<Variable> result = service.getAllByName(variable.getName());
		assertThat(result.size() == 1).isTrue();
		assertThat(result.get(0).getName()).isEqualTo(variable.getName());
	}
	
	@Test
    @DisplayName("Delete variable.")
    @Order(9)
    @Commit
	public void deleteVariableTest() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(variable).isNotNull();	 	
    	service.delete(variable);
	    Variable found = service.getById(variable.getId());	 	 
	    assertThat(found == null).isTrue();
	}
}
