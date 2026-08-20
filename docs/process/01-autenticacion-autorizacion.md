# Sistema de Autenticación y Autorización - ResiManager

## Descripción General

El sistema de autenticación implementa un modelo RBAC (Role-Based Access Control) jerárquico con soporte para múltiples contextos de trabajo (Administradoras y Conjuntos).

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    LOGIN ENDPOINT                            │
│                  POST /api/v1/login                          │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│          CustomAuthenticationProvider                        │
│  - Valida credenciales (SHA-256)                            │
│  - Control de intentos fallidos (5 max)                     │
│  - Cache de intentos con Caffeine                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  UserService                                 │
│  - Busca usuario por username o email                       │
│  - Valida que esté activo (per_sts = 'A')                  │
│  - Obtiene roles desde ContextoService                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              ContextoService                                 │
│  - Obtiene perfiles en Administradoras                      │
│  - Obtiene perfiles en Conjuntos                           │
│  - Genera lista de roles (authorities)                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  JwtService                                  │
│  - Genera token JWT firmado (HMAC-SHA512)                  │
│  - Claims: userId, nombre, email, roles, etc.              │
│  - Expiración configurable (default: ver Constants)         │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              LoginController                                 │
│  - Devuelve JWT token                                       │
│  - Devuelve datos de usuario (UserInfoDTO)                 │
│  - Devuelve contextos disponibles (ContextoDTO[])          │
└─────────────────────────────────────────────────────────────┘
```

## Flujo de Autenticación

### 1. Request de Login

**Endpoint:** `POST /api/v1/login`

**Body:**
```json
{
  "username": "admin",
  "password": "QWRtaW4yMDI0IQ=="  // Base64 encoded
}
```

### 2. Validación de Credenciales

El `CustomAuthenticationProvider` realiza:

1. **Control de intentos fallidos:**
   - Máximo 5 intentos por usuario
   - Cache en memoria con Caffeine
   - Se limpia al login exitoso

2. **Validación de password:**
   - Password en request: Base64 → Decodificado
   - Password en DB: Hash SHA-256
   - Comparación con PasswordEncoder (NoOpPasswordEncoder)

3. **Carga de authorities:**
   - `UserService` consulta perfiles del usuario
   - `ContextoService` convierte perfiles a roles

### 3. Generación de JWT

El token JWT incluye los siguientes claims:

```json
{
  "sub": "admin",
  "iss": "ISSUER_INFO",
  "iat": 1708446000,
  "exp": 1708532400,
  "roles": [
    "ROLE_USER",
    "ROLE_SUPER_ADMINISTRADOR"
  ],
  "userId": 1,
  "nombre": "Super",
  "apellido": "Admin",
  "email": "admin@resimanager.com",
  "documento": "ADMIN-001"
}
```

### 4. Response de Login

```json
{
  "type": "Bearer",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "usuario": {
    "id": 1,
    "usuario": "admin",
    "nombre": "Super",
    "apellido": "Admin",
    "email": "admin@resimanager.com",
    "documento": "ADMIN-001"
  },
  "contextosDisponibles": [
    {
      "tipo": "ADMINISTRADORA",
      "administradora": {
        "id": 1,
        "nombre": "Inmobiliaria ABC",
        "documento": "J-12345678",
        "email": "contacto@inmobiliariaabc.com"
      },
      "perfilesDisponibles": [
        {
          "id": 2,
          "nombre": "Administrador General",
          "descripcion": "Administrador de administradora"
        }
      ]
    },
    {
      "tipo": "CONJUNTO",
      "conjunto": {
        "id": 5,
        "nombre": "Residencial Las Flores",
        "documento": "J-98765432"
      },
      "perfilesDisponibles": [
        {
          "id": 3,
          "nombre": "Administrador de Conjunto",
          "descripcion": "Administrador de conjunto residencial"
        }
      ]
    }
  ]
}
```

## Componentes Principales

### 1. Repositorios

**Nuevos repositorios creados:**

- `PerfPersAdministradoraRepository` - Perfiles de usuario en administradoras
- `PerfPersConjuntoRepository` - Perfiles de usuario en conjuntos
- `PersAdministradoraRepository` - Relaciones persona-administradora
- `PersConjuntoRepository` - Relaciones persona-conjunto
- `AdministradoraRepository` - CRUD de administradoras
- `ConjuntoRepository` - CRUD de conjuntos

**Características:**
- Queries JPQL optimizadas con `JOIN FETCH`
- Filtrado automático por estatus activo (`sts = 'A'`)
- Ordenamiento consistente

### 2. DTOs

**Nuevos DTOs creados:**

```java
// Información de usuario para JWT
UserInfoDTO {
    Integer id;
    String usuario;
    String nombre;
    String apellido;
    String email;
    String documento;
}

