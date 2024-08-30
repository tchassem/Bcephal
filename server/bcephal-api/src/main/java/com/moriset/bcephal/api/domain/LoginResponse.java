package com.moriset.bcephal.api.domain;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
	
	private static String SEPARATOR = ";#;:";
	
	private String sessionID;
	
	private Long clientId;
	
	private String projectCode;
	
	private Long profileId;
	
	public LoginResponse(String token) {
		decrypt(token);
	}
	
	
	@JsonIgnore
	public String getToken() {
		return encrypt();
	}
	
	public String encrypt() {
		String token = buildStringPart(sessionID) + SEPARATOR 
				 + buildStringPart(String.valueOf(clientId)) + SEPARATOR
				 + buildStringPart(projectCode) + SEPARATOR
				 + buildStringPart(String.valueOf(profileId));
		
		return Base64.getEncoder().encodeToString(token.getBytes());
	}
	
	public void decrypt(String token) {
		String tokenDecode = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
		String[] values = tokenDecode.split(SEPARATOR);
		sessionID = values.length > 0 ? values[0].trim() : null;
		clientId = values.length > 1 ? Long.parseLong(values[1].trim()) : null;
		projectCode = values.length > 2 ? values[2].trim() : null;
		profileId = values.length > 3 ? Long.parseLong(values[3].trim()) : null;
	}
	
	
	private String buildStringPart(String value) {		
		return StringUtils.hasText(value) ? value : " ";
	}
	
}
