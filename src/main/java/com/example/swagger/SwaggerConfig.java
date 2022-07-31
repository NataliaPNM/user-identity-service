package com.example.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI springShopOpenAPI() {
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList("Auth JWT"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Auth JWT",
                    new SecurityScheme()
                        .name("Auth JWT")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .info(
            new Info()
                .title("User-identity-service")
                .description(
                    "The service stores basic information about the user and his account settings. And provides access to:\n"
                        + "- authorization and authentication;\n- password recovery;"
                        + "\n- receipt of a new pair of tokens;\n- CRUD operations for the user account;\n- change notification settings;")
                .version("v0.0.1"));
  }
}
