/**
 * 
 */
package com.moriset.bcephal.reporting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.moriset.bcephal.config.RestConfig;
import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;

@Configuration
@EnableWebSecurity
@Import(RestConfig.class)
public class SecurityConfiguration extends AbstractSecurityConfiguration {

}
