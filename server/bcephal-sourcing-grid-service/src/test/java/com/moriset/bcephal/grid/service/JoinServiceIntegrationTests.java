package com.moriset.bcephal.grid.service;

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
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;
import com.moriset.bcephal.utils.FunctionalityCodes;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "servi" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
public class JoinServiceIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.sourcing.grid", "com.moriset.bcephal.config" })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.dimension", "com.moriset.bcephal.domain",
			"com.moriset.bcephal.domain.filters", })
	@ActiveProfiles(profiles = { "int", "servi" })
	static class ApplicationTests {
	};

	private static Locale local;
	
	@Autowired
	private JoinService service;
	
	@Autowired
	private MultiTenantInterceptor interceptor;

	private static Join join;
	
	private static Join item;
	
	private static Join duplicateJoin;
	
	private static Join joinException;
	
	private static String functionalityCode;

	private String tenantId = "bcephal_test";

	@BeforeAll
	static void init() {

		local = Locale.getDefault();

		join = CreateFactory.buildJoin();
		assertThat(join).isNotNull();
		assertThat(join.getId()).isNull();
		
		duplicateJoin = CreateFactory.buildJoin();
    	assertThat(duplicateJoin).isNotNull();
    	assertThat(duplicateJoin.getId()).isNull();
    	
    	joinException = CreateFactory.buildJoin();
    	assertThat(joinException).isNotNull();
    	assertThat(joinException.getId()).isNull();
	}
	
	@Test
    @DisplayName("Get new join.")
    @Order(1)
	public void getNewJoinTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(item).isNull();
		item = service.getNewItem();
	 
	    assertThat(item).isNotNull();
	    assertThat(item.getName().startsWith("Join ")).isTrue();
	}
	
	@Test
	@DisplayName("Get functionality code.")
	@Order(2)
	public void getBrowserFunctionalityCodeTest() throws Exception {
		assertThat(functionalityCode).isNull();
		functionalityCode = service.getBrowserFunctionalityCode();

		assertThat(functionalityCode).isNotNull();
		assertThat(functionalityCode).isEqualTo(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID);
	}
	
	@Test
    @DisplayName("Save a new join.")
    @Order(3)
    @Commit
	public void saveJoinTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(join).isNotNull();
    	
    	service.save(join, local);
	 
	    assertThat(join.getId()).isNotNull();
	    Join found = service.getById(join.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getName()).isEqualTo(join.getName());
	}
	
	@Test
	@DisplayName("Save update join.")
	@Order(4)
	@Commit
	public void updateJoinTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(join).isNotNull();
		assertThat(join.getId()).isNotNull();

		join.setName("Update name ..");
		join.setDescription("New description...");
		service.save(join, local);
		Join found = service.getById(join.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(join.getName());
	}

	
	@Test
    @DisplayName("Save duplicate join.")
    @Order(5)
    @Commit
	public void saveDuplicateJoinTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(duplicateJoin).isNotNull();
	 
    	duplicateJoin.setName("Update name ..");
    	assertThrows(Exception.class, () -> {
    		service.save(duplicateJoin, local);
    	});
	}

	@Test
    @DisplayName("Throw exception wen a null object join saved.")
    @Order(6)
	public void saveJoinIfNullObjectTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(joinException).isNotNull();
		
		joinException.setName("");
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(joinException, local);
    	});
    	
    	joinException = null;
    	assertThat(joinException).isNull();
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(joinException, local);
    	});
	}
	
	@Test
	@DisplayName("Find join by id")
	@Order(7)
	public void findJoinById() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(join).isNotNull();
		assertThat(join.getId()).isNotNull();
		
		Join found = service.getById(join.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(join.getName());
	}
	
	@Test
    @DisplayName("Delete join.")
    @Order(8)
    @Commit
	public void deleteJoinTest() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(join).isNotNull();	 	
    	service.delete(join);
	    Join found = service.getById(join.getId());	 	 
	    assertThat(found == null).isTrue();
	}
}
