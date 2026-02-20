package com.resimanager.backoffice.config.openapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApi30Config {
    
    @Value("${app.api.base-url:http://localhost:8080}")
    private String apiBaseUrl;
    
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(apiBaseUrl);
        server.setDescription("ResiManager API Server");
        
        Contact contact = new Contact();
        contact.setName("ResiManager Team");
        contact.setEmail("admin@resimanager.com");
        
        Info info = new Info()
                .title("ResiManager Backoffice API")
                .version("v1.0")
                .description("Sistema de gestión de condominios - API REST")
                .contact(contact);
        
        return new OpenAPI()
                .servers(List.of(server))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth", Arrays.asList("read", "write")));
    }
}