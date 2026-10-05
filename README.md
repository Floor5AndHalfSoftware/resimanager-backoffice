# ResiManager — Backoffice (Backend)

API REST del sistema de gestión de condominios, residencias y conjuntos residenciales, con arquitectura **multi-tenant** y control de acceso jerárquico. Este README es autocontenido (resume toda la información necesaria para entender y operar el backend).

---

## Resumen

- **Autenticación** con login (password en Base64) y sesión por **access token JWT (~30 min) + refresh token (7 días)** en cookies HttpOnly.
- **Multi-tenant**: contexto activo (Administradora / Conjunto) + perfil; el menú se filtra por perfil.
- **CRUDs**: usuarios, perfiles, administradoras, conjuntos, propiedades y propietarios.
- **RBAC** por jerarquía Módulo → Opción → Acción → Perfil (hoy operativo solo a nivel de módulo).
- **Flyway** gestiona el esquema (hasta `V2.0.17`); **PostgreSQL** (Neon en dev/prod; Docker en local).
- **25 tests** automatizados (application, infrastructure, rest, bootstrap).

## Stack

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.16 |
| Spring Security | 6.x (JWT + cookies HttpOnly) |
| Spring Data JPA / Hibernate | 6.x |
| Flyway | 11.7.2 |
| JJWT | 0.12.6 |
| MapStruct | 1.5.3 |
| Lombok | 1.18.30 |
| springdoc + Scalar | 2.8.17 |
| PostgreSQL | 15+/16 |
| H2 (test) | 2.3.232 |
| Caffeine | — (rate limiting de login) |

## Arquitectura hexagonal (Maven multimodular)

La dependencia apunta siempre al centro: `bootstrap → infrastructure → application → domain`.

```
resimanager-backoffice/        # Parent POM (packaging pom)
├── resimanager-domain/        # Modelo (entidades JPA enriquecidas) + puertos in/out + enums + excepciones
├── resimanager-application/   # Casos de uso (usan puertos out), DTOs, mappers MapStruct
├── resimanager-infrastructure/# Adaptadores de salida (JPA) que implementan los puertos out
├── resimanager-rest/          # Controllers REST + DTOs (domain + application)
└── resimanager-bootstrap/     # Application, SecurityConfig, filtros, config, resources, jar ejecutable
```

- Puertos `in` (casos de uso): `UsuarioUseCase`, `PerfilUseCase`, `ModuloUseCase`, `ContextoUseCase`, `AdministradoraUseCase`, `ConjuntoUseCase`, `PropietarioUseCase`, `PropiedadUseCase`, `AuthUseCase`, `MenuUseCase`, `DashboardUseCase`, `RefreshTokenUseCase`.
- Puertos `out`: repositorios por agregado (`PersonaRepositoryPort`, `PerfilRepositoryPort`, `RefreshTokenRepositoryPort`, ...), `PasswordEncoderPort`, `JwtPort`, `DashboardStatsRepositoryPort`.
- **Modelo de dominio = entidades JPA** (decisión pragmática): el dominio depende de `jakarta.persistence` y Lombok; la lógica de negocio vive en el dominio, no en adaptadores.

## Seguridad y sesión

- **Access token (JWT HS512)** en cookie HttpOnly `jwt` (~30 min, configurable con `ACCESS_TOKEN_TTL_MINUTES`); también se acepta por header `Authorization: Bearer`.
  - Claims: `sub`, `roles`, `tokenType=access`, `userId`, `nombre`, `apellido`, `email`, `documento` y contexto (`contextoTipo`, `contextoEntidadId`, `contextoEntidadNombre`, `contextoPerfilId`, `contextoPerfilNombre`).
- **Refresh token opaco** en cookie HttpOnly `refresh` (7 días, `REFRESH_TOKEN_TTL_SECONDS`): se genera con `SecureRandom` (256 bits) y se guarda **solo su hash SHA-256** en la tabla `refresh_token`.
  - **Rotación** en cada uso (el token presentado se consume y se emite uno nuevo de la misma familia; no extiende la expiración absoluta).
  - **Detección de reutilización**: usar un token ya rotado → `401` y se **revoca toda la familia**.
  - **Logout** revoca la familia de forma real en servidor y limpia las cookies.
