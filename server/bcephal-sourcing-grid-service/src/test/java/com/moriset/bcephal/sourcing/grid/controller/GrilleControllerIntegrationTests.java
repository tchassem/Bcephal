package com.moriset.bcephal.sourcing.grid.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.sourcing.grid.CreateFactory;
import com.moriset.bcephal.sourcing.grid.config.HashMapHttpMessageConverter;
import com.moriset.bcephal.utils.BCephalParameterTest;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Wakeu Georges Favier
 *
 */
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrilleControllerIntegrationTests {

	@LocalServerPort
	private int port;
	
	private static String baseUrl;
	
	private static RestClient restClient;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	private static Grille grille;
	
	private static Long reportGridId;
	
	private static GrilleDataFilter filter;
	
	private static String reportName = "Report grille test";
	
	@BeforeAll
    static void init() {
		log.info("Begin of Before all tests");
    	restClient = RestClient.builder().messageConverters(addConverters()).build();
    	grille = CreateFactory.buildGrille();
    	filter = new GrilleDataFilter();
    	filter.setAllowRowCounting(true);
    	filter.setOrderAsc(true);
    }
	
	@BeforeEach
	public void setUp() throws Exception {
		baseUrl = "http://localhost:".concat("" + port).concat("/sourcing/grid");
		buildToken();
	}
	
	@Test
	@DisplayName("Save grille")
	@Order(1)
	@Commit
	public void saveGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNull();
		
		ResponseEntity<Grille> response = restClient.post().uri(baseUrl + "/save")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(grille).retrieve().toEntity(Grille.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isNotNull();
		assertThat(response.getStatusCode().value()).isEqualTo(200);
		grille = response.getBody();
		assertThat(grille.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Create report grille")
	@Order(2)
	@Commit
	public void reportGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		ResponseEntity<Long> response = restClient.post().uri(baseUrl + "/create-report/"+ grille.getId())
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(reportName).retrieve().toEntity(Long.class);
		
		assertThat(reportGridId).isNull();
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isNotNull();
		assertThat(response.getStatusCode().value()).isEqualTo(200);
		reportGridId = response.getBody();
		assertThat(reportGridId).isNotNull();
	}
	
	@Test
	@DisplayName("Set editable grille")
	@Order(3)
	@Commit
	public void setEditableGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		boolean editable = true;
		
		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl + "/set-editable/"+ grille.getId() + "/" + editable)
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		Grille grid = grilleService.getById(grille.getId());
		assertThat(grid).isNotNull();
		assertThat(grid.isEditable()).isTrue();
	}
	
	@Test
	@DisplayName("SearchRows of grille")
	@Order(4)
	@Commit
	public void searchRowsTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		filter.setGrid(grille);
		
		ResponseEntity<BrowserDataPage<Object[]>> response = restClient.post().uri(baseUrl + "/rows")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(filter).retrieve().toEntity(new ParameterizedTypeReference<BrowserDataPage<Object[]>>() {});
		
		assertThat(response).isNotNull();
		BrowserDataPage<Object[]> page = response.getBody();
		assertThat(page).isNotNull();
		assertThat(page.getItems().size()).isEqualTo(0);
	}
	
	@Test
	@DisplayName("SearchRows2 of grille")
	@Order(5)
	public void searchRows2Test() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		GrilleDataFilter search = new GrilleDataFilter();
		search.setAllowRowCounting(true);
		search.setOrderAsc(true);
		search.setGrid(grille);
		
		ResponseEntity<BrowserDataPage<GridItem>> response = restClient.post().uri(baseUrl + "/rows2")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(search).retrieve().toEntity(new ParameterizedTypeReference<BrowserDataPage<GridItem>>() {});
		
		assertThat(response).isNotNull();
		BrowserDataPage<GridItem> page = response.getBody();
		assertThat(page).isNotNull();
		assertThat(page.getItems().size()).isEqualTo(0);
	}
	
	@Test
	@DisplayName("load grille")
	@Order(6)
	@Commit
	public void loadGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		ResponseEntity<Boolean> response = restClient.get().uri(baseUrl + "/load/" + grille.getId())
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		Grille grid = grilleService.getById(grille.getId());
		assertThat(grid).isNotNull();
		assertThat(grid.getStatus()).isEqualTo(GrilleStatus.LOADED);
	}
	
	@Test
	@DisplayName("publish grille")
	@Order(7)
	@Commit
	public void publishGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		ResponseEntity<EditorData<Grille>> response = restClient.get().uri(baseUrl + "/publish/" + grille.getId())
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .retrieve().toEntity(new ParameterizedTypeReference<EditorData<Grille>>() {});
		
		assertThat(response).isNotNull();		
		EditorData<Grille> editorData = response.getBody();
		assertThat(editorData).isNotNull();
		assertThat(editorData.getItem()).isNotNull();
		assertThat(editorData.getItem().getPublished()).isTrue();
	}
	
	@Test
	@DisplayName("publish grilles")
	@Order(8)
	@Commit
	public void publishGrillesTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<Long> ids = List.of(grille.getId());
		
		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl + "/publish")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(ids).retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isTrue();
	}
	
	@Test
	@DisplayName("Reset publication grille")
	@Order(9)
	@Commit
	public void resetPublicationTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		ResponseEntity<EditorData<Grille>> response = restClient.get().uri(baseUrl + "/reset-publication/" + grille.getId())
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .retrieve().toEntity(new ParameterizedTypeReference<EditorData<Grille>>() {});
		
		assertThat(response).isNotNull();
		EditorData<Grille> editorData = response.getBody();
		assertThat(editorData).isNotNull();
		assertThat(editorData.getItem()).isNotNull();
		//assertThat(editorData.getItem().getPublished()).isTrue();
	}
	
	@Test
	@DisplayName("Reset publication grilles")
	@Order(10)
	@Commit
	public void resetPublicationsTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<Long> ids = List.of(grille.getId());
		
		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl + "/reset-publication")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(ids).retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isTrue();
	}
	
