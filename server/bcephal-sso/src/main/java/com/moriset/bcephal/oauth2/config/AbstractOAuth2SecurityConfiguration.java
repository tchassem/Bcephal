package com.moriset.bcephal.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.DelegatingOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.RefreshTokenOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

public abstract class AbstractOAuth2SecurityConfiguration {

//	@Bean
//	  public OAuth2AuthorizedClientManager authorizedClientManager(
//	      ClientRegistrationRepository clientRegistrationRepository,
//	      OAuth2AuthorizedClientService authorizedClientService) {
//
//	    OAuth2AuthorizedClientProvider authorizedClientProvider =
//	        OAuth2AuthorizedClientProviderBuilder.builder()
//	            .clientCredentials()
//	            .build();
//
//	    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
//	        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
//	            clientRegistrationRepository, authorizedClientService);
//	    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//	    return authorizedClientManager;
//	  }
//	
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
//	    return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
//	}
//	
//	@Bean 
//	@ConditionalOnMissingBean
//	public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
//		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public ServerOAuth2AuthorizedClientRepository serverOAuth2AuthorizedClientRepository() {
//		return new WebSessionServerOAuth2AuthorizedClientRepository();
//	}
//	

//	@Bean
//	public OAuth2AuthorizedClientManager authorizedClientManager(
//	        ClientRegistrationRepository clientRegistrationRepository,
//	        OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//	    OAuth2AuthorizedClientProvider authorizedClientProvider =
//	            OAuth2AuthorizedClientProviderBuilder.builder()
//	                    .authorizationCode()
//	                    .refreshToken()
//	                    .clientCredentials()
//	                    .password()
//	                    .build();
//
//	      
//	    DefaultOAuth2AuthorizedClientManager authorizedClientManager =
//	            new DefaultOAuth2AuthorizedClientManager(
//	                    clientRegistrationRepository, authorizedClientRepository);
//	    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//	    return authorizedClientManager;
//	}

	@Bean
	protected OAuth2AuthorizedClientManager authorizedClientManager(
			ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository,
			OAuth2AuthorizedClientService authorizedClientService) {

		OAuth2AuthorizedClientProvider authorizedClientProvider = new DelegatingOAuth2AuthorizedClientProvider(
				new RefreshTokenOAuth2AuthorizedClientProvider(),
				new ClientCredentialsOAuth2AuthorizedClientProvider());

		AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
				clientRegistrationRepository, authorizedClientService);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
		return authorizedClientManager;
	}

}