// Perfil de acceso
PerfilDTO {
    Integer id;
    String nombre;
    String descripcion;
}

// Administradora
AdministradoraDTO {
    Integer id;
    String nombre;
    String documento;
    String email;
}

// Conjunto residencial
ConjuntoDTO {
    Integer id;
    String nombre;
    String documento;
    String direccion;
    String tipo;
}

// Contexto de trabajo
ContextoDTO {
    String tipo;  // "ADMINISTRADORA" o "CONJUNTO"
    AdministradoraDTO administradora;
    ConjuntoDTO conjunto;
    List<PerfilDTO> perfilesDisponibles;
}
```

### 3. Servicios

#### ContextoService

**Responsabilidades:**
- Obtener contextos disponibles del usuario
- Convertir perfiles de DB a roles de Spring Security
- Agrupar perfiles por administradora/conjunto

**Métodos principales:**

```java
// Obtiene todos los contextos disponibles
List<ContextoDTO> getContextosDisponibles(Integer personaId)

// Obtiene roles para Spring Security authorities
List<String> getRolesFromProfiles(Integer personaId)

// Métodos privados
List<ContextoDTO> getContextosAdministradora(Integer personaId)
List<ContextoDTO> getContextosConjunto(Integer personaId)
```

**Formato de roles generados:**

```java
// Perfil: "Super Administrador" → "ROLE_SUPER_ADMINISTRADOR"
// Perfil: "Administrador General" → "ROLE_ADMINISTRADOR_GENERAL"
// Perfil: "Propietario" → "ROLE_PROPIETARIO"
```

#### UserService (Actualizado)

**Cambios realizados:**
- ✅ Eliminado hardcoded "ROLE_ADMIN" para user ID 1
- ✅ Integración con `ContextoService`
- ✅ Carga dinámica de roles desde BD
- ✅ Agregado `userId` al `AuthDto`

#### JwtService (Actualizado)

**Cambios realizados:**
- ✅ Nuevo método `generateTokenWithUserInfo()`
- ✅ Claims adicionales: userId, nombre, apellido, email, documento
- ✅ Mantiene compatibilidad con método anterior

### 4. Controller

#### LoginController (Actualizado)

**Cambios realizados:**
- ✅ Inyección de `ContextoService`
- ✅ Inyección de `PersonaRepository`
- ✅ Consulta de datos de usuario post-autenticación
- ✅ Generación de `UserInfoDTO`
- ✅ Obtención de contextos disponibles
- ✅ Response enriquecido con usuario y contextos

## Modelo de Datos de Seguridad

### Tablas Involucradas

```
Persona (usuarios del sistema)
  ├── PersAdministradora (relación N:N con Administradora)
  │   └── PerfPersAdministradora (perfiles del usuario en esa administradora)
  │       └── Perfil
  │
  └── PersConjunto (relación N:N con Conjunto)
      └── PerfPersConjunto (perfiles del usuario en ese conjunto)
          └── Perfil

Perfil
  ├── ModPerfil (módulos accesibles)
  │   └── Modulo
  ├── OpcPerfil (opciones accesibles)
  │   └── Opcion
  └── AccOpcPerfil (acciones específicas)
      └── AccOpcion (acción sobre opción)
          ├── Opcion
          └── Accion (MNJ, INS, MOD, VIS, EL)
```

### Jerarquía de Contextos

```
Usuario puede tener múltiples contextos:

Contexto 1: Administradora X
  ├── Perfil: "Administrador General"
  └── Acceso a: Todos los conjuntos de esa administradora

Contexto 2: Conjunto Y
  ├── Perfil: "Administrador de Conjunto"
  └── Acceso a: Solo ese conjunto

Contexto 3: Conjunto Z
  ├── Perfil: "Propietario"
  └── Acceso a: Solo sus propiedades en ese conjunto
