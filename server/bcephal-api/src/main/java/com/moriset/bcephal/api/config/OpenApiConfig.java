package com.moriset.bcephal.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
	info = @Info(
		contact = @Contact(
			name = "Moriset",
			email = "info@moriset.com",
			url = "https://moriset.com"						
		),
		description = "Documentation for B-CEPHAL API",
		title = "B-CEPHAL API specification",
		version = "0.0.1"
	),
	security = {
		@SecurityRequirement(name = "bearerAuth")
	}
)
@SecurityScheme(
	name = "bearerAuth",
	scheme = "bearer",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
	
}
