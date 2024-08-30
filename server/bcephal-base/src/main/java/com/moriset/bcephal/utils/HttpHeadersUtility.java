package com.moriset.bcephal.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.multitenant.jpa.TenantContext;

public class HttpHeadersUtility {	
	
	public static HttpHeaders getHttpHeaders(RestTemplate restTemplate, HttpHeaders httpHeaders, Locale locale){
		HttpHeaders requestHeaders = new HttpHeaders();
		List<String> items = Arrays.asList("bc-profile","bc-client","authorization","cookie", HttpHeaders.AUTHORIZATION, HttpHeaders.COOKIE.toLowerCase(), HttpHeaders.COOKIE);
		String remoteAddress = "remote_address";
		List<String> remoteAddressValue = null;
		remoteAddressValue = httpHeaders.get(remoteAddress);
		httpHeaders.forEach((key,value)->{
			if(items.contains(key)) {
				requestHeaders.add(key, value.get(0));
			}
		});
		requestHeaders.set(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());
		requestHeaders.set(RequestParams.LANGUAGE, locale.getLanguage());
		if(remoteAddressValue != null && remoteAddressValue.size() > 0) {
			UriComponents uriComponents;
			try {
				uriComponents = UriComponentsBuilder.newInstance()
						.uri(new URI(remoteAddressValue.get(0))).build();
				restTemplate.setUriTemplateHandler(
						  new DefaultUriBuilderFactory(String.format("%s://%s:%s", uriComponents.getScheme(),uriComponents.getHost(),uriComponents.getPort())));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return requestHeaders;
	}

}
