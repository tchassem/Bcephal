package com.moriset.bcephal.gateway;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import com.moriset.bcephal.gateway.domain.UserSessionLog;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


/* 
 * RedirectServerAuthenticationSuccessHandler
 * WebFilterChainServerAuthenticationSuccessHandler
 * */
@Component
@Slf4j
public class CustomServerAuthenticationSuccessHandler  extends  RedirectServerAuthenticationSuccessHandler {
		 
	@Autowired
	RestTemplate restTemplate;
		
	@Autowired
	ReactiveOAuth2AuthorizedClientManager auth2AuthorizedClientManager;
	

	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
		log.debug("Call Server Authentication Success Handler: user {}, isAuthenticated {},", authentication.getName(), authentication.isAuthenticated());
		new Thread(() -> {
		try {			
			saveUserConnectedSession(webFilterExchange, authentication);
		}catch (Exception e) {
			log.error("Unable to save UserSessionLog", e);
		}}).start();
        return super.onAuthenticationSuccess(webFilterExchange, authentication);
    }
	
	@SuppressWarnings("unchecked")
	private void saveUserConnectedSession(WebFilterExchange webFilterExchange, Authentication authentication) {
			String path = "/users/session-logs/save-user-sesion-log";
			log.debug("Try to save user session log {}");
			HttpHeaders requestHeaders = getHttpHeaders(webFilterExchange.getExchange().getRequest().getHeaders());
			ServerHttpRequest request = (ServerHttpRequest) webFilterExchange.getExchange().getRequest();
			
			getAuthorizationHeader(authentication,webFilterExchange.getExchange())
			 .flatMap(token_->{
				 String session = "";
				 Locale locale = new Locale.Builder().setLanguage("en").build();
				 if(request.getCookies() != null) {		
						log.trace("get Cookies {}",request.getCookies());
						HttpCookie cookie = request.getCookies().getFirst("JSESSIONID");
						if(cookie != null) {	
							log.trace("Try get JSESSIONID ");
							session = cookie.getValue();
							log.trace("End  JSESSIONID {}",session);
						}
					}
				 if(request.getHeaders().getContentLanguage() != null) {				
						locale = request.getHeaders().getContentLanguage();
					}
				Optional<String> token = (Optional<String>)token_;
				 token.ifPresent(t -> requestHeaders.set("Authorization", "Bearer " + t));
					log.trace("Get language: {}, Get token: {}", locale.getLanguage(), token.get());
					UserSessionLog userSL = new UserSessionLog();
					userSL.setUsername(authentication.getName());
					userSL.setUsersession(session);
					requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
					requestHeaders.set("Accept-Language", locale.getLanguage());
			        HttpEntity<UserSessionLog> entity = new HttpEntity<UserSessionLog>(userSL, requestHeaders);
			        ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.POST, entity,String.class);
			        if (response.getStatusCode() != HttpStatus.OK) {
						log.error("Unable to save UserSessionLog authentication: {} error : {} {} Status : {}", authentication, requestHeaders, response, response.getStatusCode());
					}
			        return null;
			 });
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
	
//	private Mono<Object> getAuthorizationHeader(Authentication authentication) {
//		OAuth2AuthorizeRequest authorizedRequest = OAuth2AuthorizeRequest
//				.withClientRegistrationId("keycloak")
//				.principal(authentication).build();
//	   Mono<OAuth2AuthorizedClient> resutl = auth2AuthorizedClientManager.authorize(authorizedRequest);
//	  return resutl.flatMap(client ->{		
//				
//			return	getAuthorizationHeader(client);
//			});
//	}
	
	private Mono<Object> getAuthorizationHeader(Authentication authentication, ServerWebExchange exchange) {
		OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
				.principal(authentication)
				.attribute(ServerWebExchange.class.getName(), exchange)
				.build();
		return this.auth2AuthorizedClientManager.authorize(authorizeRequest)
				.map(OAuth2AuthorizedClient::getAccessToken)
				.map(OAuth2AccessToken::getTokenValue);
	}
	
	@SuppressWarnings("unused")
	private Mono<Optional<String>> getAuthorizationHeader(OAuth2AuthorizedClient authrizedClient) {
		OAuth2AccessToken accessToken = Objects.requireNonNull(authrizedClient).getAccessToken();
		if(accessToken == null) {
			log.error("AuthAccessToken is null");
			return Mono.justOrEmpty(Optional.empty());
		} else {
			log.trace("Get authAccessToken: {}", accessToken);
			String tokenType = accessToken.getTokenType().getValue();
			String authorizationHeaderValue = String.format("%s %s", tokenType, accessToken.getTokenValue());
			log.debug("Token-type: {}, AuthorizationHeaderValue: {}", authorizationHeaderValue);
			return Mono.just(Optional.of(authorizationHeaderValue));
		}
	}
	
}
