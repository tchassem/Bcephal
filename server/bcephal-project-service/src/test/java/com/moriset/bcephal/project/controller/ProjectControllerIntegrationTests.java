package com.moriset.bcephal.project.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import com.moriset.bcephal.project.ProjectFactory;
import com.moriset.bcephal.project.config.TestSecurityConfig;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.utils.BCephalParameterTest;
import com.moriset.bcephal.utils.RequestParams;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestSecurityConfig.class)
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectControllerIntegrationTests {

	@LocalServerPort
	private int port;
	
//	@Autowired
//	private 
	
	private String baseUrl;
	
	RestClient restClient;

	private static Project project;
    
    @BeforeAll
    static void init(){
    	project = new ProjectFactory().buildProject();
    	project.setName(BCephalParameterTest.PROJECT_CODE);
    	project.setCode(BCephalParameterTest.PROJECT_CODE);
    	project.setAllowCodeBuilder(false);
    	Assertions.assertThat(project).isNotNull();
    	Assertions.assertThat(project.getId()).isNull();
    }
		
	@BeforeEach
	public void setUp() {
		baseUrl = "http://localhost:".concat("" + port).concat("/projects");
		restClient = RestClient.builder()
				.baseUrl(baseUrl)
				.build();
	}
	
	@Test
	@DisplayName("Delete project if exist")
    @Order(1)
	public void deleteProjectIfExistTest() {
		ResponseEntity<Boolean> response = restClient.post()
			    .uri(uriBuilder -> uriBuilder
			            .path("/delete-project-if-exist")
			            .build()
			    		)
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)
			    .body(BCephalParameterTest.PROJECT_CODE)
			    .header(RequestParams.BC_CLIENT, "1")
			    .header(RequestParams.LANGUAGE, "en")
			    .retrieve()
			    .toEntity(Boolean.class);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isNotNull();
		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		Boolean result = response.getBody();
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result).isTrue();		
	}
	
	
	@Test
	@DisplayName("Create project")
    @Order(2)
	public void createProjectTest() {
		ResponseEntity<Project> response = restClient.post()
			    .uri(uriBuilder -> uriBuilder
			            .path("/create")
			            .queryParam(RequestParams.BC_CLIENT, "1")
			            .build()
			    		)
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)
			    .body(project)
			    .header(RequestParams.BC_PROFILE, "1")
			    .header(RequestParams.LANGUAGE, "en")
			    .retrieve()
			    .toEntity(Project.class);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isNotNull();
		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(201);
		project = response.getBody();
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.getId()).isNotNull();		
	}
	
	//@Test
	@DisplayName("Update project")
    @Order(2)
	public void updateProjectTest() {
		project.setDescription("Bcephal Test project...");
		ResponseEntity<Project> response = restClient.post()
			    .uri(uriBuilder -> uriBuilder
			            .path("/update")
			            .queryParam(RequestParams.BC_CLIENT, "1")
			            .build()
			    		)
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)
			    .body(project)
			    .header(RequestParams.LANGUAGE, "en")
			    .retrieve()
			    .toEntity(Project.class);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isNotNull();
		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		project = response.getBody();
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.getId()).isNotNull();		
	}
	
	//@Test
	@DisplayName("Rename project")
    @Order(3)
	public void RenameProjectTest() {
		String name = "Bcephal Test";
		ResponseEntity<Project> response = restClient.post()
			    .uri("/rename/1/" + project.getId())
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)
			    .body(name)
			    .header(RequestParams.LANGUAGE, "en")
			    .retrieve()
			    .toEntity(Project.class);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isNotNull();
		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		project = response.getBody();
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.getId()).isNotNull();		
		Assertions.assertThat(project.getName()).isEqualTo(name);
	}
	
	
	//@Test
	@DisplayName("Save a new project")
    @Order(2)
	public void saveProjectTest() {
		ResponseEntity<Project> response = restClient.post()
			    .uri("/save")
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)
			    .body(project)
			    .header(RequestParams.BC_CLIENT, "1")
			    .header(RequestParams.LANGUAGE, "en")
			    .retrieve()
			    .toEntity(Project.class);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isNotNull();
		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		project = response.getBody();
		Assertions.assertThat(project).isNotNull();
		Assertions.assertThat(project.getId()).isNotNull();
		
	}
	
	
	//@Test
	@DisplayName("Delete project")
    @Order(10)
	public void deleteProjectTest() {
		ResponseEntity<Boolean> response = restClient.delete()
			    .uri("/delete/" + project.getId())
			    .accept(MediaType.APPLICATION_JSON)			    
			    .header(RequestParams.BC_CLIENT, "1")
			    .header(RequestParams.LANGUAGE, "en")
			    .retrieve()
			    .toEntity(Boolean.class);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isNotNull();
		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
		Assertions.assertThat(response.getBody()).isEqualTo(true);
		
	}
	
	
}
