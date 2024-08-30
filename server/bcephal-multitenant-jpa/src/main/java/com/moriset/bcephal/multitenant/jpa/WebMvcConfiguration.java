package com.moriset.bcephal.multitenant.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	
	@Autowired
	MultiTenantInterceptor multiTenantInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	 	registry.addInterceptor(multiTenantInterceptor);
	}

}
