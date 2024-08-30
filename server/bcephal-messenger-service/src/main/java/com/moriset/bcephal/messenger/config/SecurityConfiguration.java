/**
 * 
 */
package com.moriset.bcephal.messenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;

@Configuration
@EnableWebSecurity
//@Import(RestConfig.class)
public class SecurityConfiguration extends AbstractSecurityConfiguration implements WebMvcConfigurer {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers("/bcephal-messenger/resources/**");
		web.ignoring().requestMatchers("/bcephal-messenger/resources/static/**");
		web.ignoring().requestMatchers("/bcephal-messenger/webjars/**");
		web.ignoring().requestMatchers("/bcephal-messenger/css/**");
		web.ignoring().requestMatchers("/bcephal-messenger/jolokia/**");
		web.ignoring().requestMatchers("/bcephal-messenger/hawtio/**");
		web.ignoring().requestMatchers("/bcephal-management-messenger/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
//	    http.securityMatcher("/bcephal-management-messenger/**")
//	    .formLogin(Customizer.withDefaults())
//	    .httpBasic(Customizer.withDefaults())
//	    .csrf(auth -> auth.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
//	    
//	    http.authorizeHttpRequests(auth -> auth.requestMatchers("/bcephal-management-messenger/**"))
//	    .csrf(auth -> auth.ignoringRequestMatchers(EndpointRequest.to("/actuator/**")));
	}
}
