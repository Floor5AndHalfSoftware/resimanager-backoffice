package com.resimanager.backoffice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Slf4j
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final Environment environment;
    private final DataSource dataSource;

    public ApplicationStartupListener(Environment environment, DataSource dataSource) {
        this.environment = environment;
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String hostName = InetAddress.getLocalHost().getHostName();

            log.info("\n" +
                    "==============================================================================\n" +
                    "██████╗ ███████╗███████╗██╗███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗██████╗ \n" +
                    "██╔══██╗██╔════╝██╔════╝██║████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝██╔══██╗\n" +
                    "██████╔╝█████╗  ███████╗██║██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗  ██████╔╝\n" +
                    "██╔══██╗██╔══╝  ╚════██║██║██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝  ██╔══██╗\n" +
                    "██║  ██║███████╗███████║██║██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗██║  ██║\n" +
                    "╚═╝  ╚═╝╚══════╝╚══════╝╚═╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝\n" +
                    "==============================================================================\n" +
                    "  Application:        ResiManager - Sistema de Gestión de Condominios\n" +
                    "  Version:            1.0-SNAPSHOT\n" +
                    "  Profile:            " + activeProfile + "\n" +
                    "  Spring Boot:        " + environment.getProperty("spring-boot.version", "3.1.4") + "\n" +
                    "==============================================================================\n" +
                    "  Access URLs:\n" +
                    "  ------------------------------------------------------------------------------\n" +
                    "  Local:              http://localhost:" + serverPort + "\n" +
                    "  External:           http://" + hostAddress + ":" + serverPort + "\n" +
                    "  Host:               " + hostName + "\n" +
                    "  ------------------------------------------------------------------------------\n" +
                    "  Scalar UI:          http://localhost:" + serverPort + "/scalar\n" +
                    "  API Docs:           http://localhost:" + serverPort + "/v3/api-docs\n" +
                    "  ------------------------------------------------------------------------------\n" +
                    "  Actuator:           http://localhost:" + serverPort + "/actuator\n" +
                    "  Health:             http://localhost:" + serverPort + "/actuator/health\n" +
                    "  Info:               http://localhost:" + serverPort + "/actuator/info\n" +
                    "  Metrics:            http://localhost:" + serverPort + "/actuator/metrics\n" +
                    "==============================================================================\n" +
                    getDatabaseInfo() +
                    "==============================================================================\n"
            );

        } catch (UnknownHostException e) {
            log.error("Error getting host information", e);
        }
    }

    private String getDatabaseInfo() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String dbUrl = metaData.getURL();
            String dbProduct = metaData.getDatabaseProductName();
            String dbVersion = metaData.getDatabaseProductVersion();
            String dbDriver = metaData.getDriverName();

            // Remove credentials from URL
            String sanitizedUrl = sanitizeDbUrl(dbUrl);

            return "  Database Connection:\n" +
                    "  ------------------------------------------------------------------------------\n" +
                    "  Product:            " + dbProduct + " " + dbVersion + "\n" +
                    "  Driver:             " + dbDriver + "\n" +
                    "  URL:                " + sanitizedUrl + "\n" +
                    "  Connection:         ✓ Connected\n";

        } catch (Exception e) {
            return "  Database Connection:\n" +
                    "  ------------------------------------------------------------------------------\n" +
                    "  Connection:         ✗ Failed - " + e.getMessage() + "\n";
        }
    }

    private String sanitizeDbUrl(String url) {
        if (url == null) {
            return "N/A";
        }

        // Remove username and password from URL
        // Examples:
        // jdbc:postgresql://user:pass@host:port/db -> jdbc:postgresql://host:port/db
        // jdbc:h2:mem:demo_db -> jdbc:h2:mem:demo_db (no change)
        
        String sanitized = url.replaceAll("://[^:]+:[^@]+@", "://***:***@");
        
        // If no credentials were in URL, just return original
        if (sanitized.equals(url)) {
            return url;
        }
        
        return sanitized;
    }
}
