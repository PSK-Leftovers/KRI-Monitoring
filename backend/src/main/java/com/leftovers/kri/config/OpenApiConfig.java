package com.leftovers.kri.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KRI Monitoring API")
                        .description("Demo Spring Boot application showcasing best practices: layered architecture, validation, global exception handling, and structured logging.")
                        .version("v1"));
    }

}
