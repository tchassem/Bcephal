package com.moriset.bcephal.grid.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.grid.domain.JoinLog;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;
import com.moriset.bcephal.utils.FunctionalityCodes;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "servi" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
public class JoinLogServiceIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.sourcing.grid", "com.moriset.bcephal.config" })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.dimension", "com.moriset.bcephal.domain",
			"com.moriset.bcephal.domain.filters", })
	@ActiveProfiles(profiles = { "int", "servi" })
	static class ApplicationTests {
	};

	private static Locale local;
	
	@Autowired
	private JoinLogService service;
	
	@Autowired
	private MultiTenantInterceptor interceptor;

	private static JoinLog item;
	
	private static JoinLog joinLog;
	
	private static JoinLog duplicateJoinLog;
	
	private static JoinLog joinLogException;
	
	private static String functionalityCode;

	private String tenantId = "bcephal_test";

	@BeforeAll
	static void init() {

		local = Locale.getDefault();

		joinLog = CreateFactory.buildJoinLog();
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getId()).isNull();
		
		duplicateJoinLog = CreateFactory.buildJoinLog();
    	assertThat(duplicateJoinLog).isNotNull();
    	assertThat(duplicateJoinLog.getId()).isNull();
    	
    	joinLogException = CreateFactory.buildJoinLog();
    	assertThat(joinLogException).isNotNull();
    	assertThat(joinLogException.getId()).isNull();
	}
	
	@Test
    @DisplayName("Get new joinLog.")
    @Order(1)
	public void getNewJoinLogTest() throws Exception {
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
		assertThat(functionalityCode).isEqualTo(FunctionalityCodes.REPORTING_REPORT_JOIN_GRID_SCHEDULER_LOG);
	}
	
	@Test
    @DisplayName("Save a new joinLog.")
    @Order(3)
    @Commit
	public void saveJoinLogTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(joinLog).isNotNull();
    	
    	service.save(joinLog, local);
	 
	    assertThat(joinLog.getId()).isNotNull();
	    JoinLog found = service.getById(joinLog.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getName()).isEqualTo(joinLog.getName());
	}
	
	@Test
	@DisplayName("Save update joinLog.")
	@Order(4)
	@Commit
	public void updateJoinLogTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getId()).isNotNull();

		joinLog.setName("Update name ..");
		joinLog.setDescription("New description...");
		service.save(joinLog, local);
		
		JoinLog found = service.getById(joinLog.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(joinLog.getName());
	}
	
	@Test
    @DisplayName("Save duplicate joinLog.")
    @Order(5)
    @Commit
	public void saveDuplicateJoinLogTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(duplicateJoinLog).isNotNull();
	 
    	duplicateJoinLog.setName("Update name ..");
    	assertThrows(Exception.class, () -> {
    		service.save(duplicateJoinLog, local);
    	});
	}
	
	@Test
    @DisplayName("Throw exception wen a null object joinLog saved.")
    @Order(6)
	public void saveJoinLogIfNullObjectTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(joinLogException).isNotNull();
    	
    	joinLogException = null;
    	assertThat(joinLogException).isNull();
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(joinLogException, local);
    	});
	}
	
	@Test
	@DisplayName("Find joinLog by id")
	@Order(7)
	public void findJoinLogById() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getId()).isNotNull();
		
		JoinLog found = service.getById(joinLog.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getId()).isEqualTo(joinLog.getId());
	}
	
	@Test
	@DisplayName("Get joinLog by name")
	@Order(8)
	public void getJoinLogByName() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getName()).isNotNull();
		
		JoinLog found = service.getByName(joinLog.getName());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(joinLog.getName());
	}
	
	@Test
	@DisplayName("Get joinLog by names")
	@Order(9)
	public void getJoinLogByNames() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(joinLog).isNotNull();
		assertThat(joinLog.getName()).isNotNull();
		
		List<JoinLog> result = service.getAllByName(joinLog.getName());
		assertThat(result.size() == 1).isTrue();
		assertThat(result.get(0).getName()).isEqualTo(joinLog.getName());
	}
	
	@Test
	@DisplayName("Delete joinLog.")
	@Order(10)
	@Commit
	public void deleteJoinLogTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(joinLog).isNotNull();
		service.delete(joinLog);
		JoinLog found = service.getById(joinLog.getId());
		assertThat(found == null).isTrue();
	}
}
