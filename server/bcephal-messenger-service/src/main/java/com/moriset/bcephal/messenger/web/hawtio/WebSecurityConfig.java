package com.moriset.bcephal.messenger.web.hawtio;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;

@ConditionalOnMissingBean(value = AbstractSecurityConfiguration.class)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers("/resources/**");
		web.ignoring().requestMatchers("/resources/static/**");
		web.ignoring().requestMatchers("/webjars/**");
		web.ignoring().requestMatchers("/css/**");
		web.ignoring().requestMatchers("/jolokia/**");
		web.ignoring().requestMatchers("/hawtio/**");
	}

	@Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
        	try {
    			configure(web);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        };
    }
	
	 @Bean
     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {		
		    configure(http);
         return http.build();
     }
	 
	protected void configure(HttpSecurity http) throws Exception {
		
//	    http.authorizeHttpRequests(auth -> auth.anyRequest().fullyAuthenticated())
//	    .formLogin(Customizer.withDefaults())
//	    .httpBasic(Customizer.withDefaults())
//	    .csrf(auth -> auth.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));	    
//	    http.csrf(auth -> auth.ignoringRequestMatchers(EndpointRequest.to("/actuator/**")));
	}

}
