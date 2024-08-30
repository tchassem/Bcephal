package com.moriset.bcephal.api.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {
	
	
	@Bean
    LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
		List<String> mathers = getAuthorizePathMathers();
		
		http.csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(exchange -> exchange
	        		.requestMatchers(mathers.toArray(new String[mathers.size()])).permitAll()
	        		.anyRequest().permitAll()
        		)
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }
    
    
	@LoadBalanced
	@Bean
	RestTemplate loadBalancedRestTemplate() {
		RestTemplate template = new RestTemplate();
		//addConverters(template);
		return template;
	}
	
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/swagger-ui", "/swagger/index.html");
        registry.addRedirectViewController("/swagger-ui/", "/swagger/index.html");
        registry.addRedirectViewController("/swagger-ui/index.html", "/swagger/index.html");
    }
	
	protected List<String> getAuthorizePathMathers() {
		return Arrays.asList("/api/security/login");
	}
}
