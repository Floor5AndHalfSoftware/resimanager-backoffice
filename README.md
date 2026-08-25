# ResiManager - Backoffice

Aplicación backend para la gestión de residencias y condominios.

## Tecnologías

- Java 21
- Spring Boot 3.1.4
- Maven
- Spring Security (JWT + HttpOnly Cookie)
- PostgreSQL / H2
- Flyway (Migraciones)
- Swagger/OpenAPI (SpringDoc)

## Endpoints Disponibles

| Categoría | Endpoints |
|-----------|-----------|
| Autenticación | POST /v1/login |
| Contexto | POST /v1/contexto/cambiar |
| Menú | GET /v1/menu/perfil |
| Dashboard | GET /v1/dashboard/stats |
| Usuarios | GET /v1/usuarios, GET/{id}, PUT/{id}, DELETE/{id}, GET/{id}/perfiles |
| Perfiles | GET /v1/perfiles, POST, GET/{id}, PUT/{id}, DELETE/{id}, POST/{id}/modulos, DELETE/{id}/modulos/{moduloId} |
| Módulos | GET /v1/modulos |
| Conjuntos | POST, GET, GET/{id}, PUT/{id}, DELETE/{id}, GET/{id}/usuarios, POST/{conjId}/usuarios/{userId}/perfiles, DELETE/{conjId}/usuarios/{userId}/perfiles/{perfilId} |
| Administradoras | POST, GET, GET/{id}, PUT/{id}, DELETE/{id}, GET/{id}/usuarios, POST/{admId}/usuarios/{userId}/perfiles, DELETE/{admId}/usuarios/{userId}/perfiles/{perfilId} |
| Propietarios (legacy) | GET /api/owners, POST, GET/{id}, PUT/{id}, DELETE/{id} |

Documentación Swagger: http://localhost:8080/swagger-ui.html

## Configuración

### Variables de Entorno

Copia `.env.example` a `.env` y configura:

```properties
SPRING_PROFILES_ACTIVE=dev
DB_HOST=localhost:5432
DB_NAME=condominio
DB_USERNAME=postgres
DB_PASSWORD=tu_password
```

### Base de Datos

Flyway crea el esquema automáticamente con 14 migraciones (V1.0.0.0 a V2.0.8):
- 30+ tablas (Persona, Administradora, Conjunto, Propiedad, Perfil, etc.)
- Sistema de seguridad completo (Módulos, Opciones, Acciones, Perfiles)
- Sistema de invitaciones con UUID
- Datos de prueba: 6 usuarios, 2 administradoras, 3 conjuntos

**Usuario admin por defecto:**
- Usuario: `admin`
- Email: `admin@resimanager.com`
- Password: `Admin2024!`

### Desarrollo local (PostgreSQL vía Docker)

El profile `local` usa **PostgreSQL** (no H2). Levanta la base con:

```bash
docker compose up -d
```

y ejecuta la app desde el módulo `resimanager-bootstrap` con las variables de `.env.local` (usuario/BD `resimanager`, `localhost:5432`). Flyway aplica las 19 migraciones automáticamente.

> **Nota (bug preexistente de seeds):** en una base **vacía desde cero**, `V2.0.5` inserta `ModPerfil.mpid` explícitos sin avanzar la secuencia IDENTITY, por lo que `V2.0.8` choca con claves duplicadas. No afecta a Neon (ya migrada y validada). Si borras el volumen y recreas la base, corrige la secuencia una vez antes de arrancar:
>
> ```sql
> SELECT setval(pg_get_serial_sequence('"ModPerfil"','mpid'),
>               (SELECT COALESCE(MAX(mpid),0)+1 FROM "ModPerfil"), false);
> ```

## Construcción y Ejecución

```bash
# Compilar
mvn clean package

# Ejecutar (módulo bootstrap)
mvn -pl resimanager-bootstrap spring-boot:run

# Tests
mvn test

# Docker
docker build -t resimanager-backoffice .
docker run -p 8080:8080 --env-file .env resimanager-backoffice
```

## Estructura del Proyecto

Proyecto **Maven multimodular** (Arquitectura Hexagonal, ver `hexagonal-architecture.md`):

```
resimanager-backoffice/       # Parent POM
├── resimanager-domain/       # Modelo de dominio rico (Java puro) + puertos in/out
├── resimanager-application/  # servicios, DTOs, persistencia (temporal), excepciones, utils
├── resimanager-infrastructure/ # (en construcción) adaptadores de salida
├── resimanager-rest/         # controllers REST + handler
└── resimanager-bootstrap/    # Application, seguridad HTTP, config, resources, jar ejecutable
```

## Despliegue

- **Koyeb:** https://chilly-libbey-wtysoftware-aab36281.koyeb.app
- **Base de datos:** Neon (PostgreSQL)

## Documentación

Ver `/docs/general/` para documentación detallada:
- `07-api-endpoints.md` - Documentación completa de API
- `02-arquitectura.md` - Arquitectura del sistema
- `03-modelo-datos.md` - Modelo de datos
- `04-seguridad.md` - Seguridad y autenticación
