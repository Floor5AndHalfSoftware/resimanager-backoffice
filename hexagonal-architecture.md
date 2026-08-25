# Hexagonal Architecture — resimanager-backoffice

Este documento define la arquitectura objetivo de **resimanager-backoffice**, inspirada en la migración de referencia `project-jaf-teg-migration`. Guía la migración de la estructura actual (módulo único, capas por paquete, JPA/Lombok acoplado a los servicios) a una **Arquitectura Hexagonal (Puertos y Adaptadores)** con **separación física por módulos Maven**.

---

## Layer Diagram

```
bootstrap (DI/wiring, seguridad HTTP, arranque)
    |
infrastructure (adapters: JPA, JWT, password, contexto/seguridad)
    |
application (use cases / servicios de aplicación)
    |
domain (modelo de negocio, puertos, reglas de negocio)
```

**Regla de dependencias**: dependencia siempre hacia el centro. Las capas exteriores dependen de las interiores, nunca al revés. La separación en módulos Maven garantiza esto en tiempo de compilación.

---

## Módulos Maven

```
resimanager-backoffice (parent POM, packaging pom, <modules>)
├── resimanager-domain          # Java puro, cero deps de framework
├── resimanager-application     # solo depende de domain
├── resimanager-infrastructure  # domain + application
├── resimanager-rest            # domain + application
└── resimanager-bootstrap       # todos; Application + wiring + security
```

`bootstrap` escanea el paquete base `com.resimanager`, de modo que los controllers de `rest` son recogidos (mismo patrón que la referencia, que amplía a `com.teg`).

---

## Domain Layer (núcleo, `resimanager-domain`)

- **Cero dependencias de producción**: Java puro, sin Spring, sin `jakarta.persistence`, sin Lombok en el modelo de negocio.
- **Modelo de negocio rico** (`com.resimanager.domain.model`): entidades con comportamiento e invariantes, no anémicas.
  - Encapsular estado: validar en constructores, exponer métodos que preserven invariantes.
  - Ejemplos: `Persona.actualizarDatos()`, `Usuario.cambiarEstatus()`, validaciones de contexto.
  - Modelo: `Persona`, `Administradora`, `Conjunto`, `Perfil`, `Modulo`, `MenuItem`, `Opcion`, `Accion`, `Propiedad`, `Propietario`, `User` y las relaciones (`PersAdministradora`, `PersConjunto`, `PerfPersAdministradora`, `PerfPersConjunto`, `ModPerfil`, `OpcPerfil`, `AccOpcPerfil`).
- **Puertos**:
  - `port.in/` — puertos de entrada (interfaces de caso de uso): `UsuarioUseCase`, `PerfilUseCase`, `ModuloUseCase`, `ContextoUseCase`, `AdministradoraUseCase`, `ConjuntoUseCase`, `PropietarioUseCase`, `AuthUseCase`, `MenuUseCase`, `DashboardUseCase`.
  - `port.out/` — puertos de salida (interfaces de repositorio/gateway): `PersonaRepositoryPort`, `PerfilRepositoryPort`, `ModuloRepositoryPort`, `MenuRepositoryPort`, `AdministradoraRepositoryPort`, `ConjuntoRepositoryPort`, `PropietarioRepositoryPort`, `PropiedadRepositoryPort`, repos de relación, `PasswordEncoderPort`, `JwtPort`.
- **Excepciones**: excepciones de negocio (`DomainException`, `ServiceException`) en `com.resimanager.domain.exception`.
- **Enums**: clasificaciones fijas (estatus `A`/`I`, tipos de contexto `ADMINISTRADORA`/`CONJUNTO`).

---

## Application Layer (`resimanager-application`)

- **Dependencias**: solo `domain`.
- **Servicios**: implementan los puertos `in`.
  - Un servicio por agregado/caso de uso (`UsuarioService implements UsuarioUseCase`, etc.).
  - Orquestan entidades de dominio + llamadas a puertos `out`.
  - Sin anotaciones de framework, sin HTTP, sin JPA.
- **Regla crítica**: portar los cuerpos de la lógica de negocio actual **sin reescribirlos**, para preservar el contrato REST existente. Quitar `ResponseStatusException` (→ excepciones de dominio) y `@Transactional`/`Page<Entity>` (→ responsabilidad del adaptador).
- **Mappers**: traducción dominio ↔ DTO en esta capa o en `rest` (MapStruct permitido aquí apuntando a tipos de dominio).

---

