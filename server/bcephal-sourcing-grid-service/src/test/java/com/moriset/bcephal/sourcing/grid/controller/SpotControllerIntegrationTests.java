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
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.grid.service.SpotService;
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
public class SpotControllerIntegrationTests {

	@LocalServerPort
	private int port;
	
	private static String baseUrl;
	
	private static RestClient restClient;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	SpotService spotService;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	private static Spot spot;
	
	@BeforeAll
    static void init() {
		log.info("Begin of Before all tests");
    	restClient = RestClient.builder().messageConverters(addConverters()).build();
    	spot = CreateFactory.buildSpot();
    }
	
	@BeforeEach
	public void setUp() throws Exception {
		baseUrl = "http://localhost:".concat("" + port).concat("/sourcing/spot");
		buildToken();
	}
	
	@Test
	@DisplayName("Save spot")
	@Order(1)
	@Commit
	public void saveGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNull();
		
		ResponseEntity<Spot> response = restClient.post().uri(baseUrl + "/save")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(spot).retrieve().toEntity(Spot.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isNotNull();
		assertThat(response.getStatusCode().value()).isEqualTo(200);
		spot = response.getBody();
		assertThat(spot.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Delete spot")
	@Order(2)
	@Commit
	public void deleteSpotTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNotNull();
		
		List<Long> ids = List.of(spot.getId());
		
		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl + "/delete-items")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(ids).retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isTrue();
		Spot grid = spotService.getById(spot.getId());
		assertThat(grid).isNull();
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
