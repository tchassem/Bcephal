
package com.moriset.bcephal.reporting.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.reporting.JoinFactory;
import com.moriset.bcephal.reporting.ReportGridFactory;
import com.moriset.bcephal.reporting.config.HashMapHttpMessageConverter;
import com.moriset.bcephal.reporting.service.ReportGridService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.utils.BCephalParameterTest;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pascal Dev
 *
 */
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReportGridControllerIntegrationTests {

	@LocalServerPort
	private int port;

	private static String baseUrl1;
	private static String baseUrl2;

	private static RestClient restClient;

	@Autowired
	MultiTenantInterceptor interceptor;
	
	@Autowired
	MaterializedGridService materializedGridService;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	InitiationService initiationService;
	
	@Autowired
	JoinService joinService;

//	private static Grille grid;
	
	//private static List<MaterializedGrid> matGrids;
	
	private static List<Join> joins;
	
	private static List<Grille> grilles;

	private static String token;

	@BeforeAll
	static void init() {
		log.info("Begin of Before all tests");
		restClient = RestClient.builder().messageConverters(addConverters()).build();
		grilles = new ArrayList<>();
	}

	@BeforeEach
	public void setUp() throws Exception {
		baseUrl1 = "http://localhost:".concat("" + port).concat("/reporting/grid");
		baseUrl2 = "http://localhost:".concat("" + port).concat("/join");
		buildRequestToken();
	}

	@Test
	@DisplayName("create all reports")
	@Order(1)
	@Commit
	public void saveReportGridTest() throws Throwable {

		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		grilles = ReportGridFactory.buildReportingGrilles(materializedGridService, initiationService);
		for(Grille grid : grilles) {
			ResponseEntity<Grille> response = restClient.post().uri(baseUrl1 + "/save")
				    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
				    .body(grid).retrieve().toEntity(Grille.class);
				
				assertThat(response).isNotNull();
				assertThat(response.getStatusCode()).isNotNull();
				assertThat(response.getStatusCode().value()).isEqualTo(200);
				grid = response.getBody();
				assertThat(grid).isNotNull();
				assertThat(grid.getId()).isNotNull();
		}
	}

	
	/**
	 * @throws Throwable
	 */
	/*
	 * @Test
	 * 
	 * @DisplayName("Columns of report grid")
	 * 
	 * @Order(2)
	 * 
	 * @Commit public void getReportGridColumnsTest() throws Throwable {
	 * interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
	 * grilles = ReportGridFactory.buildReportingGrilles(materializedGridService);
	 * for(Grille grid : grilles) { Long id =
	 * reportGridService.getByName(grid.getName()).getId(); ResponseEntity<?> result
	 * = restClient.get().uri(baseUrl1 + "/columns/" +id) .headers(httpHeaders ->
	 * httpHeaders.addAll(buildRequestHeaders())).retrieve().toEntity(List.class);
	 * 
	 * Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
	 * Assertions.assertThat(((List<?>) result.getBody()).size()).isGreaterThan(0);
	 * //Assertions.assertThat(((List<?>) result.getBody()).size()).isEqualTo(3); }
	 * }
	 */
	
	
	@Test
	@DisplayName("create all joins")
	@Order(3)
	@Commit
	public void saveJoinTest() throws Throwable {

		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		joins = JoinFactory.buildJoinds(grilleService, materializedGridService, initiationService);
		for(Join join : joins) {
			ResponseEntity<Join> response = restClient.post().uri(baseUrl2 + "/save")
				    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
				    .body(join).retrieve().toEntity(Join.class);
				
				assertThat(response).isNotNull();
				assertThat(response.getStatusCode()).isNotNull();
				assertThat(response.getStatusCode().value()).isEqualTo(200);
				join = response.getBody();
				assertThat(join).isNotNull();
				assertThat(join.getId()).isNotNull();
		}
	}
	/*
	 * @Test
	 * 
	 * @DisplayName("Reset report grid publication")
	 * 
	 * @Order(3)
	 * 
	 * @Commit public void resetPublication() throws Exception {
	 * Interceptor.setTenantForServiceTest(reportTenantId);
	 * 
	 * ResponseEntity<EditorData> result = restClient.get().uri(baseUrl +
	 * "/reset-publication/" + grid.getId()) .headers(httpHeaders ->
	 * httpHeaders.addAll(buildRequestHeaders())).retrieve().toEntity(EditorData.
	 * class);
	 * 
	 * Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
	 * Assertions.assertThat(((Grille)result.getBody().getItem()).getPublished()).
	 * isFalse(); }
	 * 
	 * @Test
	 * 
	 * @DisplayName("Refresh report grid publication")
	 * 
	 * @Order(4)
	 * 
	 * @Commit public void refreshPublication() throws Exception {
	 * Interceptor.setTenantForServiceTest(reportTenantId);
	 * 
	 * ResponseEntity<?> result = restClient.get().uri(baseUrl +
	 * "/refresh-publication/" + grid.getId()) .headers(httpHeaders ->
	 * httpHeaders.addAll(buildRequestHeaders())).retrieve().toEntity(Boolean.class)
	 * ;
	 * 
	 * Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
	 * Assertions.assertThat((boolean)result.getBody()).isTrue(); }
	 * 
	 * @Test
	 * 
	 * @DisplayName("publish an existing report grid")
	 * 
	 * @Order(5)
	 * 
	 * @Commit public void publishTest() throws Exception {
	 * Interceptor.setTenantForServiceTest(reportTenantId);
	 * 
	 * ResponseEntity<EditorData> result = restClient.get().uri(baseUrl +
	 * "/publish/" + grid.getId()) .headers(httpHeaders ->
	 * httpHeaders.addAll(buildRequestHeaders())).retrieve().toEntity(EditorData.
	 * class);
	 * 
	 * Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
	 * Assertions.assertThat(((Grille)result.getBody().getItem()).getPublished()).
	 * isTrue(); }
	 */
	
//	@Test
//	@DisplayName("Delete report grid.")
//	@Order(3)
//	@Commit
//	public void destroy() throws Throwable {
//		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
//		grilles = ReportGridFactory.buildReportingGrilles(materializedGridService);
//		List<Long> body = new ArrayList<>();
//		for(Grille grid : grilles) {
//		
//		  body.add(grid.getId());
//		}
//		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl1 + "/delete-items")
//				.headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders())).body(body).retrieve()
//				.toEntity(Boolean.class);
//
//		Assertions.assertThat(response).isNotNull();
//		Assertions.assertThat(response.getStatusCode()).isNotNull();
//		Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
//		Assertions.assertThat(response.getBody()).isEqualTo(true);
//		
//	}

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
		if (StringUtils.hasText(token)) {
			headers.set(RequestParams.AUTHORIZATION, "Bearer " + token);
		}
		return headers;
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

	private boolean isValidJson(String jsonString) {
		try {
			return new ObjectMapper().readTree(jsonString).isObject();
		} catch (Exception e) {
			return false;
		}
	}

	private void buildRequestToken() {
		if (!StringUtils.hasText(token)) {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
			body.add("grant_type", "password");
			body.add("client_secret", "33760241-b2f1-481b-b1a1-6e2b2938145e");
			body.add("client_id", "bcephal");
			body.add("username", "emmaus");
			body.add("password", "emmaus");
			ResponseEntity<String> result = restClient.post()
					.uri("http://192.168.100.93:8282/auth/realms/bcephalRealm/protocol/openid-connect/token?")
					.body(body).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON)
					.retrieve().toEntity(String.class);

			if (!result.getStatusCode().is2xxSuccessful() || !isValidJson(result.getBody())) {
				return;
			}
			Map<String, String> map = new HashMap<String, String>();
			try {
				map = new ObjectMapper().readValue(result.getBody(), new TypeReference<HashMap<String, String>>() {
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			token = map.get("access_token").toString();
		}
		log.info("The current token is {}", token);
	}
}