```

## Seguridad

### Hash de Passwords

**Algoritmo:** SHA-256

**Proceso:**
1. Usuario ingresa password en texto plano
2. Frontend codifica en Base64 (transporte)
3. Backend decodifica Base64
4. Backend compara con hash SHA-256 en BD

**Ejemplo:**
```
Password: "Admin2024!"
Base64: "QWRtaW4yMDI0IQ=="
SHA-256: "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
```

### Control de Intentos Fallidos

**Configuración:**
- Máximo: 5 intentos
- Storage: Cache en memoria (Caffeine)
- TTL: Hasta login exitoso o reinicio de aplicación
- Key: Username

**Comportamiento:**
```
Intento 1-4: "Incorrect username or password!"
Intento 5: "Number of possible attempts reached!"
Intento 6+: "Number of possible attempts reached!" (sin validar password)
Login exitoso: Cache limpiado
```

### JWT

**Firma:** HMAC-SHA512

**Secret Key:**
- Definida en `Constants.SUPER_SECRET_KEY`
- Hash SHA-512 de la clave original
- 64 bytes de longitud

**Expiración:**
- Configurada en `Constants.TOKEN_EXPIRATION_TIME_IN_MINUTES`
- Renovación: No automática (cliente debe re-autenticar)

## Uso del Sistema

### Endpoints de Login por Ambiente

#### Login LOCAL
```bash
curl -X 'POST' 'http://localhost:8080/v1/login' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin","password": "QWRtaW4yMDI0IQ=="}'
```

#### Login DEVELOP (Koyeb)
```bash
curl -X 'POST' 'https://chilly-libbey-wtysoftware-aab36281.koyeb.app/v1/login' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin","password": "QWRtaW4yMDI0IQ=="}'
```

### 1. Login con Usuario Bootstrap (Ejemplo Extendido)

```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "QWRtaW4yMDI0IQ=="
  }'
```

### 2. Usar el Token en Requests

```bash
curl -X GET http://localhost:8080/api/v1/propiedades \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 3. Decodificar JWT (Debug)

Usar https://jwt.io para inspeccionar el token y verificar claims.

## Testing

### Datos de Bootstrap

**Usuario Admin:**
- Username: `admin`
- Password: `Admin2024!` (Base64: `QWRtaW4yMDI0IQ==`)
- ID: 1
- Email: `admin@resimanager.com`

**Perfiles Disponibles:**
1. Super Administrador (ID: 1)
2. Administrador General (ID: 2)
3. Administrador de Conjunto (ID: 3)
4. Propietario (ID: 4)
5. Residente (ID: 5)

### Casos de Prueba

**1. Login exitoso:**
- ✅ Username correcto + Password correcto
- ✅ Email correcto + Password correcto

**2. Login fallido:**
- ❌ Username incorrecto
- ❌ Password incorrecto
- ❌ Usuario inactivo (per_sts = 'I')

**3. Intentos fallidos:**
- ❌ 5 intentos incorrectos → Bloqueado
- ✅ 4 intentos incorrectos + 1 correcto → Desbloqueado

**4. JWT Claims:**
- ✅ Incluye userId
- ✅ Incluye roles desde BD
- ✅ Incluye datos de usuario (nombre, email, etc.)

**5. Contextos:**
- ✅ Usuario con múltiples administradoras
- ✅ Usuario con múltiples conjuntos
- ✅ Usuario con múltiples perfiles por contexto

## Próximos Pasos

### Pendientes de Implementación

1. **Autorización en Endpoints**
   - Anotaciones `@PreAuthorize`
   - Validación de permisos por módulo/acción
   - Interceptor para contexto actual

2. **Cambio de Contexto**
   - Endpoint `POST /api/v1/auth/cambiar-contexto`
   - Actualización de contexto sin re-login
   - Almacenamiento de contexto activo

3. **Menú Dinámico**
   - Endpoint `GET /api/v1/menu`
   - Query según perfil/contexto actual
   - Estructura jerárquica

4. **Filtrado por Contexto**
   - Interceptor automático en repositories
   - WHERE clause dinámico según contexto
   - Prevención de acceso cross-context

5. **Refresh Token**
   - Token de larga duración
   - Endpoint `/api/v1/auth/refresh`
   - Rotación de tokens

6. **Auditoría**
   - Log de logins exitosos/fallidos
   - Tabla de sesiones activas
   - Histórico de cambios de contexto

## Archivos Modificados/Creados

### Nuevos Archivos

**Repositorios:**
- `PerfPersAdministradoraRepository.java`
- `PerfPersConjuntoRepository.java`
- `PersAdministradoraRepository.java`
- `PersConjuntoRepository.java`
- `AdministradoraRepository.java`
- `ConjuntoRepository.java`

**DTOs:**
- `UserInfoDTO.java`
- `PerfilDTO.java`
- `AdministradoraDTO.java`
- `ConjuntoDTO.java`
- `ContextoDTO.java`

**Servicios:**
- `ContextoService.java`

### Archivos Modificados

- `AuthDto.java` - Agregado campo `userId`
- `UserService.java` - Integración con ContextoService
- `JwtService.java` - Método `generateTokenWithUserInfo()`
- `LoginController.java` - Response con contextos
- `LoginResponseJson.java` - Campos `usuario` y `contextosDisponibles`

## Referencias

- Documentación principal: `C:\dev\Personal\NewProjectF5\ResiManager\docs\04-seguridad.md`
- Modelo de datos: `C:\dev\Personal\NewProjectF5\ResiManager\docs\03-modelo-datos.md`
- Migraciones: `src/main/resources/migrations/V2.0.5__INSERT_BASE_DATA.sql`
