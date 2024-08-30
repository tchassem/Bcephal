package com.moriset.bcephal.project.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;

@TestConfiguration
@Order(1)
public class TestSecurityConfig extends AbstractSecurityConfiguration {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {    	
    	httpSecurity.csrf((csrf) -> csrf.disable()).authorizeHttpRequests((req) -> req.anyRequest().permitAll());        
    }
}
