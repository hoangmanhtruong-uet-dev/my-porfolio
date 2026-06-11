package com.love.portfolio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Love Portfolio API")
                        .version("1.0.0")
                        .description("API Documentation for Love Portfolio Backend")
                        .contact(new Contact()
                                .name("Hoàng Mạnh Trường")
                                .email("contact@example.com")
                                .url("https://github.com/hoangmanhtruong-uet-dev")
                        )
                );
    }
}