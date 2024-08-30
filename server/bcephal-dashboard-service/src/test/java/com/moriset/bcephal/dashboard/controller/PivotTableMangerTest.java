package com.moriset.bcephal.dashboard.controller;

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
import com.moriset.bcephal.dashboard.config.HashMapHttpMessageConverter;
import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.factory.PivotTableFactory;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.utils.BCephalParameterTest;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PivotTableMangerTest {
	@LocalServerPort
	private int port;
	
	private static String baseUrl;
	
	//private static String baseUrl2;
	
	private static RestClient restClient;
	private static String token;
	
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	
	
	private static List<DashboardReport> pivots;
	
	@BeforeAll
    static void init() throws Exception {
		log.info("Begin of Before all tests");
    	restClient = RestClient.builder().messageConverters(addConverters()).build();
    	pivots= new ArrayList<>();
    }
    
    @BeforeEach
	public void setUp() throws Exception {
		baseUrl = "http://localhost:".concat("" + port).concat("/dashboarding/report");
		buildToken();
	}
    
   
    
    @Test
	@DisplayName("Create all pivots")
	@Order(1)
	@Commit
	public void savePivotsTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		pivots = PivotTableFactory.BuildAllPivot();
		assertThat(pivots).isNotNull();
		for(DashboardReport pivot : pivots) {
			ResponseEntity<DashboardReport> response = restClient.post().uri(baseUrl + "/save")
				    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
				    .body(pivot).retrieve().toEntity(DashboardReport.class);
				
				assertThat(response).isNotNull();
				assertThat(response.getStatusCode()).isNotNull();
				assertThat(response.getStatusCode().value()).isEqualTo(200);
				pivot = response.getBody();
				assertThat(pivot).isNotNull();
				assertThat(pivot.getId()).isNotNull();
		}
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
		if (StringUtils.hasText(token)) {
			headers.set(RequestParams.AUTHORIZATION, "Bearer " + token);
		}
		return headers;
	}
    
    private void buildToken() {
		if (!StringUtils.hasText(token)) {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
			body.add("grant_type", "password");
			body.add("client_secret", "33760241-b2f1-481b-b1a1-6e2b2938145e");
			body.add("client_id", "bcephal");
			body.add("username", "admin");
			body.add("password", "admin");
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
	
	 private boolean isValidJson(String jsonString) {
			try {
				return new ObjectMapper().readTree(jsonString).isObject();
			} catch (Exception e) {
				return false;
			}
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
