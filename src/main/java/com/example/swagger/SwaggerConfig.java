package com.example.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Auth JWT")).components(
                        new Components()
                                .addSecuritySchemes("Auth JWT",
                                        new SecurityScheme()
                                                .name("Auth JWT")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .info(new Info().title("Auth-service")
                        .description("Сервис для авторизации пользователя, обновления токена и смены пароля")
                        .version("v0.0.1"));
    }

}