//	@Test
//	@DisplayName("Refresh publication grille")
//	@Order(11)
//	@Commit
//	public void refreshPublicationTest() throws Exception {
//		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
//		assertThat(grille).isNotNull();
//		assertThat(grille.getId()).isNotNull();
//		
//		ResponseEntity<Boolean> response = restClient.get().uri(baseUrl + "/refresh-publication/" + grille.getId())
//		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
//		    .retrieve().toEntity(Boolean.class);
//		
//		assertThat(response).isNotNull();
//		assertThat(response.getBody()).isTrue();
//	}
	
	@Test
	@DisplayName("Refresh publication grilles")
	@Order(12)
	@Commit
	public void refresPublicationsTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<Long> ids = List.of(grille.getId());
		
		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl + "/refresh-publication")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(ids).retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isTrue();
	}
	
	@Test
	@DisplayName("Unload grille")
	@Order(13)
	@Commit
	public void unloadGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		ResponseEntity<Boolean> response = restClient.get().uri(baseUrl + "/unload/" + grille.getId())
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		Grille grid = grilleService.getById(grille.getId());
		assertThat(grid).isNotNull();
		assertThat(grid.getStatus()).isEqualTo(GrilleStatus.UNLOADED);
	}
	
	@Test
	@DisplayName("Get columns grille")
	@Order(14)
	@Commit
	public void getGrilleColumnsTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		ResponseEntity<List<GrilleColumn>> response = restClient.get().uri(baseUrl + "/columns/" + grille.getId())
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .retrieve().toEntity(new ParameterizedTypeReference<List<GrilleColumn>>() {});
		
		assertThat(response).isNotNull();
		assertThat(response.getBody().size()).isEqualTo(0);
	}
	
	@Test
	@DisplayName("Delete grille")
	@Order(15)
	@Commit
	public void deleteGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<Long> ids = List.of(grille.getId());
		
		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl + "/delete-items")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(ids).retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isTrue();
		Grille grid = grilleService.getById(grille.getId());
		assertThat(grid).isNull();
	}
	
	@Test
	@DisplayName("Delete report grille")
	@Order(16)
	@Commit
	public void deleteReportGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(reportName).isNotNull();
		
		ResponseEntity<Grille> response = restClient.get().uri(baseUrl + "/by-name/" + reportName)
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .retrieve().toEntity(Grille.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isNotNull();
		assertThat(response.getStatusCode().value()).isEqualTo(200);
		
		Grille grid  = response.getBody();
		assertThat(grid).isNotNull();
		assertThat(grid.getId()).isNotNull();
		
		List<Long> ids = List.of(grid.getId());
		
		ResponseEntity<Boolean> respons = restClient.post().uri(baseUrl + "/delete-items")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(ids).retrieve().toEntity(Boolean.class);
		
		assertThat(respons).isNotNull();
		assertThat(respons.getBody()).isTrue();
		assertThat(grille.getId()).isNotNull();
		Grille grid_ = grilleService.getById(grille.getId());
		assertThat(grid_).isNull();
	}
	
	
	/**
	 * @return headers
	 */
    private HttpHeaders buildRequestHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		headers.set(RequestParams.LANGUAGE, "en");
	    headers.set(RequestParams.BC_CLIENT, "" + BCephalParameterTest.CLIENT_ID);
	    headers.set(RequestParams.BC_PROJECT, "" + BCephalParameterTest.PROJECT_CODE);
	    headers.set(RequestParams.BC_PROFILE, "" + BCephalParameterTest.PROFILE_ID);
	    if (StringUtils.hasText(BCephalParameterTest.TOKEN)) {
			headers.set(RequestParams.AUTHORIZATION, "Bearer " + BCephalParameterTest.TOKEN);
		}
		return headers;
	}
        
	private void buildToken() throws JsonMappingException, JsonProcessingException {
		if (!StringUtils.hasText(BCephalParameterTest.TOKEN)) {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
			body.add("grant_type", "password");
			body.add("client_secret", BCephalParameterTest.KEYCLOACK_CLIENT_SECRET);
			body.add("client_id", BCephalParameterTest.KEYCLOACK_CLIENT_ID);
			body.add("username", BCephalParameterTest.KEYCLOACK_USERNAME);
			body.add("password", BCephalParameterTest.KEYCLOACK_PASSWORD);
			
			ResponseEntity<String> result = restClient.post()
					.uri(BCephalParameterTest.KEYCLOACK_URL)
					.body(body)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON).retrieve()
					.toEntity(String.class);
			
			if (!result.getStatusCode().is2xxSuccessful() || !mapper.readTree(result.getBody()).isObject()) {
				return;
	        }	        
	        Map<String, String> map = new HashMap<String, String>();
			try {
				map = new ObjectMapper().readValue(result.getBody(), new TypeReference<HashMap<String,String>>(){});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			BCephalParameterTest.TOKEN = map.get("access_token").toString();
		}		
		log.trace("The current token is {}", BCephalParameterTest.TOKEN);
	}
    
    private static Consumer<List<HttpMessageConverter<?>>> addConverters() {
		return converters -> {
			converters.add(new MappingJackson2HttpMessageConverter());
			converters.add(new ResourceHttpMessageConverter());
			converters.add(new FormHttpMessageConverter());
			converters.add(new ByteArrayHttpMessageConverter());
			converters.add(new StringHttpMessageConverter());
			converters.add(new BufferedImageHttpMessageConverter());
			converters.add(new HashMapHttpMessageConverter());
		};
	}
    

}
