package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.xmlpull.v1.XmlPullParserException;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;

@SpringBootApplication
@EnableJpaAuditing
public class AuthorizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServiceApplication.class, args);
    }

//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//
//    @Bean
//    public Docket api() throws IOException, XmlPullParserException {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
//                .paths(PathSelectors.any())
//                .build().apiInfo(new ApiInfo("User Service Api Documentation", "Documentation automatically generated", "1.0.0", null, null, null, null));
//    }


}
