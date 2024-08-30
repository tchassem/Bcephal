package com.moriset.bcephal.planification.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
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
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.planification.config.HashMapHttpMessageConverter;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.service.TransformationRoutineService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.utils.BCephalParameterTest;
import com.moriset.bcephal.utils.RequestParams;
import com.moriset.planification.TranformationFactory;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.ClientEndpointConfig.Builder;
import jakarta.websocket.ClientEndpointConfig.Configurator;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;

 /**
  * @author pascal dev
  */

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TansformationIntegrationTest {
	
	@LocalServerPort
	private int port;
	
	private static String baseUrl;
	
	private static RestClient restClient;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	@Autowired
	InitiationService initiationService;
	
	@Autowired
	TransformationRoutineService transformationRoutineService;
	
	@Autowired
	GrilleService grilleService;
	@Autowired
	MaterializedGridService materializedGridService;
	
	private static List<Long> matGridIds;
	private static EditorDataFilter filter;
	private static List<EditorData<TransformationRoutine>> editorTranGrids;
	
	private static List<TransformationRoutine> routines;
//	private static List<Long> ids;
	
	@BeforeAll
    static void init() throws Exception {
		log.info("Begin of Before all tests");
    	restClient = RestClient.builder().messageConverters(addConverters()).build();
    	matGridIds = new ArrayList<>();
    	editorTranGrids = new ArrayList<>();
    	filter = new EditorDataFilter();
		filter.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		filter.setNewData(true);
    }
	
	@BeforeEach
	public void setUp() throws Exception {
		baseUrl = "http://localhost:".concat("" + port).concat("/planification/routine");
		buildToken();
	}
	 
	@Test
	@DisplayName("Get all materializeGrid")
	@Order(1)
	@Commit
	public void getMatGridsTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		List<String> mats = TranformationFactory.names();
		for(String matName : mats) {
			
			MaterializedGrid grid = materializedGridService.getByName(matName);
	   		assertThat(grid).isNotNull();
	   		assertThat(grid.getId()).isNotNull();
	   		matGridIds.add(grid.getId());
		}
	}
	
	@Test
	@DisplayName("Get all TransformationEditorData")
	@Order(2)
	@Commit
	public void getAllTransEditorTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(matGridIds.size() > 0).isTrue();
		for(Long id : matGridIds) {
			filter.setDataSourceId(id);
			ResponseEntity<EditorData<TransformationRoutine>> response = restClient.post().uri(baseUrl + "/editor-data")
				    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
				    .body(filter).retrieve().toEntity(new ParameterizedTypeReference<EditorData<TransformationRoutine>>() {});
				
				assertThat(response).isNotNull();
				assertThat(response.getStatusCode()).isNotNull();
				assertThat(response.getStatusCode().value()).isEqualTo(200);
				assertThat(response.getBody()).isNotNull();
				editorTranGrids.add(response.getBody()) ;
		}
	}
	 
	 @Test
		@DisplayName("Create all routines")
		@Order(3)
		@Commit
		public void saveTranformationTest() throws Exception {
			interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
			assertThat(editorTranGrids.size() > 0).isTrue();
			routines = TranformationFactory.buildTransformationRoutines(editorTranGrids, grilleService,transformationRoutineService, materializedGridService, initiationService);
			for(TransformationRoutine routine : routines) {
				ResponseEntity<TransformationRoutine> response = restClient.post().uri(baseUrl + "/save")
					    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
					    .body(routine).retrieve().toEntity(TransformationRoutine.class);
					
					assertThat(response).isNotNull();
					assertThat(response.getStatusCode()).isNotNull();
					assertThat(response.getStatusCode().value()).isEqualTo(200);
					routine = response.getBody();
					assertThat(routine).isNotNull();
					//assertThat(routine.getId()).isNotNull();
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
		    if (StringUtils.hasText(BCephalParameterTest.TOKEN)) {
				headers.set(RequestParams.AUTHORIZATION, "Bearer " + BCephalParameterTest.TOKEN);
			}
			return headers;
		}
	    
//	    private String buildRequestParameters() {
//			String query = "?" + RequestParams.LANGUAGE + "=" + "en" + 
//						   "&" + RequestParams.BC_CLIENT + "=" + BCephalParameterTest.CLIENT_ID + 
//						   "&" + RequestParams.BC_PROJECT + "=" + BCephalParameterTest.PROJECT_CODE +
//						   "&" + RequestParams.BC_PROFILE + "=" + BCephalParameterTest.PROFILE_ID;
//			if (StringUtils.hasText(BCephalParameterTest.TOKEN)) {
//				query += "&" + RequestParams.AUTHORIZATION + "=" + "Bearer%20" + BCephalParameterTest.TOKEN;
//			}		   
//			return query;
//		}
	    
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
	    

	    public class TestWebSocketClient extends Endpoint {
	    	
	        private Session session;
	        
	        public TestWebSocketClient(URI uri) {
	        	 try {
	        		 
	        		 WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	        		 Builder configBuilder = ClientEndpointConfig.Builder.create();
	        		 configBuilder.configurator(new Configurator() {
	        		     @Override
	        		     public void beforeRequest(Map<String, List<String>> headers) {
	        		     buildRequestHeaders().forEach((k, v) -> {
	        		    	 headers.put(k, v);
	        		     });
	        		     }
	        		 });
	        		 ClientEndpointConfig clientConfig = configBuilder.build();
	        		 container.connectToServer(this, clientConfig, uri);
	             } catch (Exception e) {
	                 throw new RuntimeException(e);
	             }
	        }
	        
	        
	        public void sendMessage(String message) {
	        	this.session.getAsyncRemote().sendText(message);
	        	try {
					Thread.sleep(180000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
	        public void sendMessage(Object message) {
	        	this.session.getAsyncRemote().sendObject(message);
	        }
	        
	                
			@Override
			public void onOpen(Session session, EndpointConfig config) {
				this.session = session;
				session.addMessageHandler(new MessageHandler.Whole<String>() {
		            @Override
		            public void onMessage(String text) {
		            	System.out.println(String.format("%s %s", "Received message: ", text));
		            }
		        });
			}
	    }
}
