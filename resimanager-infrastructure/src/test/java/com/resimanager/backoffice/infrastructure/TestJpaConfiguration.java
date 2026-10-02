package com.resimanager.backoffice.infrastructure;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración mínima de Spring Boot para los tests de la capa de infraestructura.
 * Escanea las entidades de dominio y los repositorios Spring Data.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.resimanager.backoffice.domain.model")
@EnableJpaRepositories("com.resimanager.backoffice.infrastructure.persistence.repository")
public class TestJpaConfiguration {
}
