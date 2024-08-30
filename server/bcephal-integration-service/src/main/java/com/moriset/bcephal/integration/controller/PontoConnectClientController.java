package com.moriset.bcephal.integration.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibanity.apis.client.products.ponto_connect.models.Token;
import com.ibanity.apis.client.products.ponto_connect.models.create.TokenCreateQuery;
import com.ibanity.apis.client.products.ponto_connect.models.refresh.TokenRefreshQuery;
import com.ibanity.apis.client.products.ponto_connect.services.PontoConnectService;
import com.ibanity.apis.client.services.IbanityService;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.integration.domain.IntegrationParams;
import com.moriset.bcephal.integration.domain.PontoConnectBrowserData;
import com.moriset.bcephal.integration.domain.PontoConnectEntity;
import com.moriset.bcephal.integration.scheduler.SchedulerManager;
import com.moriset.bcephal.integration.service.PontoConnectEntityService;
import com.moriset.bcephal.integration.service.PontoConnectManagerService;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;
import com.moriset.bcephal.utils.socket.WebSocketDataTransfert;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/integration/ponto")
public class PontoConnectClientController {

	@Autowired SchedulerManager manager;
	
	@Autowired
	private PontoConnectEntityService pontoConnectEntityService;

	@Autowired
	private PontoConnectManagerService pontoConnectManagerService;
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	protected ObjectMapper mapper;

