package com.resimanager.backoffice.config.openapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
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
        scheme = "bearer",
        description = "Token JWT obtenido del endpoint /v1/login. Formato: Bearer {token}"
)
public class OpenApi30Config {

    @Value("${app.api.base-url:http://localhost:8080}")
    private String apiBaseUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(apiBaseUrl);
        server.setDescription("ResiManager API Server");

        Contact contact = new Contact()
                .name("ResiManager Team")
                .email("admin@resimanager.com");

        Info info = new Info()
                .title("ResiManager Backoffice API")
                .version("v1.0")
                .description("""
                        ## API REST para el sistema de gestión de condominios ResiManager

                        ### Flujo de autenticación
                        1. Llama a `POST /v1/login` con usuario y contraseña (contraseña en Base64)
                        2. Recibes un **token JWT** y la lista de contextos disponibles
                        3. Si el usuario tiene múltiples contextos, llama a `POST /v1/contexto/cambiar` para seleccionar uno
                        4. Usa el token resultante en el header `Authorization: Bearer {token}` para el resto de llamadas
                        5. Para obtener el menú filtrado, incluye además el header `X-Perfil-Id: {perfilId}`

                        ### Multi-tenancy
                        El sistema soporta múltiples contextos por usuario (Administradora y/o Conjunto).
                        Cada contexto genera un token JWT distinto con los claims del contexto activo.
                        """)
                .contact(contact)
                .license(new License().name("Privado - Uso interno"));

        List<Tag> tags = List.of(
                new Tag().name("Autenticación").description("Login y gestión de sesión de usuario"),
                new Tag().name("Contexto").description("Cambio de contexto multi-tenant (Administradora / Conjunto)"),
                new Tag().name("Menú").description("Obtención del menú lateral filtrado por perfil y permisos"),
                new Tag().name("Propietarios").description("CRUD de propietarios (legacy)")
        );

        return new OpenAPI()
                .servers(List.of(server))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenido del endpoint /v1/login")))
                .info(info)
                .tags(tags)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth", Arrays.asList("read", "write")));
    }
}