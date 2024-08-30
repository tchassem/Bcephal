

package com.moriset.bcephal.reporting.service;

import java.util.List;
import java.util.Locale;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.reporting.ReportGridFactory;
import com.moriset.bcephal.utils.BCephalParameterTest;

/**
 * @author Emmanuel Emmeni
 *
 */
@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReportGridServiceIntegrationTests {
	
	@SpringBootApplication(
		scanBasePackages = {
				"com.moriset.bcephal.reporting", "com.moriset.bcephal.grid", "com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config", 
				"com.moriset.bcephal.join.service", "com.moriset.bcephal", "com.moriset.bcephal.planification"
		},
		exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }
	)
	@EntityScan(
		basePackages = { 
			"com.moriset.bcephal.domain", "com.moriset.bcephal.grid.domain", "com.moriset.bcephal.sheet.domain",
			"com.moriset.bcephal.planification.domain.routine", "com.moriset.bcephal.security.domain"
		}
	)
	@ActiveProfiles(profiles = {"int", "serv"})
	static class ApplicationTests {}
	
	private static Locale local;
	
	@Autowired
    private ReportGridService reportGridService;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	//@Value("${bcephal.report.tenant-id}")
	//private String reportTenantId;
	
	private static Grille grid;
    
    @BeforeAll
    static void init() {
    	grid = ReportGridFactory.buildGrille();
    	Assertions.assertThat(grid).isNotNull();
    	Assertions.assertThat(grid.getId()).isNull();
	}
    
    @Test
    @DisplayName("Validate report grid bean.")
    @Order(1)
	public void validateTest() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		/*
		 * Grille grille =
		 * ReportGridFactory.buildCHK100BillingEventAmountDetailsPerClient();
		 * grille.setName(null);
		 * org.junit.jupiter.api.Assertions.assertThrows(NoSuchMessageException.class,
		 * () -> { reportGridService.save(grille, local); });
		 */
	}
    
    @Test
    @DisplayName("Save a new report grid")
    @Order(2)
    @Commit
	public void saveReportGridTest() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	Assertions.assertThat(grid).isNotNull();
    	reportGridService.save(grid, local);
	 
	    Assertions.assertThat(grid.getId()).isNotNull();
	    Grille grille = reportGridService.getById(grid.getId());
	    Assertions.assertThat(grille).isNotNull();
	    Assertions.assertThat(grille).isEqualTo(grid);
	}
    
    @Test
    @DisplayName("Set an existing report grid editable")
    @Order(3)
    @Commit
	public void setEditableReportGridTest() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	Assertions.assertThat(grid).isNotNull();
    	boolean res, editable = true;
    	res = reportGridService.setEditable(grid.getId(), editable, local);
	    Assertions.assertThat(res).isTrue();
	    Grille grille = reportGridService.getById(grid.getId());
	    Assertions.assertThat(grille.isEditable()).isEqualTo(editable);
	}
    
    @Test
    @DisplayName("Update an existing report grid")
    @Order(4)
    @Commit
	public void updateReportGridTest() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	Assertions.assertThat(grid).isNotNull();
    	Assertions.assertThat(grid.getId()).isNotNull();
    	grid.setDescription("New description...");
    	reportGridService.save(grid, local);
	    Assertions.assertThat(grid.getId()).isNotNull();
	    Grille grille = reportGridService.getById(grid.getId());
	    Assertions.assertThat(grille.getDescription()).isEqualTo(grid.getDescription());
	}
    
    @Test
    @DisplayName("Find report grid by name")
    @Order(5)
	public void findReportGridByName() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	Assertions.assertThat(grid).isNotNull();
	    List<Grille> found = reportGridService.getAllReportByName(grid.getName());
	    Assertions.assertThat(found.isEmpty()).isFalse();
	    Assertions.assertThat(found.size()).isEqualTo(1);
	}
    
    @Test
    @DisplayName("Change report grid status")
    @Order(6)
    @Commit
	public void changeReportGridStatus() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	Assertions.assertThat(grid).isNotNull();
    	boolean bool = grid.getStatus().isLoaded();
        GrilleStatus status = bool ? GrilleStatus.LOADED : GrilleStatus.UNLOADED;
        grid.setStatus(status);
       // reportGridService.save(grid, local);
        Long id = reportGridService.getByName(grid.getName()).getId();
        Grille grille = reportGridService.getById(id);
	    Assertions.assertThat(grid.getStatus()).isEqualTo(grille.getStatus());
	}
    
    @Test
    @DisplayName("Create report grid from existing report")
    @Order(7)
    @Commit
	public void createReportGrid() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	Assertions.assertThat(grid).isNotNull();
    	Assertions.assertThat(grid.getId()).isNotNull();
        GrilleStatus status = grid.getStatus().isLoaded() ? GrilleStatus.UNLOADED : GrilleStatus.LOADED;
        grid.setStatus(status);
        Long id = reportGridService.createReport(grid.getId(), grid.getName() + " " + System.currentTimeMillis(), local);
	    Assertions.assertThat(id).isNotNull();
	    Assertions.assertThat(id).isNotEqualTo(grid.getId());
	    Grille grille = reportGridService.getById(id);
    	reportGridService.delete(grille);
	}
    @Disabled
    @Test
    @DisplayName("Delete report grid.")
    @Order(8)
    @Commit
	public void deleteReportGridById() throws Exception {
    	interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	Assertions.assertThat(grid).isNotNull();
    	reportGridService.delete(grid);
    	//Long id = reportGridService.getByName(grid.getName()).getId();
	    Grille found = reportGridService.getById(grid.getId());
	    Assertions.assertThat(found).isNull();
	}

}
