/**
 * 
 */
package com.moriset.bcephal.oauth2.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Joseph Wambo
 *
 */
@Configuration
@EnableWebSecurity
//@Import(RefreshTokenFilterConfig.class)
public abstract class AbstractSecurityConfiguration {

	protected void configure(HttpSecurity http) throws Exception {
		
		List<String> mathers = new ArrayList<>(Arrays.asList("/actuator/**", "/ws-js/**"));
		List<String> otherMathers = getAuthorizePathMathers();
		if(otherMathers != null && otherMathers.size() > 0) {
			mathers.addAll(otherMathers);
		}
		
		http.oauth2Client(Customizer.withDefaults())
				.authorizeHttpRequests((exchanges) -> exchanges.requestMatchers(mathers.toArray(new String[mathers.size()])).permitAll()
						.anyRequest().authenticated())
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
		http.headers((headers) -> headers.frameOptions((options) -> options.sameOrigin()));
		http.csrf((csrf) -> csrf.disable());
	}


	protected List<String> getAuthorizePathMathers() {
		return new ArrayList<>();
	}
	
	@Bean
	protected WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> {
			try {
				configure(web);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	public void configure(WebSecurity web) throws Exception {

	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		configure(http);
		return http.build();
	}

	JwtAuthenticationConverter grantedAuthoritiesExtractor() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
		return jwtAuthenticationConverter;
	}

	class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
		@SuppressWarnings("unchecked")
		@Override
		public Collection<GrantedAuthority> convert(Jwt jwt) {
			final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
			return ((List<String>) realmAccess.get("roles")).stream().map(roleName -> "ROLE_" + roleName)
					.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		}
	}

//	  @Bean
//	  public JwtDecoder  jwtDecoderByIssuerUri(OAuth2ResourceServerProperties properties) {
//	    String issuerUri = properties.getJwt().getIssuerUri();
//	    NimbusJwtDecoder  jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
//	 //   OAuth2TokenValidator<Jwt> audienceValidator = new JwtTimestampValidator(Duration.ofSeconds(2));
//	   // OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
//	   // OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
//	   // OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer);
//	    //jwtDecoder.setJwtValidator(withAudience);
//	    jwtDecoder.setClaimSetConverter(new UsernameSubClaimAdapter());
//	    return jwtDecoder;
//	  }

	@Bean
	protected JwtDecoder jwtDecoderByIssuerUri(OAuth2ResourceServerProperties properties) {
		String issuerUri = properties.getJwt().getIssuerUri();
		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
		jwtDecoder.setClaimSetConverter(new UsernameSubClaimAdapter());
		return jwtDecoder;
	}

//	@Bean
//	  public JwtDecoder  jwtDecoderByIssuerUri(OAuth2ResourceServerProperties properties, RestTemplateBuilder builder) {
//	    String jwkSetUri = properties.getJwt().getJwkSetUri();
//	    RestOperations rest = builder
//	            .setConnectTimeout(Duration.ofSeconds(60))
//	            .setReadTimeout(Duration.ofSeconds(60))
//	            .build();
//	    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).restOperations(rest).build();
//	    jwtDecoder.setClaimSetConverter(new UsernameSubClaimAdapter());
//	    return jwtDecoder;
//	  }

	class UsernameSubClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {
		private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter
				.withDefaults(Collections.emptyMap());

		@Override
		public Map<String, Object> convert(Map<String, Object> claims) {
			Map<String, Object> convertedClaims = this.delegate.convert(claims);
			String username = (String) convertedClaims.get("preferred_username");
			convertedClaims.put("sub", username);
			return convertedClaims;
		}
	}

}
