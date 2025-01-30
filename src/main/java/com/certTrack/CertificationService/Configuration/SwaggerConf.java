package com.certTrack.CertificationService.Configuration;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConf {

	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	        .info(new Info().title("CertificationService API").version("1.0")
	            .description("CertificationService Documentation by using Swagger")
	            .contact(new Contact().name("Dmytro Trofimov")
	                .email("dima6836753@gmail.com"))
	            .license(new License().name("Apache 2.0").url("http://springdoc.org")))
	        .servers(List.of(new Server().url("http://localhost:8083").description("Local server"),
	                         new Server().url("https://api.certTrack-users.com").description("Production server")))
	        .components(new Components()
	            .addSecuritySchemes("bearerAuth", 
	                new SecurityScheme().type(SecurityScheme.Type.HTTP)
	                    .scheme("bearer")
	                    .bearerFormat("JWT")))
	        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}


	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("springshop-public")
				.pathsToMatch("/**")
				.build();
	}
}
