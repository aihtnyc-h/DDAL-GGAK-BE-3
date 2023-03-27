package com.ddalggak.finalproject.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("DDALGGAK API Docs")
			.version("v0.0.1")
			.description("딸깍 프로젝트의 API 명세서입니다.");
		String security = "JwtAuth";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(security);
		Components components = new Components()
			.addSecuritySchemes(security, new SecurityScheme()
				.name(security)
				.type(SecurityScheme.Type.HTTP)
				.scheme("Bearer")
				.bearerFormat("JWT"));

		return new OpenAPI()
			.addSecurityItem(securityRequirement)
			.components(components)
			.info(info);
	}
}
