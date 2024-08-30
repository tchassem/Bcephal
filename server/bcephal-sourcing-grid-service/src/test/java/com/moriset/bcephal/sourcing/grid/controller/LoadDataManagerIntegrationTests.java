package com.moriset.bcephal.sourcing.grid.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.service.FileLoaderRunData;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.sourcing.grid.FileLoaderFactory;
import com.moriset.bcephal.sourcing.grid.InputGrilleFactory;
import com.moriset.bcephal.sourcing.grid.MaterializedGilleFactory;
import com.moriset.bcephal.sourcing.grid.config.HashMapHttpMessageConverter;
import com.moriset.bcephal.utils.BCephalParameterTest;
import com.moriset.bcephal.utils.RequestParams;

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
 * @author Wakeu Georges Favier
 *
 */
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"int", "servi"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoadDataManagerIntegrationTests {

	@LocalServerPort
	private int port;
	
	private static String baseUrl;
	
	private static URI webSocketUrl;
	
	private static RestClient restClient;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	MultiTenantInterceptor interceptor;
	
	@Autowired
	InitiationService initiationService;
	
	@Autowired
	MaterializedGridService materializedGridService;
	
	@Autowired
	GrilleService grilleService;
	
	private static List<Grille> grilles;
	
	private static List<MaterializedGrid> matGrids;
	
	private static List<FileLoader> fileLoadersForGrids;
	
	private static List<FileLoader> fileLoadersSaved;
	
	private static GrilleDataFilter filter;
	
	private static MaterializedGridDataFilter matFilter;
	
	private static FileLoaderRunData data;
	
	private static TestWebSocketClient client;
	
	private static List<Long> ids;
	
    @BeforeAll
    static void init() throws Exception {
		log.info("Begin of Before all tests");
    	restClient = RestClient.builder().messageConverters(addConverters()).build();
    	filter = new GrilleDataFilter();
    	filter.setAllowRowCounting(true);
    	filter.setOrderAsc(true);
    	matFilter = new MaterializedGridDataFilter();
    	matFilter.setOrderAsc(true);
    	matFilter.setAllowRowCounting(true);
    	ids = new ArrayList<>();
    	fileLoadersSaved = new ArrayList<>();
    }
    
    @BeforeEach
	public void setUp() throws Exception {
		baseUrl = "http://localhost:".concat("" + port).concat("/sourcing");
		buildToken();
		webSocketUrl = URI.create("ws://localhost:".concat("" + port).concat("/ws/sourcing/file-loader/load").concat(buildRequestParameters()));
		client = new TestWebSocketClient(webSocketUrl);
	}
    
    @Test
	@DisplayName("Create all grids")
	@Order(1)
	@Commit
	public void saveGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		grilles = InputGrilleFactory.buildGrilles(initiationService);
		for(Grille grid : grilles) {
			ResponseEntity<Grille> response = restClient.post().uri(baseUrl.concat("/grid") + "/save")
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
    
    @Test
	@DisplayName("Create all materialize grids")
	@Order(2)
	@Commit
	public void saveMatGrilleTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		matGrids = MaterializedGilleFactory.buildMaterializedGrilles();
		for(MaterializedGrid grid : matGrids) {
			ResponseEntity<MaterializedGrid> response = restClient.post().uri(baseUrl.concat("/mat-grid") + "/save")
				    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
				    .body(grid).retrieve().toEntity(MaterializedGrid.class);
				
				assertThat(response).isNotNull();
				assertThat(response.getStatusCode()).isNotNull();
				assertThat(response.getStatusCode().value()).isEqualTo(200);
				grid = response.getBody();
				assertThat(grid).isNotNull();
				assertThat(grid.getId()).isNotNull();
				ids.add(grid.getId());
		}
	}
    
    @Test
	@DisplayName("publish all matGrids")
	@Order(3)
	@Commit
	public void publishGrillesTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(ids).isNotNull();
		assertThat(ids.size() > 0).isTrue();
		
		ResponseEntity<Boolean> response = restClient.post().uri(baseUrl.concat("/mat-grid") + "/publish")
		    .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		    .body(ids).retrieve().toEntity(Boolean.class);
		
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isTrue();
	}
     
    @Test
	@DisplayName("Create all file loaders")
	@Order(4)
	@Commit
	public void saveFileLoaderTest() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		
		  fileLoadersForGrids = FileLoaderFactory.buildFileLoaders(grilleService, initiationService);
		  for(FileLoader fileLoader : fileLoadersForGrids) { ResponseEntity<FileLoader>
		  response = restClient.post().uri(baseUrl.concat("/file-loader") + "/save")
		  .headers(httpHeaders -> httpHeaders.addAll(buildRequestHeaders()))
		  .body(fileLoader).retrieve().toEntity(FileLoader.class);
		  
		  assertThat(response).isNotNull();
		  assertThat(response.getStatusCode()).isNotNull();
		  assertThat(response.getStatusCode().value()).isEqualTo(200); 
		  fileLoader = response.getBody(); assertThat(fileLoader).isNotNull();
		  assertThat(fileLoader.getId()).isNotNull();
		  fileLoadersSaved.add(fileLoader);
		  }
	}
     
    @Test
	@DisplayName("Load file Acquiring___0_SFPA0WK000001")
	@Order(5)
	@Commit
	public void loadFileLoader0Test() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(fileLoadersSaved.size() > 0).isTrue();
		assertThat(fileLoadersSaved.get(0).getTargetId()).isNotNull();
		assertThat(fileLoadersSaved.get(0).getTargetName()).isNotNull();
		assertThat(fileLoadersSaved.get(0).getTargetName()).isEqualTo("BNK001 EPB funding account");
		FileLoader loader = fileLoadersSaved.get(0);
		data = FileLoaderFactory.buildFileLoaderRunData(loader);
		String playload = mapper.writeValueAsString(data);
		
		client.sendMessage(playload);
		
		Grille grid = grilleService.getByName(fileLoadersSaved.get(0).getTargetName());
		assertThat(grid).isNotNull();
		filter.setGrid(grid);
		
		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
		assertThat(page).isNotNull();
		assertThat(page.getTotalItemCount()).isEqualTo(2073);
    }
     
    @Test
	@DisplayName("Load file Acquiring___0_SFPA0WK0000011")
	@Order(6)
	@Commit
	public void loadFileLoader1Test() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(fileLoadersSaved.size() > 1).isTrue();
		assertThat(fileLoadersSaved.get(1).getTargetId()).isNotNull();
		assertThat(fileLoadersSaved.get(1).getTargetName()).isNotNull();
		assertThat(fileLoadersSaved.get(1).getTargetName()).isEqualTo("BNK001 EPB funding account");
		
		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(1));
		String playload = mapper.writeValueAsString(data);
		
		client.sendMessage(playload);
		
		Grille grid = grilleService.getByName(fileLoadersSaved.get(1).getTargetName());
		assertThat(grid).isNotNull();
		filter.setGrid(grid);
		
		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
		assertThat(page).isNotNull();
		assertThat(page.getTotalItemCount()).isEqualTo(2073);
    }
     
    @Test
	@DisplayName("Load file BNK001 EPB funding account")
	@Order(7)
	@Commit
	public void loadFileLoader2Test() throws Exception {
		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
		assertThat(fileLoadersSaved.size() > 2).isTrue();
		assertThat(fileLoadersSaved.get(2).getTargetId()).isNotNull();
		assertThat(fileLoadersSaved.get(2).getTargetName()).isNotNull();
		assertThat(fileLoadersSaved.get(2).getTargetName()).isEqualTo("BNK001 EPB funding account");
		
		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(2));
		String playload = mapper.writeValueAsString(data);
		
		client.sendMessage(playload);
		
		Grille grid = grilleService.getByName(fileLoadersSaved.get(2).getTargetName());
		assertThat(grid).isNotNull();
		filter.setGrid(grid);
		
		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
		assertThat(page).isNotNull();
		assertThat(page.getTotalItemCount()).isEqualTo(2073);
    }
     
    @Test
   	@DisplayName("Load file INP002 Issuing SA - CGA")
   	@Order(8)
   	@Commit
   	public void loadFileLoader3Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 3).isTrue();
   		assertThat(fileLoadersSaved.get(3).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(3).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(3).getTargetName()).isEqualTo("INP002 Issuing SA");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(3));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(3).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(302);
       }
     
    @Test
   	@DisplayName("Load file INP002 Issuing SA - MGA")
   	@Order(9)
   	@Commit
   	public void loadFileLoader4Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 4).isTrue();
   		assertThat(fileLoadersSaved.get(4).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(4).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(4).getTargetName()).isEqualTo("INP002 Issuing SA");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(4));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(4).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(302);
       }
     
    @Test
   	@DisplayName("Load file INP003 Issuing MA - CMA")
   	@Order(10)
   	@Commit
   	public void loadFileLoader5Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 5).isTrue();
   		assertThat(fileLoadersSaved.get(5).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(5).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(5).getTargetName()).isEqualTo("INP003 Issuing MA");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(5));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(5).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
  		assertThat(page.getTotalItemCount()).isEqualTo(3083);
       }
     
    @Test
   	@DisplayName("Load file INP003 Issuing MA - MMA")
   	@Order(11)
   	@Commit
   	public void loadFileLoader6Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 6).isTrue();
   		assertThat(fileLoadersSaved.get(6).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(6).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(6).getTargetName()).isEqualTo("INP003 Issuing MA");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(6));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(6).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(3083);
       }
     
    @Test
   	@DisplayName("Load file INP005 Issuing REC")
   	@Order(12)
   	@Commit
   	public void loadFileLoader7Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 7).isTrue();
   		assertThat(fileLoadersSaved.get(7).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(7).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(7).getTargetName()).isEqualTo("INP005 Issuing REC");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(7));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(7).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(4593);
       }
     
    @Test
   	@DisplayName("Load file INP005 REC Maestro")
   	@Order(13)
   	@Commit
   	public void loadFileLoader8Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 8).isTrue();
   		assertThat(fileLoadersSaved.get(8).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(8).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(8).getTargetName()).isEqualTo("INP005 REC Maestro");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(8));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(8).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(0);
       }
     
    @Test
   	@DisplayName("Load file INP102 Acquiring SA")
   	@Order(14)
   	@Commit
   	public void loadFileLoader9Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 9).isTrue();
   		assertThat(fileLoadersSaved.get(9).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(9).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(9).getTargetName()).isEqualTo("INP102 Acquiring SA");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(9));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(9).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(68);
       }
     
    @Test
   	@DisplayName("Load file INP103 Acquiring MA")
   	@Order(15)
   	@Commit
   	public void loadFileLoader10Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 10).isTrue();
   		assertThat(fileLoadersSaved.get(10).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(10).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(10).getTargetName()).isEqualTo("INP103 Acquiring MA");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(10));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(10).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(0);
       }
     
    @Test
   	@DisplayName("Load file INP105 Acquiring REC")
   	@Order(16)
   	@Commit
   	public void loadFileLoader11Test() throws Exception {
   		interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
   		assertThat(fileLoadersSaved.size() > 11).isTrue();
   		assertThat(fileLoadersSaved.get(11).getTargetId()).isNotNull();
   		assertThat(fileLoadersSaved.get(11).getTargetName()).isNotNull();
   		assertThat(fileLoadersSaved.get(11).getTargetName()).isEqualTo("INP105 Acquiring REC");
   		
   		data = FileLoaderFactory.buildFileLoaderRunData(fileLoadersSaved.get(11));
   		String playload = mapper.writeValueAsString(data);
   		
   		client.sendMessage(playload);
   		
   		Grille grid = grilleService.getByName(fileLoadersSaved.get(11).getTargetName());
   		assertThat(grid).isNotNull();
   		filter.setGrid(grid);
   		
   		BrowserDataPage<GridItem> page = grilleService.searchRows2(filter, Locale.getDefault());
   		assertThat(page).isNotNull();
   		assertThat(page.getTotalItemCount()).isEqualTo(1099);
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
    
    /**
	 * @return parameters
	 */
    private String buildRequestParameters() {
		String query = "?" + RequestParams.LANGUAGE + "=" + "en" + 
					   "&" + RequestParams.BC_CLIENT + "=" + BCephalParameterTest.CLIENT_ID + 
					   "&" + RequestParams.BC_PROJECT + "=" + BCephalParameterTest.PROJECT_CODE +
					   "&" + RequestParams.BC_PROFILE + "=" + BCephalParameterTest.PROFILE_ID;
		if (StringUtils.hasText(BCephalParameterTest.TOKEN)) {
			query += "&" + RequestParams.AUTHORIZATION + "=" + "Bearer%20" + BCephalParameterTest.TOKEN;
		}		   
		return query;
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
