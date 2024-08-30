package com.moriset.bcephal.security.controller.api;

import lombok.Data;

@Data
public class LoginResponse {
	
	private Long clientId;
	
	private String projectCode;
	
	private Long profileId;
	
}