- **Contraseñas con BCrypt** (strength 10); el login recibe la contraseña en **Base64** y la compara con BCrypt.
- **Rate limiting** de login con caché **Caffeine** (`loginAttempts`): máximo 5 intentos por usuario.
- Cookies: `Path=/`, HttpOnly, `Secure`/`SameSite` configurables por `SECURITY_COOKIE_SECURE` (dev `false`/`Lax`, prod `true`/`None`).

## API (base `/v1`)

| Categoría | Endpoints |
|-----------|-----------|
| Autenticación | `POST /v1/login`, `POST /v1/refresh`, `POST /v1/logout` |
| Contexto | `POST /v1/contexto/cambiar` |
| Menú | `GET /v1/menu/perfil` (header `X-Perfil-Id`) |
| Dashboard | `GET /v1/dashboard/stats` |
| Usuarios | `GET /v1/usuarios`, `GET/{id}`, `PUT/{id}`, `DELETE/{id}`, `GET/{id}/perfiles` |
| Perfiles | `GET /v1/perfiles`, `POST`, `GET/{id}`, `PUT/{id}`, `DELETE/{id}`, `POST/{id}/modulos`, `DELETE/{id}/modulos/{moduloId}` |
| Módulos | `GET /v1/modulos` *(solo lectura)* |
| Conjuntos | `GET`, `POST`, `GET/{id}`, `PUT/{id}`, `DELETE/{id}`, `GET/{id}/usuarios`, `POST|DELETE /v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles[/{perfilId}]` |
| Administradoras | `GET`, `POST`, `GET/{id}`, `PUT/{id}`, `DELETE/{id}`, `GET/{id}/usuarios`, `POST|DELETE /v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles[/{perfilId}]` |
| Propiedades | `GET /v1/propiedades`, `GET/{id}`, `POST`, `PUT/{id}`, `DELETE/{id}`, `GET /v1/propiedades/clases` |
| Propietarios | `GET /v1/propietarios`, `POST`, `GET/{conjId}/{perId}`, `PUT/{conjId}/{perId}`, `DELETE/{conjId}/{perId}` |

- **Paginación**: `{ data: [...], total, page, limit }` con `page` (1) y `limit` (25–50 según endpoint).
- **Códigos**: 200, 201, 204, 400, 401, 403, 404, 409, 422, 500.
- **OpenAPI/Scalar**: `/scalar`, `/v3/api-docs`, `/swagger-ui.html`.

## Permisos (RBAC)

Jerarquía: `Módulo → Opción → Acción → Perfil`.

- Acciones: `MNJ` (manejar), `INS` (insertar), `MOD` (modificar), `VIS` (visualizar), `EL` (eliminar).
- Tablas: `modulo`, `opcion`, `accion`, `"AccOpcion"`, `"ModPerfil"` (módulos por perfil), `"OpcPerfil"` (opciones), `"AccOpcPerfil"` (permisos finales).
- **Estado actual:** el sistema opera **solo a nivel de módulo** (`"ModPerfil"`). `opcion`, `"AccOpcion"`, `"OpcPerfil"` y `"AccOpcPerfil"` existen pero no tienen CRUD ni uso; `PermissionService.hasPermission()` no está enganchado como middleware (pendiente).

## Base de datos y migraciones

- **PostgreSQL**; esquema gestionado por **Flyway** (`spring.flyway`), hasta `V2.0.17`.
- Migraciones relevantes: `V2.0.1` core, `V2.0.2` seguridad, `V2.0.3` relaciones, `V2.0.4` invitaciones, `V2.0.5` datos base, `V2.0.6` datos de prueba, `V2.0.7` contexto admin, `V2.0.8` permisos por perfil, `V2.0.9` nivel de perfil, `V2.0.10`–`V2.0.13` menú, `V2.0.16` `Propietario`, **`V2.0.17` `refresh_token`**.
- **Neon pooler**: configurar `spring.flyway.postgresql.transactional-lock: true` (evita `Unable to release PostgreSQL advisory lock`; ninguna migración usa sentencias no transaccionales como `CREATE INDEX CONCURRENTLY`).
- **Bug preexistente de seeds**: en una base **vacía desde cero**, `V2.0.5` inserta `ModPerfil.mpid` explícitos sin avanzar la secuencia IDENTITY, por lo que `V2.0.8` choca con claves duplicadas. No afecta a Neon (ya migrada). Si recreas el volumen, corrige la secuencia una vez:
  ```sql
  SELECT setval(pg_get_serial_sequence('"ModPerfil"','mpid'),
                (SELECT COALESCE(MAX(mpid),0)+1 FROM "ModPerfil"), false);
  ```
