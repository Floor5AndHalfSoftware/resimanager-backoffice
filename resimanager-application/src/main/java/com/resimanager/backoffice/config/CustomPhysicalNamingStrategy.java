package com.resimanager.backoffice.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Custom naming strategy that:
 * - Preserves table names exactly as defined in @Table annotations (with quotes for case sensitivity)
 * - Converts column names from camelCase to snake_case and lowercases them
 */
public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        // Preserve table names exactly as defined (they already have quotes in @Table annotations)
        return identifier;
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        // Convert column names to snake_case and lowercase
        String columnName = identifier.getText();
        String snakeCaseName = camelCaseToSnakeCase(columnName);
        return Identifier.toIdentifier(snakeCaseName.toLowerCase(), identifier.isQuoted());
    }

    /**
     * Converts column names to match PostgreSQL schema
     * Examples:
     * - PrfID -> prfid (primary keys: no underscore)
     * - PrfNombre -> prf_nombre  
     * - Prf_UsrCrea -> prf_usr_crea (already has underscore)
     * - PPAId -> ppaid
     * - PPAID -> ppaid
     */
    private String camelCaseToSnakeCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // Special handling for primary key patterns ending with "ID" or "Id"
        // Examples: PrfID -> prfid, PPAID -> ppaid, ModID -> modid
        if (str.matches(".*[Ii][Dd]$")) {
            // Just lowercase it, don't add underscores
            return str.toLowerCase();
        }

        // Already has underscores, just lowercase it
        if (str.contains("_")) {
            return str.toLowerCase();
        }

        // Replace sequences of uppercase letters followed by lowercase with underscore separation
        // Example: PPANombre -> PPA_Nombre
        String result = str.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2");
        
        // Replace lowercase followed by uppercase with underscore separation
        // Example: prfNombre -> prf_Nombre
        result = result.replaceAll("([a-z])([A-Z])", "$1_$2");
        
        // Replace multiple consecutive underscores with single underscore
        result = result.replaceAll("_+", "_");
        
        return result.toLowerCase();
    }
}
