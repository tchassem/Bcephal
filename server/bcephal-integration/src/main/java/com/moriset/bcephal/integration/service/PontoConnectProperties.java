package com.moriset.bcephal.integration.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "ibanity")
@Data
public class PontoConnectProperties {
	private String apiEndpoint;
	private String sandboxAuthorizationUrl; 
}
