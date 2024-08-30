/**
 * 
 */
package com.moriset.bcephal.license.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends AbstractSecurityConfiguration {
	
}
