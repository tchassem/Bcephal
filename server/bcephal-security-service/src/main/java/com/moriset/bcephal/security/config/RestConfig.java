package com.moriset.bcephal.security.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@Import(com.moriset.bcephal.config.RestConfig.class)
public class RestConfig {
	
	public static final String SSO_REST_TEMPLATE_ADMIN = "SSO_REST_TEMPLATE_ADMIN";
	public static final String SSO_REST_TEMPLATE_CONNECT = "SSO_REST_TEMPLATE_CONNECT";

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.realm}")
	private String realm;

	private String getUrl() {		
		return authServerUrl.concat("/admins/realms").concat(realm);
	}
	
	private String getConnectUrl() {		
		return authServerUrl.concat("/realms/master/protocol/openid-connect");
	}
	
	

	@Bean(SSO_REST_TEMPLATE_ADMIN)
	RestTemplate restTemplate() {
		RestTemplate template = new RestTemplate();
		template.setUriTemplateHandler(new DefaultUriBuilderFactory(getUrl()));
		addConverters(template);
		return template;
	}
	
	@Bean(SSO_REST_TEMPLATE_CONNECT)
	RestTemplate restTemplateConnect() {
		RestTemplate template = new RestTemplate();
		template.setUriTemplateHandler(new DefaultUriBuilderFactory(getConnectUrl()));
		addConverters(template);
		return template;
	}

	protected void addConverters(RestTemplate restTemplate) {
		ArrayList<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(
				Arrays.asList(new MappingJackson2HttpMessageConverter(), new ResourceHttpMessageConverter(),
						new FormHttpMessageConverter(), new ByteArrayHttpMessageConverter(),
						new StringHttpMessageConverter(), new BufferedImageHttpMessageConverter()));
		restTemplate.getMessageConverters().addAll(converters);
		return;
	}
}
