/**
 * 9 janv. 2024 - TestSecurityConfiguration.java
 * 
 */
package com.moriset.bcephal.reporting.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;

/**
 * @author Emmanuel Emmeni
 *
 */
@TestConfiguration
@Order(1)
public class TestSecurityConfiguration extends AbstractSecurityConfiguration {
	
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {    	
    	httpSecurity.csrf((csrf) -> csrf.disable()).authorizeHttpRequests((req) -> req.anyRequest().permitAll());        
    }

}
