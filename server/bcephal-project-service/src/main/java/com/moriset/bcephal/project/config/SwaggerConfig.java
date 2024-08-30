//package com.moriset.bcephal.project.config;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import lombok.Data;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
////@Configuration
////@EnableSwagger2
////@ConfigurationProperties(prefix = "app.api")
////@ConditionalOnProperty(name="app.api.swagger.enable", havingValue = "true", matchIfMissing = false)
//@Data
//public class SwaggerConfig {
//
//	private String version;
//	private String title;
//	private String description;
//	private String basePackage;
//	private String contactName;
//	private String contactEmail;
//	
//	@Value("${spring.profiles.active}")
//	private String activeProfile;
//	
//	@Bean
//	public Docket api() {
////		return new Docket(DocumentationType.SWAGGER_2)
////			.select()
////			.apis(RequestHandlerSelectors.basePackage(basePackage))
////			.paths(PathSelectors.any())
////			.build()
////			.directModelSubstitute(LocalDate.class, java.sql.Date.class)
////			.directModelSubstitute(LocalDateTime.class, java.util.Date.class)
////			.apiInfo(apiInfo());
//		
//		return new Docket(DocumentationType.SWAGGER_2)  
//		          .select()                                  
//		          .apis(RequestHandlerSelectors.any())              
//		          .paths(PathSelectors.any())                          
//		          .build();                                           
//	}
//	
//	private ApiInfo apiInfo() {
//		return new ApiInfoBuilder()
//			.title(title)
//			.description(description)
//			.version(version)
//			.contact(new Contact(contactName, null, contactEmail))
//			.build();
//	}
//}
