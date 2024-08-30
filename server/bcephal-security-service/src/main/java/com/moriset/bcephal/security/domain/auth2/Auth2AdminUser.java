package com.moriset.bcephal.security.domain.auth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "bcephal.keycloak.admin")
@Data
public class Auth2AdminUser {

	private String userName;
	private String userPassword;
}
