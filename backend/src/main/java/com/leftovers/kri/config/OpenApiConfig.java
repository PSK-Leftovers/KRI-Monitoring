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
                        .description("API for the KRI Monitoring platform: risk indicators, threshold classification, and director notifications.")
                        .version("v1"));
    }

}
