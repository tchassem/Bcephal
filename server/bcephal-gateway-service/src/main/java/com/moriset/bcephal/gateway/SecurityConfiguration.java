package com.moriset.bcephal.gateway;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponents;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfiguration {

	@Autowired
	CustomServerAuthenticationSuccessHandler CustomServerAuthenticationSuccessHandler;

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
			ReactiveClientRegistrationRepository clientRegistrationRepository) {
		
		List<String> mathers = new ArrayList<>(Arrays.asList("/actuator/**", "/ws-js/**", "/rest-session", "/assets/**"));
		List<String> otherMathers = getAuthorizePathMathers();
		if(otherMathers != null && otherMathers.size() > 0) {
			mathers.addAll(otherMathers);
		}
		
		http.cors(Customizer.withDefaults()).oauth2Login(Customizer.withDefaults());	
		http.cors(Customizer.withDefaults()).authorizeExchange((exchanges) -> exchanges
				.pathMatchers(mathers.toArray(new String[mathers.size()]))
				.permitAll().anyExchange().authenticated()
        );
		http.headers((headers) -> headers				
				.frameOptions((options) -> options.mode(Mode.SAMEORIGIN))
        );		
		http.cors(Customizer.withDefaults());
		http.csrf((csrf) -> csrf.disable());
		http.cors(Customizer.withDefaults()).logout((logout) -> logout.disable());

		return http.build();
	}
	
	
	
	protected List<String> getAuthorizePathMathers() {
		return Arrays.asList();
	}
	
	@Autowired
	OAuth2ResourceServerProperties properties;


    @Bean
    ReactiveJwtDecoder jwtDecoder() {		
		String issuerUri = properties.getJwt().getIssuerUri();
		NimbusReactiveJwtDecoder jwtDecoder = (NimbusReactiveJwtDecoder) ReactiveJwtDecoders
				.fromIssuerLocation(issuerUri);
		jwtDecoder.setClaimSetConverter(new UsernameSubClaimAdapter());
		return jwtDecoder;
	}
	
	
	

	class UsernameSubClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {
		private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter
				.withDefaults(Collections.emptyMap());

		@Override
		public Map<String, Object> convert(Map<String, Object> claims) {
			Map<String, Object> convertedClaims = this.delegate.convert(claims);
			String username = (String) convertedClaims.get("preferred_username");
			convertedClaims.put("username", username);
			convertedClaims.put("iss", convertedClaims.get("iss"));
			convertedClaims.put("sub", convertedClaims.get("sub"));
			convertedClaims.put("aud", convertedClaims.get("aud"));
			convertedClaims.put("iat", convertedClaims.get("iat"));
			convertedClaims.put("jti", convertedClaims.get("jti"));
			convertedClaims.put("sid", convertedClaims.get("sid"));
			convertedClaims.put("session_state", convertedClaims.get("session_state"));
			return convertedClaims;
		}
	}

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(true);
		corsConfig.addAllowedOriginPattern(CorsConfiguration.ALL);
		corsConfig.setMaxAge(8000L);
		corsConfig.addAllowedMethod(CorsConfiguration.ALL);
		corsConfig.addAllowedHeader(CorsConfiguration.ALL);
		corsConfig.addExposedHeader(HttpHeaders.SET_COOKIE);
		corsConfig.addExposedHeader(HttpHeaders.SET_COOKIE);
		corsConfig.addExposedHeader(HttpHeaders.CONTENT_DISPOSITION);
		corsConfig.addExposedHeader("fileName");
		corsConfig.addExposedHeader("Filename");
		corsConfig.addExposedHeader("filename");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

	@Component
	public class CookiesGatewayFilter implements Ordered, GlobalFilter {

		@Override
		public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
			ServerHttpRequest req = exchange.getRequest();
			ServerHttpRequest request = exchange.getRequest().mutate().headers((httpHeaders) -> {
				List<String> cooks = httpHeaders.get(HttpHeaders.COOKIE);
				String cId = (HttpHeaders.COOKIE + "__").toLowerCase();
				List<String> cooks_ = httpHeaders.get(cId);
				if (cooks != null && cooks.size() > 0 && (cooks_ == null || cooks_.size() == 0)) {
					httpHeaders.set(cId, cooks.get(0));
					String host = req.getURI().getHost();
					String port = req.getURI().getPort() + "";	
					if (host.equals("0:0:0:0:0:0:0:1") || host.equals("localhost") || host.equals("127.0.0.1") || req.getLocalAddress().getAddress().isLoopbackAddress()) {
						InetAddress localip;
						try {
							localip = java.net.InetAddress.getLocalHost();
							host = localip.getHostAddress();
						} catch (UnknownHostException ex) {
							ex.printStackTrace();
							log.error("Error: {}", ex);
						}						
					}
					UriComponents uriComponents2 = null;
					uriComponents2 = ForwardedHeaderUtils.adaptFromForwardedHeaders(req.getURI(), req.getHeaders())
							.host(host).port(port).build();
					log.trace("Host Address: {}", uriComponents2);
					log.trace("Host remote_uri: {}", exchange.getRequest().getURI().toString());
					httpHeaders.set("remote_address", uriComponents2.toUri().toString());
					httpHeaders.set("remote_uri", exchange.getRequest().getURI().toString());
				}
			}).build();
			return chain.filter(exchange.mutate().request(request).build());
		}

		@Override
		public int getOrder() {
			return -100;
		}

	}
}
