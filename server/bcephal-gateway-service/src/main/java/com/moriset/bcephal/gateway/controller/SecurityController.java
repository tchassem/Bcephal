 /**
 * 
 */
package com.moriset.bcephal.gateway.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.keycloak.OAuth2Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.gateway.domain.UserInfo;
import com.moriset.bcephal.gateway.domain.UserSessionLog;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
@RestController
public class SecurityController {

	public SecurityController(ReactiveClientRegistrationRepository clientRegistrationRepository) {
		 clientRegistrationRepository.findByRegistrationId("keycloak").subscribe(client -> {
			 clientRegistration = client;
		 });
	}
	ClientRegistration clientRegistration;
	
	@RequestMapping(value = "/token", method = { RequestMethod.GET, RequestMethod.POST })
	public String getToken(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
		return authorizedClient.getAccessToken().getTokenValue();
	}

	@GetMapping(value = "/security/connected-user-info")
	public UserInfo getConnectedUserInfo(OAuth2AuthenticationToken authentication) {
		log.debug("Try to retreive connected user info : {}", "/security/connected-user-info");
		OAuth2User principal = authentication.getPrincipal();
		UserInfo user = new UserInfo(principal.getName(), "", principal.getName(), "fr");
		log.debug("Connected user info found : {}", user.getLogin());
		log.trace("Connected user info : {}", user);
		return user;
	}
	

