# Configuración de Swagger en Koyeb

## Problema
Swagger UI muestra el error "Failed to fetch" con mensajes sobre CORS cuando se despliega en Koyeb.

## Solución Implementada

### 1. Configuración de CORS
Se agregó configuración de CORS en `SecurityConfig.java` para permitir peticiones cross-origin:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 2. Configuración de Servidor OpenAPI
Se actualizó `OpenApi30Config.java` para usar la URL correcta del servidor:

```java
@Value("${app.api.base-url:http://localhost:8080}")
private String apiBaseUrl;

@Bean
public OpenAPI customOpenAPI() {
    Server server = new Server();
    server.setUrl(apiBaseUrl);
    server.setDescription("ResiManager API Server");
    // ... resto de configuración
}
```

### 3. Variable de Entorno
Se agregó `API_BASE_URL` en `application.yml`:

```yaml
app:
  api:
    base-url: ${API_BASE_URL:http://localhost:8080}
```

## Configuración en Koyeb

### Variables de Entorno Requeridas

En el panel de Koyeb, agrega/actualiza estas variables de entorno:

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Server
SERVER_PORT=8080
API_BASE_URL=https://tu-app.koyeb.app

# Database (Neon PostgreSQL)
DB_URL=jdbc:postgresql://ep-raspy-sound-aip2i5um-pooler.c-4.us-east-1.aws.neon.tech/daat_db_dev?sslmode=require&channel_binding=require
DB_DRIVER=org.postgresql.Driver
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_7TphxeoKQ4FZ

# JPA/Hibernate
JPA_DDL_AUTO=none
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
JPA_SHOW_SQL=false

# Flyway
FLYWAY_ENABLED=true

# SQL Init
SQL_INIT_MODE=never
```

### ⚠️ IMPORTANTE

**Reemplaza `https://tu-app.koyeb.app` con tu URL real de Koyeb.**

Por ejemplo:
- `https://resimanager-backoffice.koyeb.app`
- `https://resimanager-jesfa.koyeb.app`

### Cómo Obtener tu URL de Koyeb

1. Ve a tu app en Koyeb Dashboard
2. En la sección **Domains**, copia la URL principal
3. Asegúrate de que sea HTTPS (Koyeb lo proporciona automáticamente)
4. Configúrala en la variable `API_BASE_URL`

### Verificación

Después de configurar las variables y redesplegar:

1. Accede a `https://tu-app.koyeb.app/swagger-ui.html`
2. Deberías ver Swagger UI correctamente
3. La lista desplegable de servidores debe mostrar tu URL de Koyeb
4. Los botones "Try it out" deberían funcionar sin errors de CORS

### Troubleshooting

Si sigues viendo errores:

1. **Verifica que la URL sea HTTPS**: Koyeb usa HTTPS automáticamente
2. **Revisa los logs de Koyeb**: Busca errores de conexión a la base de datos
3. **Prueba el endpoint de API docs**: `https://tu-app.koyeb.app/v3/api-docs`
4. **Verifica que Flyway se haya ejecutado**: Revisa logs para ver si las migraciones se aplicaron

### Endpoints de Verificación

```bash
# Health check
curl https://tu-app.koyeb.app/actuator/health

# API docs JSON
curl https://tu-app.koyeb.app/v3/api-docs

# Swagger UI
https://tu-app.koyeb.app/swagger-ui.html
```

## Desarrollo Local

Para desarrollo local, usa:

```bash
# En .env o .env.local
API_BASE_URL=http://localhost:8080
```

## Notas de Seguridad

- **CORS está configurado para aceptar todos los orígenes** (`allowedOriginPatterns: *`)
- Para producción, considera restringir los orígenes permitidos:

```java
configuration.setAllowedOriginPatterns(List.of(
    "https://tu-app.koyeb.app",
    "https://tu-dominio-frontend.com"
));
```

## Referencias

- [Koyeb Documentation](https://www.koyeb.com/docs)
- [Spring Boot CORS](https://spring.io/guides/gs/rest-service-cors/)
- [Swagger OpenAPI Configuration](https://springdoc.org/#swagger-ui-properties)
