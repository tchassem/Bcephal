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

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;
import com.moriset.bcephal.utils.FunctionalityCodes;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "servi" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
public class MaterializedGridServiceIntegrationTest {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.sourcing.grid", "com.moriset.bcephal.config" })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.dimension", "com.moriset.bcephal.domain",
			"com.moriset.bcephal.domain.filters", })
	@ActiveProfiles(profiles = { "int", "servi" })
	static class ApplicationTests {
	};

	@Autowired
	private MaterializedGridService service;

	private static Locale local;
	
	@Autowired
	private GrilleService grilleService;

	@Autowired
	private MultiTenantInterceptor interceptor;

	private static MaterializedGrid matGrille;
	
	private static MaterializedGrid matGrilleException;
	
	private static Grille grille;
	
	private static Long reportId;

	private static String functionalityCode;

	private String tenantId = "bcephal_test";

	@BeforeAll
	static void init() {

		local = Locale.getDefault();

		matGrille = CreateFactory.buildMaterialize();
		assertThat(matGrille).isNotNull();
		assertThat(matGrille.getId()).isNull();
		
		matGrilleException = CreateFactory.buildMaterialize();
    	assertThat(matGrilleException).isNotNull();
    	assertThat(matGrilleException.getId()).isNull();
	}

	@Test
	@DisplayName("Get functionality code.")
	@Order(1)
	public void getBrowserFunctionalityCodeTest() throws Exception {
		assertThat(functionalityCode).isNull();
		functionalityCode = service.getBrowserFunctionalityCode();

		assertThat(functionalityCode).isNotNull();
		assertThat(functionalityCode).isEqualTo(FunctionalityCodes.SOURCING_MATERIALIZED_GRID);
	}
	
	@Test
    @DisplayName("Throw exception wen a null object matGrille saved.")
    @Order(2)
	public void saveMatGrilleIfNullObjectTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrilleException).isNotNull();
		
		matGrilleException.setName("");
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(matGrilleException, local);
    	});
    	
    	matGrilleException = null;
    	assertThat(matGrilleException).isNull();
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.save(matGrilleException, local);
    	});
	}

	@Test
	@DisplayName("Save a new matGrille.")
	@Order(3)
	@Commit
	public void createReportGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille).isNotNull();

		service.save(matGrille, local);

		assertThat(matGrille.getId()).isNotNull();
		MaterializedGrid found = service.getById(matGrille.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(matGrille.getName());
	}

	@Test
	@DisplayName("Create a reportGrille.")
	@Order(4)
	@Commit
	public void saveMaterializeGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille.getId()).isNotNull();
		assertThat(reportId).isNull();
		assertThat(grille).isNull();

		reportId = service.createReport(matGrille.getId(), "Report grille name", local);

		assertThat(reportId).isNotNull();
		Grille found = grilleService.getById(reportId);
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo("Report grille name");
	}
	
	@Test
    @DisplayName("Save duplicate reportgrille.")
    @Order(5)
    @Commit
	public void saveDuplicateReportGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
    	assertThat(reportId).isNotNull();
	 
    	assertThrows(NoSuchMessageException.class, () -> {
    		service.createReport(matGrille.getId(), "Report grille name", local);
    	});
	}

	@Test
	@DisplayName("Save update matGrille.")
	@Order(6)
	@Commit
	public void updateMaterializeGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille).isNotNull();
		assertThat(matGrille.getId()).isNotNull();

		matGrille.setName("Update name ..");
		service.save(matGrille, local);
		MaterializedGrid found = service.getById(matGrille.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(matGrille.getName());
	}

	@Test
	@DisplayName("Save publish matGrille.")
	@Order(7)
	@Commit
	public void publishMaterializeGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille).isNotNull();
		assertThat(matGrille.getId()).isNotNull();

		service.publish(matGrille, local);
		MaterializedGrid found = service.getById(matGrille.getId());
		assertThat(found.isPublished()).isTrue();
		assertThat(found.getName()).isEqualTo(matGrille.getName());
	}
	
	@Test
	@DisplayName("Find grille by id")
	@Order(8)
	public void findMatGrilleById() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille).isNotNull();
		assertThat(matGrille.getId()).isNotNull();
		
		MaterializedGrid found = service.getById(matGrille.getId());
		assertThat(found != null).isTrue();
		assertThat(found.getId()).isEqualTo(matGrille.getId());
	}
	
	@Test
	@DisplayName("Get grille by name")
	@Order(9)
	public void getMatGrilleByName() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille).isNotNull();
		assertThat(matGrille.getName()).isNotNull();
		
		MaterializedGrid found = service.getByName(matGrille.getName());
		assertThat(found != null).isTrue();
		assertThat(found.getName()).isEqualTo(matGrille.getName());
	}
	
	@Test
	@DisplayName("Get grille by names")
	@Order(10)
	public void getMatGrilleByNames() throws Exception {
    	interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille).isNotNull();
		assertThat(matGrille.getName()).isNotNull();
		
		List<MaterializedGrid> result = service.getAllByName(matGrille.getName());
		assertThat(result.size() == 1).isTrue();
		assertThat(result.get(0).getName()).isEqualTo(matGrille.getName());
	}

	@Test
	@DisplayName("Delete matGrille.")
	@Order(11)
	@Commit
	public void deleteMaterializeGrille() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(matGrille).isNotNull();
		service.delete(matGrille);
		MaterializedGrid found = service.getById(matGrille.getId());
		assertThat(found == null).isTrue();
	}
	
	@Test
	@DisplayName("Delete report grille.")
	@Order(12)
	@Commit
	public void deleteReportGrilleById() throws Exception {
		interceptor.setTenantForServiceTest(tenantId);
		assertThat(reportId).isNotNull();
		Grille found = grilleService.getById(reportId);
		if(found != null) {
			grilleService.delete(found);
			reportId = null;
		}
		assertThat(reportId == null).isTrue();
	}
}
