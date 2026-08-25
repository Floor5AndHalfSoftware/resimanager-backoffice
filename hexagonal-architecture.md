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

- **Modelo de negocio = entidades JPA** (decisión pragmática): el modelo de dominio son las entidades JPA (`@Entity`, Lombok) **enriquecidas con comportamiento e invariantes** (métodos de negocio sobre los datos). Se descarta tener un modelo paralelo + capa de traducción JPA↔dominio.
  - `com.resimanager.domain.model`: `Persona`, `Administradora`, `Conjunto`, `Perfil`, `Modulo`, `MenuItem`, `Menu`, `Opcion`, `Accion`, `AccOpcion`, `Propiedad`, `Propietario`, relaciones (`PersAdministradora`, `PersConjunto`, `PerfPersAdministradora`, `PerfPersConjunto`, `ModPerfil`, ...) y value objects no persistidos (`ResultadoPaginado`, `ContextoDisponible`, `ContextoActivo`, `AuthUser`, `DashboardStats`).
  - La lógica de negocio se expresa con métodos de dominio; los datos se persisten tal cual (sin DTO anémico paralelo).
- **Puertos**:
  - `port.in/` — puertos de entrada (interfaces de caso de uso): `UsuarioUseCase`, `PerfilUseCase`, `ModuloUseCase`, `ContextoUseCase`, `AdministradoraUseCase`, `ConjuntoUseCase`, `PropietarioUseCase`, `AuthUseCase`, `MenuUseCase`, `DashboardUseCase`.
  - `port.out/` — puertos de salida (interfaces de repositorio/gateway): `PersonaRepositoryPort`, `PerfilRepositoryPort`, `ModuloRepositoryPort`, `MenuRepositoryPort`, `AdministradoraRepositoryPort`, `ConjuntoRepositoryPort`, `PropietarioRepositoryPort`, `PropiedadRepositoryPort`, repos de relación (`PersAdministradoraRepositoryPort`, `PersConjuntoRepositoryPort`, `PerfPersAdministradoraRepositoryPort`, `PerfPersConjuntoRepositoryPort`, `ModPerfilRepositoryPort`), `PasswordEncoderPort`, `JwtPort`, `DashboardStatsRepositoryPort`.
- **Excepciones**: `DomainException` en `com.resimanager.domain.exception`.
- **Enums**: `Estatus` (A/I) y `TipoContexto` (ADMINISTRADORA/CONJUNTO) en `com.resimanager.domain.model.enums`.

> **Trade-off documentado:** a diferencia de la referencia (dominio puro), aquí el dominio depende de `jakarta.persistence` y Lombok porque el modelo es directamente el modelo de persistencia. La regla de dependencias (solo hacia el centro) se mantiene intacta; la regla "cero anotaciones" se relaja a "la lógica de negocio vive en el dominio, no en adaptadores".

---

## Application Layer (`resimanager-application`)

- **Dependencias**: solo `domain` (modelo + puertos) y sus propios DTOs/mappers.
- **Servicios**: orquestan entidades de dominio (JPA) usando los **puertos `out`** inyectados; ya no dependen de repositorios Spring Data ni de `EntityManager`.
  - Un servicio por agregado/caso de uso (`UsuarioService`, `PerfilService`, `AdministradoraService`, `ConjuntoService`, `ContextoService`, `PropietarioService`, `PropiedadService`, `ModuloService`, `MenuService`, `DashboardService`, `UserService`).
  - Conservan la traducción dominio ↔ DTO para el contrato REST (mappers MapStruct existentes).
- **Notas transitorias** (se limpian en fases posteriores): se mantienen `@Transactional` en los servicios y `ResponseStatusException`/excepciones de aplicación; el desacople de repositorios ya está resuelto por los puertos.

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
- **Adaptadores**: implementan los puertos `out` como beans Spring, envolviendo los repositorios Spring Data:
  - `infrastructure/adapter`: `JpaPersonaAdapter`, `JpaPerfilAdapter`, `JpaModuloAdapter`, `JpaAdministradoraAdapter`, `JpaConjuntoAdapter`, `JpaPropietarioAdapter`, `JpaPropiedadAdapter`, `JpaPersAdministradoraAdapter`, `JpaPersConjuntoAdapter`, `JpaPerfPersAdministradoraAdapter`, `JpaPerfPersConjuntoAdapter`, `JpaModPerfilAdapter`, `JpaAccOpcPerfilAdapter`, `JpaMenuAdapter`, `JpaDashboardStatsAdapter`.
  - Traducción de paginación (`Page` → `ResultadoPaginado`), `Estatus` → código y las consultas `MAX+1` (IDs manuales) viven aquí.
- **Sin lógica de negocio**: solo delegación técnica.
- Los repositorios Spring Data permanecen temporalmente en `application/persistance/repository`; se moverán a esta capa en la fase de limpieza.

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
| `persistance/entity/*` | `domain/model` (entidades JPA como modelo de dominio) |
| `persistance/repository/*` | `domain/port/out` (interfaces) + `infrastructure/adapter` (impl) |
| `service/*`, `service/mapper/*` | `application/service` (usan puertos `out`) |
| `controller/*`, `dto/*` | `rest/api`, `rest/dto` |
| `config/*`, `util*`, `exception/*` | `infrastructure` y `bootstrap` |

---

## Stack

- Java 21 · Spring Boot 3.1.4 · Spring Security (JWT) · Spring Data JPA · Flyway · MapStruct · Lombok (solo fuera de `domain`) · PostgreSQL/H2 · springdoc-openapi-scalar