	@GetMapping("/close-session")
	public ResponseEntity<?> closeSession(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,			
			OAuth2AuthenticationToken authentication, WebSession webSession, ServerHttpRequest request) {
		log.debug("Try to retreive connected close session : {}", "/close-session");
		webSession.invalidate();
		webSession.changeSessionId();
		ReactiveSecurityContextHolder.clearContext();
		String logoutUrl = getLogoutUrl(authorizedClient);
		try {
			ResponseEntity<?> response = logout(authorizedClient);
			if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)
					|| response.getStatusCode().equals(HttpStatus.OK)) {
				log.debug("Session close successfully");
				log.trace("Session close successfully");
				
				return ResponseEntity.ok("true");
			} else {
				log.error("Session close error response.status code: {}, server URL: {}", response.getStatusCode(),
						logoutUrl);
				log.error("Session close error response.status code: {}, server URL: {}", response.getStatusCode(),
						logoutUrl);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			log.error(
					"HttpClientErrorException invalidating token with SSO authorization server. response.status code: {}, server URL: {}",
					e.getStatusCode(), logoutUrl);
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	@GetMapping("/signout")
	public ResponseEntity<?> signout(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
			OAuth2AuthenticationToken authentication, WebSession webSession, ServerHttpRequest request,
			ServerWebExchange exchange, @AuthenticationPrincipal Jwt jwt, @AuthenticationPrincipal OidcUser user) {
		log.debug("Try to retreive connected signout : {}", "/signout");
		webSession.invalidate();
		webSession.changeSessionId();
		ReactiveSecurityContextHolder.clearContext();
		String redirect = getPostLogoutUrl(request);
		String logoutUrl = getLogoutUrl(authorizedClient);
		try {
			ResponseEntity<?> response = logout(authorizedClient);
			if (response.getStatusCode().equals(HttpStatus.NO_CONTENT)
					|| response.getStatusCode().equals(HttpStatus.OK)) {
				log.debug("Session close successfully");
				log.trace("Session close successfully");
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirect)).build();
			} else {
				log.error("Session close error response.status code: {}, server URL: {}", response.getStatusCode(),
						logoutUrl);
				log.error("Session close error response.status code: {}, server URL: {}", response.getStatusCode(),
						logoutUrl);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getBody());
			}
		} catch (HttpClientErrorException e) {
			log.error(
					"HttpClientErrorException invalidating token with SSO authorization server. response.status code: {}, server URL: {}",
					e.getStatusCode(), logoutUrl);
		}
		log.debug("Session close fail");
		log.trace("Session close fail ");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session close fail");
	}
	
	public void disconnecUserSessionLog(String username, ServerWebExchange exchange) {
		@SuppressWarnings("unused")
		String path = "/users/session-logs/disconnect";
		HttpHeaders requestHeaders = getHttpHeaders(exchange.getRequest().getHeaders());
		ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
		Locale locale = new Locale.Builder().setLanguage("en").build();
		if(request.getHeaders().getContentLanguage() != null) {				
			locale = request.getHeaders().getContentLanguage();
		}
		UserSessionLog userSL = new UserSessionLog();
		userSL.setUsername(username);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		requestHeaders.set("Accept-Language", locale.getLanguage());
	}
	
	private HttpHeaders getHttpHeaders(HttpHeaders requestHeader){
		HttpHeaders requestHeaders = new HttpHeaders();
		List<String> items = Arrays.asList("Bcephal-Project","Bcephal-Project-Name","bc-profile","bc-client","Authorization","Cookie");
		requestHeader.forEach((key,value)->{
			if(items.contains(key)) {
				requestHeaders.add(key, value.get(0));
			}
		});
		return requestHeaders;
	}

	public ResponseEntity<?> logout(OAuth2AuthorizedClient authorizedClient) {
		log.debug("Executing SsoLogoutHandler.logout");
		String accessToken = authorizedClient.getRefreshToken().getTokenValue();
		log.debug("token: {}", accessToken);
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set(OAuth2Constants.CLIENT_ID, authorizedClient.getClientRegistration().getClientId());
		params.set(OAuth2Constants.CLIENT_SECRET, authorizedClient.getClientRegistration().getClientSecret());
		params.set(OAuth2Constants.REFRESH_TOKEN, accessToken);
		params.set(OAuth2Constants.SCOPE, "offline_access");
		params.set("id_token_hint", accessToken);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/x-www-form-urlencoded");
		headers.add("Authorization", "bearer " + accessToken);
		HttpEntity<?> request = new HttpEntity<>(params, headers);
		String logoutUrl = getLogoutUrl(authorizedClient);
		FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
		HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
		restTemplate.setMessageConverters(
				Arrays.asList(new HttpMessageConverter[] { formHttpMessageConverter, stringHttpMessageConverternew }));
		return restTemplate.exchange(logoutUrl, HttpMethod.POST, request, String.class);
	}

	private String getLogoutUrl(OAuth2AuthorizedClient authorizedClient) {
		return authorizedClient.getClientRegistration().getProviderDetails().getConfigurationMetadata()
				.get("end_session_endpoint").toString();
	}
	
	private String getAccessTokenUrl() {
		return clientRegistration.getProviderDetails().getConfigurationMetadata()
				.get("token_endpoint").toString();
	}

	private String getPostLogoutUrl(ServerHttpRequest request) {
		String path = request.getURI().toString();
		return path.replace(request.getURI().getPath(), "/bcephal/");
	}

	@GetMapping("/state-id")
	public ResponseEntity<String> getStateId(WebSession webSession) {
		byte[] bytesEncoded = Base64.getEncoder().encode(webSession.getId().getBytes());
		String id = new String(bytesEncoded);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).header("state-id", id).build();
	}

	@Autowired
	ObjectMapper mapper;

	@GetMapping("/http-header")
	public ResponseEntity<String> getHttpSessionHeader(WebSession webSession, @RequestHeader HttpHeaders headers) {
		log.trace("Call get HttpSession Header for session: {}", webSession.getId());
		try {
			HttpHeaders requestHeaders = new HttpHeaders();
			headers.forEach((key, value) -> {
				if (key.contains("Cookie")) {
					requestHeaders.addAll(key, value);
				}
			});
			String header = mapper.writeValueAsString(requestHeaders);
			byte[] bytesEncoded = Base64.getEncoder().encode(header.getBytes());
			String headerString = new String(bytesEncoded);
			return ResponseEntity.ok(headerString);
		} catch (Exception e) {
			log.error("Unexpected error while computing new project name", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Unexpected error while HttpSession Header");
		}
	}
	
	
	
	@GetMapping("/scheduler-state-session")
	public ResponseEntity<String> getSchedulerHttpSessionHeader(WebSession webSession, @RequestHeader HttpHeaders headers,
																@RequestHeader("C-authorization") String pass) {
		log.trace("Call get HttpSession Header for session: {}", webSession.getId());
		try {
			if (StringUtils.hasText(pass)) {
				String pass_ = new String(Base64.getDecoder().decode(pass));
				String[] auth = pass_.split(":");
				if (auth != null && auth.length == 2) {
					String userName = auth[0];
					String password = auth[1];
					RestTemplate restTemplate = new RestTemplate();
					MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
					params.set(OAuth2Constants.CLIENT_ID, clientRegistration.getClientId());
					params.set(OAuth2Constants.CLIENT_SECRET,clientRegistration.getClientSecret());
					//params.set(OAuth2Constants.GRANT_TYPE, OAuth2Constants.ACCESS_TOKEN_TYPE);
					params.set(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
					//params.set(OAuth2Constants.SCOPE, "openid");
					params.set(OAuth2Constants.USERNAME, userName);
					params.set(OAuth2Constants.PASSWORD, password);
					HttpHeaders headers_ = new HttpHeaders();
					headers_.set("Content-Type", "application/x-www-form-urlencoded");
					headers_.set("Authorization", "Basic " + pass);
					HttpEntity<?> request = new HttpEntity<>(params, headers_);
					String accessTokenUrl = getAccessTokenUrl();
					FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
					HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
					
			        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = 
			          new OAuth2AccessTokenResponseHttpMessageConverter(); 
			        tokenResponseHttpMessageConverter.setAccessTokenResponseConverter(new CustomTokenResponseConverter()); 
					
			        restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter[] {formHttpMessageConverter, stringHttpMessageConverternew ,tokenResponseHttpMessageConverter}));
					
			        
					ResponseEntity<OAuth2AccessTokenResponse> response = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, request,OAuth2AccessTokenResponse.class);
					// boolean response = valid
					if (response.getStatusCode().equals(HttpStatus.OK)) {
						HttpHeaders requestHeaders = new HttpHeaders();
						headers.forEach((key, value) -> {
							if (key.contains("Cookie")) {
								requestHeaders.addAll(key, value);
							}
						});
						OAuth2AccessTokenResponse responToken = response.getBody();
						//headers.add("Authorization", "bearer " + responToken.getRefreshToken());
						requestHeaders.add("Authorization", "bearer " + responToken.getRefreshToken());
						String header = mapper.writeValueAsString(requestHeaders);
						byte[] bytesEncoded = Base64.getEncoder().encode(header.getBytes());
						String headerString = new String(bytesEncoded);
						return ResponseEntity.ok(headerString);
					}
				}
			}
			webSession.invalidate();
			webSession.changeSessionId();
			ReactiveSecurityContextHolder.clearContext();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (Exception e) {
			webSession.invalidate();
			webSession.changeSessionId();
			ReactiveSecurityContextHolder.clearContext();
			log.error("Unexpected error while computing new project name", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Unexpected error while HttpSession Header");
		}
	}

	
	public class CustomTokenResponseConverter implements  Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
	    @SuppressWarnings("unused")
		private final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream.of(
	        OAuth2ParameterNames.ACCESS_TOKEN, 
	        OAuth2ParameterNames.TOKEN_TYPE, 
	        OAuth2ParameterNames.EXPIRES_IN, 
	        OAuth2ParameterNames.REFRESH_TOKEN, 
	        OAuth2ParameterNames.SCOPE).collect(Collectors.toSet());

	    @Override
	    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
	        String accessToken = (String)tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
	        @SuppressWarnings("unused")
			String accessTokenType_ = (String)tokenResponseParameters.get(OAuth2ParameterNames.TOKEN_TYPE);
	        String expiresIn = (String)tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN);
	        String refreshToken = (String)tokenResponseParameters.get(OAuth2ParameterNames.REFRESH_TOKEN);
	        TokenType accessTokenType = TokenType.BEARER;

	        Set<String> scopes = Collections.emptySet();
	        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
	            String scope = (String)tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
	            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, ","))
	                .collect(Collectors.toSet());
	        }

	        return OAuth2AccessTokenResponse.withToken(accessToken)
	          .tokenType(accessTokenType)
	          .expiresIn(Long.valueOf(expiresIn))
	          .scopes(scopes)
	          .refreshToken(refreshToken)
	          //.additionalParameters(tokenResponseParameters)
	          .build();
	    }

	}
	
}
