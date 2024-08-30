package com.moriset.bcephal.gateway;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.moriset.bcephal.gateway.domain.Auth2AdminUser;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class KeycloakServices /*implements WebFilter*/{

    @Bean
    RouterFunction<ServerResponse> editUser(RestPasswordHandler restPasswordHandler) {
        return RouterFunctions
            .route(RequestPredicates.GET("/edit-user")
            		.and(RequestPredicates.accept(MediaType.ALL)), restPasswordHandler::restPassword);
    }

    @Bean
    RouterFunction<ServerResponse> listUsers(RestPasswordHandler restPasswordHandler) {
        return RouterFunctions
            .route(RequestPredicates.GET("/list-users")
            		.and(RequestPredicates.accept(MediaType.ALL)), restPasswordHandler::userList);
    }


    @Bean
    RouterFunction<ServerResponse> connexion(BcephalGatewayServiceApplication application) {
        return RouterFunctions
            .route(RequestPredicates.GET("").or(RequestPredicates.GET("/"))
            		.and(RequestPredicates.accept(MediaType.ALL)), application::connexion);
    }

    @Bean
    RouterFunction<ServerResponse> favicon(BcephalGatewayServiceApplication application) {
        return RouterFunctions
            .route(RequestPredicates.GET("/favicon.ico")
            		.and(RequestPredicates.accept(MediaType.ALL)), application::favicon);
    }

    @Bean
    RouterFunction<ServerResponse> resetSession(SessionCloseHandler sessionCloseHandler) {
        return RouterFunctions
            .route(RequestPredicates.GET("/rest-session")
            		.and(RequestPredicates.accept(MediaType.ALL)), sessionCloseHandler::resetSession);
    }

    @Bean
    ClientResponseFilter_ clientResponseFilter_() {
		return new  ClientResponseFilter_();
	}
	
	protected class ClientResponseFilter_ implements ClientResponseFilter
    {
		ClientResponseContext responseContext;
		@Override
		public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
			log.info("Keycloak admins cli responseContext Cookies : {}",responseContext.getCookies());
			log.info("Keycloak admins cli responseContext Headers : {}",responseContext.getHeaders());
			this.responseContext = responseContext;			
		}
	}
	
	@Component
	public class RestPasswordHandler {
		@Value("${keycloak.realm}")
		private String realm;
		
		@Value("${keycloak.resource}")
		private String clientId;
		
		@Value("${keycloak.auth-server-url}")
		private String server_root_url;
		
		
		@Autowired
		Auth2AdminUser auth2AdminUser;
		
		@Autowired
		ClientResponseFilter_ clientResponseFilter_;
		
		public Keycloak getKeycloak(Client clientTarget) {
			Keycloak keycloak = KeycloakBuilder.builder()
					.serverUrl(server_root_url)
					.grantType(OAuth2Constants.PASSWORD)
					.realm("master")
					.clientId("admin-cli")
					.username(auth2AdminUser.getUserName())
					.password(auth2AdminUser.getUserPassword())
					.resteasyClient(clientTarget)
					.build();
			//keycloak.tokenManager().getAccessToken();
			return keycloak;
		}
		
	    public Mono<ServerResponse> restPassword(ServerRequest request) {
	 		  String url = server_root_url + "/realms/" + realm + "/account";
	    	String redirectUri = request.uri().toString();
	    	redirectUri = redirectUri.replace(request.path(), "");
	    	URI accountLinkUrl = UriBuilder.fromUri(url)
					   .queryParam("nonce", "")
	                   .queryParam("hash", "")
	                   .queryParam("client_id", clientId)
	                   .queryParam("redirect_uri", redirectUri).build();
	    	return  ServerResponse.permanentRedirect(accountLinkUrl).build();
	    }
	    
	  
	    
	    public Mono<ServerResponse> userList(ServerRequest request) {
	    	ResteasyClient clientTarget = new ResteasyClientBuilderImpl()
			.connectionPoolSize(10)
			.register(clientResponseFilter_)
			.build();
	    	Keycloak keycloak = getKeycloak(clientTarget);
	    	RealmResource realmResource = keycloak.realm(realm);
			ClientsResource client = realmResource.clients();
			//return request.session().flatMap(session -> {
				String url = server_root_url + "/admin/master/console/#/realms/" + realm + "/sessions/realm";				 
			if(client != null && StringUtils.hasText(clientId)) {
				List<ClientRepresentation> clients = client.findByClientId(clientId);
				if(clients != null && clients.size() > 0) {
					String id = clients.get(0).getId();
					url = server_root_url + "/admin/master/console/#/realms/" + realm + "/clients/" + id + "/sessions";
				}
			}
			 String token = keycloak.tokenManager().getAccessTokenString();
	    	try {
				URI usersLinkUrl = new URI(url);
				//session.changeSessionId();
				//ReactiveSecurityContextHolder.clearContext();
				return  ServerResponse.permanentRedirect(usersLinkUrl)
	    			.headers(headers ->headers.setBearerAuth(token))
	    			//.headers(this::setBearerAuth)
//	    			.cookies(cookies ->{
//	    				cookies.add(token, keycloak);
//	    				
//	    			})
	    			.build();
	    	} catch (URISyntaxException e) {
	    		return  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			// });
	    }
	    
	    @Path("/")
	    public interface LibraryService {

	       @GET
	       @Produces(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	       @Consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	       Response get();
	    }
	    
	    public class MethodWrapper {

	        private Method method;

	        public Method getMethod() {
	            return method;
	        }

	        public void setMethod(Method method) {
	            this.method = method;
	        }
	    }
//	    
//	    public Mono<ServerResponse> userList2(ServerRequest request) {
//	    	RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
//	    	
//	    	ResteasyClient clientTarget = new ResteasyClientBuilder()
//			.connectionPoolSize(10)
//			.register(clientResponseFilter_)
//			.register(request)
//			.build();
//	    	Keycloak keycloak = getKeycloak(clientTarget);
//	    	RealmResource realmResource = keycloak.realm(realm);
//			String url = server_root_url + "/admin/master/console/#/realms/" + realm + "/sessions/realm";
//			ClientsResource client = realmResource.clients();
//			
//			if(client != null && StringUtils.hasText(clientId)) {
//				List<ClientRepresentation> clients = client.findByClientId(clientId);
//				if(clients != null && clients.size() > 0) {
//					String id = clients.get(0).getId();
//					url = server_root_url + "/admin/master/console/#/realms/" + realm + "/clients/" + id + "/sessions";
//				}
//			}
//			 String token = keycloak.tokenManager().getAccessTokenString();
//	    	try {
//				URI usersLinkUrl = new URI(url);
////				Response r = clientTarget.target(usersLinkUrl).request().get();
//				LibraryService libraryService = clientTarget.target(usersLinkUrl).proxy(LibraryService.class);
//				
//				MethodWrapper wrapper = new MethodWrapper();
//
//				ProxyFactory proxyFactory = new ProxyFactory(libraryService);
//				proxyFactory.addAdvice(new MethodInterceptor() {
//				    @Override
//				    public Object invoke(MethodInvocation invocation) throws Throwable {
//				        wrapper.setMethod(invocation.getMethod());
//				        return invocation.proceed();
//				    }
//				});
//				Response r = libraryService.get();
//		        MultivaluedMap<String, Object> headers_ = r.getHeaders();
//		        log.info("headers: {}" , headers_);
//				return ServerResponse.ok().contentType(MediaType.TEXT_HTML).headers(headers -> {
//					headers_.forEach((k, v) -> {
//						if (v != null && !k.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
//							if (v instanceof List<Object>) {
//								List<String> its = new ArrayList<>(0);
//								for (Object ob : (List<Object>) v) {
//									its.add(ob.toString());
//								}
//								headers.set(k, String.join(";", its).replace("[", "").replace("]", ""));
//							} else {
//								headers.set(k, v.toString());
//							}
//							log.info("headers: k {}, v {}" , k,v);
//						}
//					});
//				}).headers(headers ->headers.setBearerAuth(token)).bodyValue(r.readEntity(String.class));
////				 return ServerResponse.status(HttpStatus.PERMANENT_REDIRECT)
////						 .location(usersLinkUrl)
////						 .headers(headers ->headers.setBearerAuth(token))
////						 .build();
//	    	} catch (URISyntaxException e) {
//	    		return  ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//			}
//	    }
//	    
//	    private void setBearerAuth(HttpHeaders headers) {
////	    	String token = getKeycloak().tokenManager().getAccessTokenString();
////	    	headers.setBearerAuth(token);
//	    }
	    
//	    public ResponseEntity<?> logout(OAuth2AuthorizedClient authorizedClient) {
//			log.debug("Executing SsoLogoutHandler.logout");
//			String accessToken = authorizedClient.getRefreshToken().getTokenValue();
//			log.debug("token: {}", accessToken);
//			RestTemplate restTemplate = new RestTemplate();
//			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//			params.set(OAuth2Constants.CLIENT_ID, authorizedClient.getClientRegistration().getClientId());
//			params.set(OAuth2Constants.CLIENT_SECRET, authorizedClient.getClientRegistration().getClientSecret());
//			params.set(OAuth2Constants.REFRESH_TOKEN, accessToken);
//			params.set(OAuth2Constants.SCOPE, "offline_access");
//			params.set("id_token_hint", accessToken);
//			HttpHeaders headers = new HttpHeaders();
//			headers.set("Accept", "application/x-www-form-urlencoded");
//			headers.add("Authorization", "bearer " + accessToken);
//			HttpEntity<?> request = new HttpEntity<>(params, headers);
//			String logoutUrl = getLogoutUrl(authorizedClient);
//			FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//			HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
//			restTemplate.setMessageConverters(
//					Arrays.asList(new HttpMessageConverter[] { formHttpMessageConverter, stringHttpMessageConverternew }));
//			return restTemplate.exchange(logoutUrl, HttpMethod.POST, request, String.class);
//		}
	    
	}
	
	
	
	@Component
	public class SessionCloseHandler {	
		
		//GET /reset-session
	    public Mono<ServerResponse> resetSession(ServerRequest request) {
	 		//String url = request.requestPath().value();
	    	String redirectUri = request.uri().toString();
	    	redirectUri = redirectUri.replace(request.path(), "");
	    	URI linkUrl = UriBuilder.fromUri(redirectUri).build();
	    	return  ServerResponse.permanentRedirect(linkUrl).build();
	    }
	}
	
}
