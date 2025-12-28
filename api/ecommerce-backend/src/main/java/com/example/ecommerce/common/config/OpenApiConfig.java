package com.example.ecommerce.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiDocumentation() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("E-commerce Backend Service")
                .description("Backend service for E-commerce platform using Spring Boot")
                .version("1.0")
                .contact(apiContact())
                .license(apiLicense());
    }

    private Contact apiContact() {
        return new Contact()
                .name("Pial Kanti Samadder")
                .email("pialkanti2012@gmail.com");
    }

    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/license/mit/");
    }
}
