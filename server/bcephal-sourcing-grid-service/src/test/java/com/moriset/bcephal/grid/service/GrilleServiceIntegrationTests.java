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

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "servi"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
public class GrilleServiceIntegrationTests {

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
	private GrilleService service;
	
	private static Locale local;
	
	@Autowired
	private MultiTenantInterceptor interceptor;
	
	private static Grille grille;
	
	private static Grille item;
	
	private static Grille duplicateGrille;
	
	private static Grille grilleException;
	
	private String tenantId = "bcephal_test";
	
	@BeforeAll
    static void init(){
		
		local = Locale.getDefault(); 
		
		grille = CreateFactory.buildGrille();
    	assertThat(grille).isNotNull();
    	assertThat(grille.getId()).isNull();
    	
    	duplicateGrille = CreateFactory.buildGrille();
    	assertThat(duplicateGrille).isNotNull();
    	assertThat(duplicateGrille.getId()).isNull();
    	
    	grilleException = CreateFactory.buildGrille();
    	assertThat(grilleException).isNotNull();
    	assertThat(grilleException.getId()).isNull();
    }
	
	@Test
    @DisplayName("Get new grille.")
    @Order(1)
	public void getNewGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(item).isNull();
		item = service.getNewItem("Input grille ", true);
	 
	    assertThat(item).isNotNull();
	    assertThat(item.getName().startsWith("Input grille ")).isTrue();
	}
	
	@Test
    @DisplayName("Save a new grille.")
    @Order(2)
    @Commit
	public void saveGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(grille).isNotNull();
    	
    	service.save(grille, local);
	 
	    assertThat(grille.getId()).isNotNull();
	    Grille found = service.getById(grille.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getName()).isEqualTo(grille.getName());
	}
	

//	@Disabled
//	@Test
//    @DisplayName("Save duplicate grille.")
//    @Order(3)
//    @Commit
//	public void saveDuplicateGrilleTest() throws Exception {
//		interceptor.setTenantForServiceTest(tenantId);
//    	assertThat(duplicateGrille).isNotNull();
//    	assertThat(grille.getId()).isNotNull();
//	 
//    	assertThrows(NoSuchMessageException.class, () -> {
//    		service.save(duplicateGrille, local);
//    	});
//	}
	
	@Test
    @DisplayName("Update an existing grille.")
    @Order(3)
    @Commit
	public void updateGrilleTest() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(grille).isNotNull();
    	assertThat(grille.getId()).isNotNull();
    	
    	grille.setDescription("New description...");
    	service.save(grille, local);
    	
	    assertThat(grille.getId()).isNotNull();
	    Grille found = service.getById(grille.getId());	 
	    assertThat(found != null).isTrue();
	    assertThat(found.getDescription()).isEqualTo(grille.getDescription());
	}

	@Test
    @DisplayName("Throw exception wen a null object grille saved.")
    @Order(4)
	public void saveGrilleIfNullObjectTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(grilleException).isNotNull();
		
		grilleException.setName("");
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(grilleException, local);
    	});
    	
    	grilleException = null;
    	assertThat(grilleException).isNull();
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(grilleException, local);
    	});
	}
	
	@Test
	@DisplayName("Find grille by id")
	@Order(5)
	public void findGrilleById() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		Grille found = service.getById(grille.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(grille.getName());
	}
	
	@Test
    @DisplayName("Delete grille.")
    @Order(6)
    @Commit
	public void deleteGrille() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
    	assertThat(grille).isNotNull();	 	
    	service.delete(grille);
	    Grille found = service.getById(grille.getId());	 	 
	    assertThat(found == null).isTrue();
	}
}
