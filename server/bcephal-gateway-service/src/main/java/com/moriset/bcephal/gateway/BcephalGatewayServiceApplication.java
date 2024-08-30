package com.moriset.bcephal.gateway;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@SpringBootApplication(
		scanBasePackages = {"com.moriset.bcephal.*" },
		exclude = {SecurityAutoConfiguration.class}
)
//@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class})
//org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class 
@EnableDiscoveryClient
@Slf4j
@RestController
public class BcephalGatewayServiceApplication {
	
	private static final String CONNECTION_DONE = "B-CEPHAL_CONNECTION_DONE";
	private static final String BCEPHAL_USER_AGENT = "B-CEPHAL_CLIENT";
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BcephalGatewayServiceApplication.class, args);
		log.info("Gateway service started!");
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() throws Exception {
		String path = Paths.get(licenseFolder, licenseFileName).toString();
		log.trace("License path : {}", path);
		if(LicenseUtil.validate(path)) {
			log.trace("License is valid!");
		}
		else {
			log.error("Incalid license!");
			System.exit(-1);
		}
	}
	
	@Value("${bcephal.license-resource-folder}")
	String licenseFolder;
	
	@Value("${bcephal.license-file-name}")
	String licenseFileName;
	
	
	@GetMapping("/hystrixfallback")
	public String hystrixfallback() {
		return "This is a fallback";
	}
	
	public Mono<ServerResponse> connexion(ServerRequest request) {
		List<String> userAgent = request.headers().header("user-agent");
		if(userAgent != null && userAgent.size() > 0 
				&& userAgent.get(0).contains(BCEPHAL_USER_AGENT)) {	
			return  ServerResponse.ok().bodyValue(CONNECTION_DONE);
		}
		else {
			ServerHttpRequest req = request.exchange().getRequest();
			UriComponents uriComponents2 = ForwardedHeaderUtils.adaptFromForwardedHeaders(req.getURI(), req.getHeaders())
				.path("bcephal").build().encode();
			
			return  ServerResponse.permanentRedirect(uriComponents2.toUri()).build();
		}		
	}
	
	public Mono<ServerResponse> favicon(ServerRequest request) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
		.scheme(request.uri().getScheme())
		.host(request.uri().getHost())
		.port(request.uri().getPort())
		.path("bcephal/favicon.ico").build().encode();
		return  ServerResponse.permanentRedirect(uriComponents.toUri()).build();	
	}
	
}