## REST API Layer (adaptador HTTP de entrada, `resimanager-rest`)

- **Dependencias**: `domain` + `application` (nunca `infrastructure`).
- **Propósito**: exponer los casos de uso por HTTP como **API pública** consumida por el SPA (`resimanager-spa`).
- **Contenido**:
  - `@RestController`s en `com.resimanager.rest.api`.
  - DTOs de request/response en `com.resimanager.rest.dto`.
  - OpenAPI 3 + Scalar (springdoc).
- **Regla**: no lógica de negocio; solo traducción a/desde DTOs. Inyectan puertos `in` (use cases), no servicios concretos.
- **Contrato**: los endpoints y DTOs existentes se **mantienen idénticos** (`/v1/**`, `/api/owners/**`).

---

## Infrastructure Layer (`resimanager-infrastructure`)

- **Dependencias**: `domain` + `application`.
- **Adaptadores**: implementan los puertos `out`.
  - `infrastructure/adapter`: `JpaPersonaAdapter implements PersonaRepositoryPort`, etc. Envuelven los repos de Spring Data existentes.
  - `infrastructure/security`: `BCryptPasswordEncoder` (impl de `PasswordEncoderPort`), `JwtAdapter` (impl de `JwtPort`), `CustomAuthenticationProvider` (adaptador de auth).
  - Modelo de persistencia JPA (`@Entity`/repos) separado del modelo de dominio, con mapeo entre ambos.
- **Sin lógica de negocio**: solo traducción técnica. `@Transactional` y paginación viven aquí.
- **Config de datos**: Flyway (migraciones), conexión, cache.

---

## Bootstrap Layer (más externa, `resimanager-bootstrap`)

- **Dependencias**: todas las demás.
- **Responsabilidad**: `Application` (main), wiring de dependencias (beans Spring que unen puertos `in` con adaptadores), `SecurityConfig` (filter chain, whitelist), `JWTAuthorizationFilter`, CORS, `ApplicationStartupListener`, `OpenApi30Config`.
- La seguridad HTTP es un concern de esta capa; el hashing y el JWT en sí son adaptadores de `infrastructure`.

---

## Testing Strategy (objetivo)

> Estado actual: **0 tests**. Se documenta la estrategia objetivo; la migración actual no añade tests (decisión de alcance).

| Capa | Tipo de test | Ejemplo |
|---|---|---|
| domain | Unitarios (sin mocks) | `PersonaTest`, `PerfilTest` |
| application | Unitarios (puertos `out` mockeados) | `UsuarioServiceTest` con `PersonaRepositoryPort` mockeado |
| rest | Unitarios de controller (MockMvc standalone) | `UsuarioApiTest` con `UsuarioUseCase` mockeado |
| infrastructure | Integración (H2/Postgres) | `JpaPersonaAdapterIT` |
| bootstrap | Smoke / end-to-end | Arranque y verificación de endpoints |

---

## Code Style & Conventions

- Código y comentarios en **español**.
- `Objects.requireNonNull()` para validación de parámetros en dominio.
- `List`, `Set`, `Optional`; nunca `Vector` ni retornos null.
- `@Override` en todas las implementaciones de interfaces.
- Clases de test: `<Class>Test` unit, `<Class>IT` integración.

---

## What NOT to do

- NO poner lógica de negocio en infraestructura (sin reglas `if` en repos/adaptadores).
- NO filtrar concerns de UI/HTTP al dominio.
- NO usar anotaciones de framework (`@Autowired`, `@Service`, `@Entity`, `@Transactional`) fuera de la capa correspondiente.
- NO romper la dirección de dependencias (evita el `module-info` o compila por módulos).
- NO cambiar endpoints/DTOs del contrato público consumido por el SPA.

---

## Mapeo actual → destino

| Actual | Destino |
|---|---|
| `persistance/entity/*` | `domain/model` (entidades ricas) |
| `persistance/repository/*` | `domain/port/out` (interfaces) + `infrastructure/adapter` (JPA) |
| `service/*`, `service/mapper/*` | `application/service` |
| `controller/*`, `dto/*` | `rest/api`, `rest/dto` |
| `config/*`, `util*`, `exception/*` | `infrastructure` y `bootstrap` |

---

## Stack

- Java 21 · Spring Boot 3.1.4 · Spring Security (JWT) · Spring Data JPA · Flyway · MapStruct · Lombok (solo fuera de `domain`) · PostgreSQL/H2 · springdoc-openapi-scalar