	@Value("${bcephal.project.temp-dir}")
	protected String tempDir;

	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody PontoConnectEntity entity,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode,
			@RequestHeader(name = RequestParams.BC_PROFILE, required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of save entity : {}", entity);
		try {
			boolean isNew = entity.getId() == null;
			if (entity.getClientId() == null) {
				entity.setClientId(client);
			}
			if (entity.getProjectCode() == null) {
				entity.setProjectCode(projectCode);
			}
			pontoConnectEntityService.refreshToken(entity);
			pontoConnectEntityService.save(entity, locale);
			entity = pontoConnectEntityService.getById((Long) entity.getId());
			String rightLevel = isNew ? "CREATE" : "EDIT";
			pontoConnectEntityService.saveUserSessionLog(principal.getName(), client, projectCode, session.getId(),
					entity.getId(), pontoConnectEntityService.getBrowserFunctionalityCode(), rightLevel, profileId);
			if(entity.isHasToken() && entity.getScheduled() != null && entity.getScheduled()) {
				manager.restart(entity.getProjectCode(), entity.getId(), entity.getClientId());
			}else {
				manager.stop(entity.getProjectCode(), entity.getId(), entity.getClientId());
			}
			log.debug("entity : {}", entity);
			return ResponseEntity.status(HttpStatus.OK).body(entity);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", entity, e);
			String message = messageSource.getMessage("unable.to.save.entity", new Object[] { entity }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<PontoConnectBrowserData> page = pontoConnectEntityService.search(filter, clientId,
					profileId, locale);
			log.debug("Ponto Connect found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search Ponto Connect by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.ponto.connect.by.filter",
					new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale, HttpSession session,
			@RequestHeader HttpHeaders headers, @RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(name = RequestParams.BC_PROJECT, required = false) String projectCode) {

		log.debug("Call of get editor data");
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			session.setAttribute(RequestParams.BC_PROFILE, profileId);
			session.setAttribute(RequestParams.BC_PROJECT, projectCode);
			EditorData<PontoConnectEntity> data = pontoConnectEntityService.getEditorData(filter, session, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/delete-items")
	public ResponseEntity<?> delete(@RequestBody List<Long> oids,
			@RequestHeader("Accept-Language") java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name = RequestParams.BC_PROFILE, required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of delete : {}", oids);
		try {
			Map<Long, String> map = new LinkedHashMap<Long, String>();
			if (oids != null) {
				oids.forEach(result -> {
					map.put(result, pontoConnectEntityService.getBrowserFunctionalityCode());
				});
			}
			pontoConnectEntityService.delete(oids, locale);
			String rightLevel = "DELETE";
			if (oids != null) {
				map.forEach((result, functionalityCode) -> {
					pontoConnectEntityService.saveUserSessionLog(principal.getName(), client, projectCode,
							session.getId(), result, functionalityCode, rightLevel, profileId);
				});
			}
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleteing : {}", oids, e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { oids }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping(path = "/upload-by-data/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadWithResume(@RequestPart("file") MultipartFile file) throws Exception {
		try {
			WebSocketDataTransfert dataTransfert = mapper.readValue(file.getBytes(), WebSocketDataTransfert.class);
			WebSocketDataTransfert reponseData = new WebSocketDataTransfert();
			reponseData.setName(dataTransfert.getName());
			if (dataTransfert.getDecision() != null && dataTransfert.getDecision().isNew()) {
				Path path2 = Paths.get(tempDir, dataTransfert.getFolder(), dataTransfert.getName());
				if (!path2.getParent().toFile().exists()) {
					path2.getParent().toFile().mkdirs();
					try {
						path2.getParent().toFile().deleteOnExit();
					} catch (Exception e) {
					}
				}
				try {
					path2.toFile().deleteOnExit();
				} catch (Exception e) {
				}
				reponseData.setRemotePath(path2.getParent().normalize().toString());
			} else {
				reponseData.setRemotePath(dataTransfert.getRemotePath());
			}
			if (dataTransfert.getData().length > 0) {
				Path path2 = Paths.get(reponseData.getRemotePath(), dataTransfert.getName());
				log.debug(" Download data from {}", path2.toString());
				FileUtils.writeByteArrayToFile(path2.toFile(), dataTransfert.getData(), true);
			}
			return ResponseEntity.ok(reponseData);
		} catch (IOException e) {
			log.error("io exception", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private String getRedirectUrl(String RedirectUrlBase) {
		log.info("RedirectUrlBase : {}", RedirectUrlBase);
		return RedirectUrlBase.concat("/integration/ponto/token");
	}

	@GetMapping("/{" + IntegrationParams.entityId + "}")
	public ResponseEntity<?> getAutorization(@PathVariable(IntegrationParams.entityId) Long entityId,
			@RequestParam(name = IntegrationParams.REDIRECT_ORIGNE_HOMEPAGE) String homePage,
			@RequestParam(name = IntegrationParams.REDIRECT_ORIGNE_HOMEPAGE_CLIENT, required = false) String homePageClient,
			@RequestParam(name = IntegrationParams.CLIENT_KEY1) String key1,
			@RequestParam(name = IntegrationParams.CLIENT_KEY2) String key2,
			HttpSession session)
			throws URISyntaxException, UnsupportedEncodingException, NoSuchAlgorithmException {
		log.info("homePage : {}", homePage);
		log.info("homePageClient : {}", homePageClient);
		session.setAttribute(IntegrationParams.REDIRECT_ORIGNE_HOMEPAGE, homePage);
		try {
			PontoConnectEntity entity = pontoConnectEntityService.getById(entityId);
			String http = entity.getAuthorizationUrl();

			if (!StringUtils.hasText(http)) {
				http = "https://sandbox-authorization.myponto.com/oauth2/auth?";
			}
			if (!http.trim().endsWith("?")) {
				http = http.concat("?");
			}
			String format_ = "client_id=%s&client_secret=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&code_challenge=%s&code_challenge_method=S256";
			String state = UUID.randomUUID().toString();
			Map<String, Object> additionalHeaders = new LinkedHashMap<>();
			String codeVerifier = pontoConnectManagerService.generateCodeVerifier();
			additionalHeaders.put(IntegrationParams.codeVerifier, codeVerifier);
			String key00 = IntegrationParams.CLIENT_KEY1.concat("=").concat(key1).concat("&");
			String key01 = IntegrationParams.CLIENT_KEY2.concat("=").concat(key2);			
			additionalHeaders.put(IntegrationParams.CLIENT_KEY1, key00.concat(key01));
			additionalHeaders.put(IntegrationParams.entity, entity);
			additionalHeaders.put(IntegrationParams.REDIRECT_ORIGNE_HOMEPAGE_CLIENT,
					StringUtils.hasText(homePageClient) ? homePageClient : homePage);
			session.setAttribute(state, additionalHeaders);
			String redirectUrl = getRedirectUrl(homePage.substring(0, homePage.indexOf("/bcephal/")));
			String http0 = String.format(format_, entity.getOauth2().getClientId(),
					entity.getOauth2().getClientSecret(), URLEncoder.encode(redirectUrl, "UTF-8"),
					URLEncoder.encode("offline_access ai pi name", "UTF-8"), state,
					pontoConnectManagerService.generateCodeChallange(codeVerifier));

			http0 = String.format("%s%s", http, http0);
			log.info("codeVerifier : {}", codeVerifier);
			log.info("Redirect URI : {}", redirectUrl);
			log.info("Redirect to : {}", http0);
			entity.getOauth2().setRedirectUrlBase(redirectUrl);
			return ResponseEntity.status(HttpStatus.FOUND).location(new URI(http0)).build();
		} catch (Exception e) {
			log.error("Unexpected error while retrieving redirection from entityId : {}", entityId, e);
			String message = messageSource.getMessage("unable.to.get.redirect.to", new Object[] { entityId },
					Locale.ENGLISH);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/token")
	public ResponseEntity<?> getToken(@RequestParam(IntegrationParams.code) String code, HttpSession session,
			@RequestParam(name = IntegrationParams.state, required = false) String state,
			@RequestParam(name = IntegrationParams.error_description, required = false) String errorDescription) throws URISyntaxException {
		String redirectTorigine = "";
		String exp = "**@@*::__;;;**___";
		String succes = "?".concat(IntegrationParams.REQ_S).concat("=s&");
		String error = "?".concat(IntegrationParams.REQ_S).concat("=s&");
		try {
			log.info("Try to creating token by code : {}", code);
			
			if (StringUtils.hasText(state)) {
				@SuppressWarnings("unchecked")
				Map<String, Object> sessionEntity = (Map<String, Object>) session.getAttribute(state);
				String codeVerifier = (String) sessionEntity.get(IntegrationParams.codeVerifier);
				redirectTorigine = (String) sessionEntity.get(IntegrationParams.REDIRECT_ORIGNE_HOMEPAGE_CLIENT);
				String key01 = (String) sessionEntity.get(IntegrationParams.CLIENT_KEY1);
				
				if(redirectTorigine.contains("?")) {					
					if(redirectTorigine.trim().endsWith("&")) {
						redirectTorigine = redirectTorigine.concat(key01);
					}else {
						redirectTorigine = redirectTorigine.concat("&").concat(key01);
					}
				}else {
					redirectTorigine = redirectTorigine.concat("?").concat(key01);
				}
				
				redirectTorigine = redirectTorigine.replace("?", exp);
				
				PontoConnectEntity entity = (PontoConnectEntity) sessionEntity.get(IntegrationParams.entity);

				IbanityService ibanityService = pontoConnectManagerService.getIbanityService(entity);
				PontoConnectService pontoConnectService = ibanityService.pontoConnectService();

				Map<String, String> additionalHeaders = new LinkedHashMap<String, String>();
				additionalHeaders.put("Content-Type", "application/x-www-form-urlencoded");
				additionalHeaders.put("Accept", "application/vnd.api+json");

				log.trace("Token created codeVerifier : {}", codeVerifier);
				log.trace("Redirect URI : {}", entity.getOauth2().getRedirectUrlBase());

				Token token = pontoConnectService.tokenService()
						.create(TokenCreateQuery.builder()
								.clientSecret(entity.getOauth2().getClientSecret())
								.authorizationCode(code).codeVerifier(codeVerifier)
								.additionalHeaders(additionalHeaders)
								.redirectUri(entity.getOauth2().getRedirectUrlBase())
								.build());

				token = pontoConnectService.tokenService()
						.refresh(TokenRefreshQuery.builder()
						.clientSecret(entity.getOauth2().getClientSecret())
						.refreshToken(token.getRefreshToken())
						.build());
				
				entity.setAccessToken(token.getAccessToken());
				entity.setRefreshToken(token.getRefreshToken());
				entity.setTokenType(token.getTokenType());
				entity.setExpiresIn(token.getExpiresIn());
				entity.setScope(token.getScope());

				pontoConnectEntityService.save(entity, Locale.ENGLISH);
				log.trace("Token created  : {}", entity);
				log.info("Redirect to  : {}", redirectTorigine);
				session.removeAttribute(state);
				redirectTorigine = redirectTorigine.replace(exp, succes);
				return ResponseEntity.status(HttpStatus.FOUND).location(new URI(redirectTorigine)).build();
			} else {
				log.error("Unexpected error while retrieving state not found  errorDescription {}",errorDescription);
//				String message = messageSource.getMessage("unable.to.get.state.not.found", new Object[] { errorDescription },
//						Locale.ENGLISH);
				redirectTorigine = redirectTorigine.replace(exp, error);
				return ResponseEntity.status(HttpStatus.FOUND).location(new URI(redirectTorigine)).build();
				//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
			}
		} catch (Exception e) {
			session.removeAttribute(state);
			log.error("Unexpected error while retrieving to validate authorization code : {} {} {}", code, e,
					e.getLocalizedMessage(), e.getMessage());
//			String message = messageSource.getMessage("unable.to.validate.authorization.code", new Object[] { code },
//					Locale.ENGLISH);
			redirectTorigine = redirectTorigine.replace(exp, error);
			return ResponseEntity.status(HttpStatus.FOUND).location(new URI(redirectTorigine)).build();
			//return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/resfresh-token/{" + IntegrationParams.entityId + "}")
	public ResponseEntity<?> getRefreshToken(@PathVariable(IntegrationParams.entityId) Long entityId,
			HttpSession session) throws URISyntaxException {
		try {
			log.info("Try to creating token by code : {}", entityId);
			PontoConnectEntity entity = pontoConnectEntityService.getById(entityId);
			IbanityService ibanityService = pontoConnectManagerService.getIbanityService(entity);
			PontoConnectService pontoConnectService = ibanityService.pontoConnectService();
			
			Map<String, String> additionalHeaders = new LinkedHashMap<String, String>();
			additionalHeaders.put("Content-Type", "application/x-www-form-urlencoded");
			additionalHeaders.put("Accept", "application/vnd.api+json");
			
			Token token = pontoConnectService.tokenService()
					.refresh(TokenRefreshQuery.builder()
					.clientSecret(entity.getOauth2().getClientSecret())
					.refreshToken(entity.getRefreshToken())
					.build());
			
			entity.setAccessToken(token.getAccessToken());
			entity.setRefreshToken(token.getRefreshToken());
			entity.setTokenType(token.getTokenType());
			entity.setExpiresIn(token.getExpiresIn());
			entity.setScope(token.getScope());

			pontoConnectEntityService.save(entity, Locale.ENGLISH);
			return ResponseEntity.status(HttpStatus.OK).body(entity);
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", entityId, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { entityId },
					Locale.ENGLISH);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/resfresh-transactions/{" + IntegrationParams.entityId + "}")
	public ResponseEntity<?> getTransactions(@PathVariable(IntegrationParams.entityId) Long entityId,
			HttpSession session) throws URISyntaxException {
		try {
			log.info("Try to creating token by code : {}", entityId);
			PontoConnectEntity entity = pontoConnectEntityService.getById(entityId);
			IbanityService ibanityService = pontoConnectManagerService.getIbanityService(entity);
			log.trace("Try to creating ibanityService successfully");
			Token token = ibanityService.pontoConnectService().tokenService()
					.refresh(TokenRefreshQuery.builder()
					.clientSecret(entity.getOauth2().getClientSecret())
					.refreshToken(entity.getRefreshToken())
					.build());
			
			entity.setAccessToken(token.getAccessToken());
			entity.setRefreshToken(token.getRefreshToken());
			entity.setTokenType(token.getTokenType());
			entity.setExpiresIn(token.getExpiresIn());
			entity.setScope(token.getScope());

			pontoConnectEntityService.save(entity, Locale.ENGLISH);
			int count = pontoConnectManagerService.buildTransactions(entity, ibanityService);
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", entityId, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { entityId },
					Locale.ENGLISH);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
}