- Tabla `refresh_token`: `rt_token_hash` (único), `rt_family_id` (UUID), `rt_per_id`, `rt_ctx_tipo` (A/C), `rt_ctx_entidad_id`, `rt_ctx_prf_id`, `rt_expira`, `rt_consumido`, `rt_revocado`, + auditoría.

**Usuario admin por defecto:** `admin` / `Admin2024!` — `admin@resimanager.com`.
Datos de prueba: `cmartinez` (Administrador General), `mrodriguez` (Admin de Conjunto), `jperez` (Propietario), `agarcia` (Residente), `lgomez` (multi-contexto). Contraseñas patrón `{Nombre}2024!`.

## Tests

```bash
mvn test
```
25 tests (se fija `maven-surefire-plugin` 3.2.5; `spring-boot-starter-test` + `h2` en los módulos con tests):
- **application (13):** `JwtService` (TTL, `tokenType`, contexto), `RefreshTokenGenerator`, `RefreshTokenService` (rotación/reutilización/expiración).
- **infrastructure (4):** `JpaRefreshTokenAdapter` con H2 (`@DataJpaTest`).
- **rest (4):** `RefreshController` (200/401) y logout→refresh `401`.
- **bootstrap (4):** `JWTAuthorizationFilter` (refresh opaco rechazado; access por header/cookie).

## Configuración (variables de entorno)

```properties
SPRING_PROFILES_ACTIVE=local
SERVER_PORT=8080
API_BASE_URL=http://localhost:8080

DB_URL=jdbc:postgresql://<host>/<db>?sslmode=require&channel_binding=require
DB_DRIVER=org.postgresql.Driver
DB_USERNAME=<user>
DB_PASSWORD=<password>

JPA_DDL_AUTO=none
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
JPA_SHOW_SQL=false

FLYWAY_ENABLED=true
SQL_INIT_MODE=never

SECURITY_COOKIE_SECURE=true          # false en desarrollo HTTP
ACCESS_TOKEN_TTL_MINUTES=30
REFRESH_TOKEN_TTL_SECONDS=604800
```

## Construcción y ejecución

```bash
# Compilar/instalar (raíz del multimódulo)
mvn clean install -DskipTests

# Ejecutar (módulo bootstrap)
mvn -pl resimanager-bootstrap spring-boot:run

# Ejecutar con debugger (JDWP puerto 5005)
mvn -pl resimanager-bootstrap spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Tests
mvn test

# Docker
docker build -t resimanager-backoffice .
docker run -p 8080:8080 --env-file .env resimanager-backoffice
```

### Postgres local (Docker)
```bash
docker compose up -d          # postgres:16-alpine — BD/user/pass = resimanager
set -a; source .env.local; set +a
mvn -pl resimanager-bootstrap spring-boot:run
```
> `spring-boot:run` arranca con working directory = raíz del repo, por lo que `spring-dotenv` carga el `.env` de la raíz. Para usar el Postgres local, exporta `.env.local` como arriba.

## Despliegue

- **Koyeb:** https://chilly-libbey-wtysoftware-aab36281.koyeb.app
- **Base de datos:** Neon (PostgreSQL).
- Requisitos de producción: `SECURITY_COOKIE_SECURE=true`, `DB_URL` de Neon y Flyway con `transactional-lock: true`. Verificar `GET /actuator/health`.

## Estado del proyecto

Backend **~92%**. Completos: autenticación/sesión con refresh, contexto multi-tenant, menú dinámico, CRUDs (usuarios, perfiles, administradoras, conjuntos, propiedades, propietarios), dashboard (conteos reales + facturas/incidencias mock) y tests.

**Pendientes:** sistema de invitaciones (solo migración `V2.0.4`), CRUD de módulos/opciones/acciones + permisos granulares + middleware de autorización, auditoría (`log_operacion` + triggers), datos reales de facturas/incidencias, recuperación de contraseña.
