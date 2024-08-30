package com.moriset.bcephal.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestConfig {

	@Value("${gateway-host}")
	private String gatewayHost;
	@Value("${gateway-port}")
	private String gatewayPort;

	@Value("${gateway-protocol}")
	private String gatewayProtocol;

	private String gatewayUrl() {
		String proto = gatewayProtocol;
		if (!StringUtils.hasText(proto)) {
			proto = "http";
		}
		return proto + "://" + gatewayHost + ":" + gatewayPort;
	}

	@Bean
	RestTemplate restTemplate() {
		RestTemplate template = new RestTemplate();
		if (gatewayProtocol.startsWith("https")) {
			try {
				template = new RestTemplate(getRequestFactory());
			} catch (Exception e) {
			}
		}
		template.setUriTemplateHandler(new DefaultUriBuilderFactory(gatewayUrl()));
		addConverters(template);
		return template;
	}

	@LoadBalanced
	@Bean
	RestTemplate loadBalancedRestTemplate() {
		RestTemplate template = new RestTemplate();
		addConverters(template);
		return template;
	}

	private HttpComponentsClientHttpRequestFactory getRequestFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, (hostname, session) -> true);

		PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
				.setSSLSocketFactory(csf).build();

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return requestFactory;
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
