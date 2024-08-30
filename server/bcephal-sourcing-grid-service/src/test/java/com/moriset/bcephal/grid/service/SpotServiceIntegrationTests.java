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

import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;
import com.moriset.bcephal.utils.FunctionalityCodes;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpotServiceIntegrationTests {

	@SpringBootApplication(
			scanBasePackages = {
					"com.moriset.bcephal.sourcing.grid",
					"com.moriset.bcephal.config"
			})
	@EntityScan(
			basePackages = { 
					"com.moriset.bcephal.domain.dimension",
					"com.moriset.bcephal.domain",
					"com.moriset.bcephal.domain.filters",
					})
	@ActiveProfiles(profiles = {"int", "servi"})
	static class ApplicationTests {};
	
	@Autowired
	private SpotService service;
	
	private static Locale local;
	
	@Autowired
	private MultiTenantInterceptor interceptor;
	
	private static Spot spot;
	
	private static Spot item;
	
	private static Spot spotException;
	
	private static String functionalityCode;
	
	private String tenantId = "bcephal_test";
	
	@BeforeAll
    static void init(){
		
		local = Locale.getDefault(); 
		
		spot = CreateFactory.buildSpot();
    	assertThat(spot).isNotNull();
    	assertThat(spot.getId()).isNull();
    	
    	spotException = CreateFactory.buildSpot();
    	assertThat(spotException).isNotNull();
    	assertThat(spotException.getId()).isNull();
    }
	
	@Test
    @DisplayName("Get functionality code.")
    @Order(1)
	public void getBrowserFunctionalityCodeTest() throws Exception {
    	assertThat(functionalityCode).isNull();
    	functionalityCode = service.getBrowserFunctionalityCode();
	 
	    assertThat(functionalityCode).isNotNull();
	    assertThat(functionalityCode).isEqualTo(FunctionalityCodes.SOURCING_SPOT);
	}
	
	@Test
    @DisplayName("Get new item.")
    @Order(2)
	public void getNewItemTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(item).isNull();
		item = service.getNewItem();
	 
	    assertThat(item).isNotNull();
	    assertThat(item.getName().startsWith("Spot ")).isTrue();
	}
	
	@Test
    @DisplayName("Save a new spot.")
    @Order(3)
    @Commit
	public void saveSpotTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(spot).isNotNull();
    	
    	service.save(spot, local);
	 
	    assertThat(spot.getId()).isNotNull();
	    Spot found = service.getById(spot.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getName()).isEqualTo(spot.getName());
	}
	
	@Test
    @DisplayName("Update an existing spot.")
    @Order(4)
    @Commit
	public void updateSpotTest() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(spot).isNotNull();
    	assertThat(spot.getId()).isNotNull();
    	
    	spot.setDescription("New description...");
    	service.save(spot, local);
    	
	    assertThat(spot.getId()).isNotNull();
	    Spot found = service.getById(spot.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getDescription()).isEqualTo(spot.getDescription());
	}

	@Test
    @DisplayName("Throw exception wen a null object spot saved.")
    @Order(5)
	public void saveSpotIfNullObjectTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(spotException).isNotNull();
		
		spotException.setName("");
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(spotException, local);
    	});
    	
    	spotException = null;
    	assertThat(spotException).isNull();
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(spotException, local);
    	});
	}
	
	@Test
	@DisplayName("Find spot by id")
	@Order(6)
	public void findSpotById() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNotNull();
		
		Spot found = service.getById(spot.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(spot.getName());
	}
	
	@Test
    @DisplayName("Delete spot.")
    @Order(7)
    @Commit
	public void deleteSpot() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(spot).isNotNull();	 	
    	service.delete(spot);
	    Spot found = service.getById(spot.getId());	 	 
	    assertThat(found == null).isTrue();
	}
}
