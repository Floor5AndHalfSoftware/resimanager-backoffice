# API Endpoints - Documentación

**Última actualización:** 09 de Mayo de 2026  
**Versión API:** v1  
**Base Path:** `/v1`

---

## 📌 Base URL

```
Local:   http://localhost:8080/v1
Koyeb:   https://chilly-libbey-wtysoftware-aab36281.koyeb.app/v1
```

---

## 🔐 Autenticación

El JWT se almacena en una cookie HttpOnly (`jwt`) en lugar de enviarse por header.
Para compatibilidad, también se acepta el header `Authorization: Bearer <token>`.

El token JWT incluye estos claims:
- `userId` - ID del usuario
- `nombre` - Nombre del usuario
- `apellido` - Apellido del usuario
- `email` - Email del usuario
- `documento` - Documento de identidad
- `roles` - Lista de roles/perfiles
- `contextoTipo` - Tipo de contexto (ADMINISTRADORA o CONJUNTO)
- `contextoEntidadId` - ID de la entidad activa
- `contextoPerfilId` - ID del perfil activo

**Expiración:** 24 horas

---

## 📚 Documentación Swagger

```
http://localhost:8080/swagger-ui.html
https://chilly-libbey-wtysoftware-aab36281.koyeb.app/swagger-ui.html
```

---

## 📋 Resumen de Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/v1/login` | Iniciar sesión |
| POST | `/v1/contexto/cambiar` | Cambiar contexto activo |
| GET | `/v1/menu/perfil` | Obtener menú por perfil |
| GET | `/v1/usuarios` | Listar usuarios |
| GET | `/v1/usuarios/{id}` | Obtener usuario por ID |
| PUT | `/v1/usuarios/{id}` | Actualizar usuario |
| DELETE | `/v1/usuarios/{id}` | Inactivar usuario |
| GET | `/v1/usuarios/{id}/perfiles` | Perfiles de un usuario |
| GET | `/v1/perfiles` | Listar perfiles |
| POST | `/v1/perfiles` | Crear perfil |
| GET | `/v1/perfiles/{id}` | Detalle de perfil |
| PUT | `/v1/perfiles/{id}` | Actualizar perfil |
| DELETE | `/v1/perfiles/{id}` | Eliminar perfil |
| POST | `/v1/perfiles/{id}/modulos` | Asignar módulos a perfil |
| DELETE | `/v1/perfiles/{id}/modulos/{moduloId}` | Revocar módulo de perfil |
| GET | `/v1/modulos` | Listar módulos |
| GET | `/v1/conjuntos` | Listar conjuntos |
| GET | `/v1/conjuntos/{id}` | Obtener conjunto por ID |
| GET | `/v1/conjuntos/{id}/usuarios` | Usuarios de un conjunto |
| POST | `/v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles` | Asignar perfiles en conjunto |
| DELETE | `/v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles/{perfilId}` | Remover perfil en conjunto |
| GET | `/v1/administradoras` | Listar administradoras |
| GET | `/v1/administradoras/{id}` | Obtener administradora por ID |
| GET | `/v1/administradoras/{id}/usuarios` | Usuarios de una administradora |
| POST | `/v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles` | Asignar perfiles en administradora |
| DELETE | `/v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles/{perfilId}` | Remover perfil en administradora |
| GET | `/api/owners` | Listar propietarios (legacy) |
| GET | `/api/owners/{id}` | Obtener propietario (legacy) |
| POST | `/api/owners` | Crear propietario (legacy) |
| PUT | `/api/owners/{id}` | Actualizar propietario (legacy) |
| DELETE | `/api/owners/{id}` | Eliminar propietario (legacy) |

---

## 🔑 Endpoints de Autenticación

### POST /v1/login

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Inicia sesión y retorna datos del usuario + contextos disponibles. El JWT se devuelve en el body y también se establece como cookie HttpOnly.

**Request:**
```json
{
  "username": "admin",
  "password": "QWRtaW4yMDI0IQ=="
}
```

**Response 200:**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
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
      "administradora": { "id": 1, "nombre": "Inmobiliaria ABC", "documento": "J-12345678-9", "email": "contacto@inmobiliariaabc.com" },
      "perfilesDisponibles": [{ "id": 1, "nombre": "Super Administrador", "descripcion": "Acceso total al sistema" }]
    }
  ]
}
```

**Usuarios de Prueba:**
| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | Admin2024! | Super Administrador |
| cmartinez | Carlos2024! | Administrador General |
| mrodriguez | Maria2024! | Administrador de Conjunto |
| jperez | Juan2024! | Propietario |
| agarcia | Ana2024! | Residente |
| lgomez | Luis2024! | Multi-role |

---

### POST /v1/contexto/cambiar

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Cambia el contexto activo y genera un nuevo JWT (también actualiza la cookie HttpOnly).

**Request:**
```json
{
  "tipo": "ADMINISTRADORA",
  "entidadId": 1,
  "perfilId": 2
}
```

**Response 200:**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "contexto": {
    "tipo": "ADMINISTRADORA",
    "entidadId": 1,
    "entidadNombre": "Inmobiliaria ABC",
    "perfilId": 2,
    "perfilNombre": "Administrador General",
    "perfilDescripcion": "Administrador de administradora"
  }
}
```

---

## 🔑 Endpoints de Menú

### GET /v1/menu/perfil

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene el árbol de menú filtrado según el perfil activo.

**Headers:**
```
X-Perfil-Id: <perfil_id>
```

**Response 200:**
```json
[
  { "itemId": 1, "nombre": "Dashboard", "tipo": "O", "idPadre": 0, "orden": 1, "controlador": "dashboard", "metodo": "index", "submenus": [] },
  { "itemId": 2, "nombre": "Configuracion", "tipo": "A", "idPadre": 0, "orden": 2, "submenus": [...] }
]
```

---

## 🔑 CRUD Usuarios

### GET /v1/usuarios

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Lista usuarios con filtros opcionales y paginación.

**Query Parameters:**
- `estatus` (opcional) - Filtrar por A/I
- `search` (opcional) - Búsqueda por nombre, documento o email
- `page` (default: 1)
- `limit` (default: 50)

**Response 200:**
```json
{
  "data": [
    { "id": 1, "documento": "ADMIN-001", "nombre": "Super", "apellido": "Admin", "email": "admin@resimanager.com", "telefono": null, "usuario": "admin", "estatus": "A" }
  ],
  "total": 1,
  "page": 1,
  "limit": 50
}
```

### GET /v1/usuarios/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene un usuario por ID.

### PUT /v1/usuarios/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Actualiza datos de un usuario (nombre, apellido, email, teléfono, estatus).

**Request:**
```json
{
  "nombre": "Super",
  "apellido": "Admin",
  "email": "admin@resimanager.com",
  "telefono": "+584121234567",
  "estatus": "A"
}
```

### DELETE /v1/usuarios/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Inactiva un usuario (soft-delete, cambia estatus a 'I').

### GET /v1/usuarios/{id}/perfiles

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene los perfiles del usuario agrupados por contexto (Administradora/Conjunto).

**Response 200:**
```json
{
  "persona": { "id": 1, "nombre": "Super", "apellido": "Admin", "email": "admin@resimanager.com", "estatus": "A" },
  "contextos": [
    {
      "tipo": "ADMINISTRADORA",
      "entidad": { "id": 1, "nombre": "Inmobiliaria ABC" },
      "perfiles": [ { "id": 1, "nombre": "Super Administrador" } ]
    }
  ]
}
```

---

## 🔑 CRUD Perfiles

### GET /v1/perfiles

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Lista perfiles con filtros (estatus, nivel, search) y paginación.

**Query Parameters:**
- `estatus` (opcional) - A/I
- `nivel` (opcional) - 0 (Super Admin), 1 (Admin General), 2 (Admin Conjunto), 3 (Propietario/Residente)
- `search` (opcional) - Búsqueda por nombre o descripción
- `page` (default: 1)
- `limit` (default: 25)

**Response 200:**
```json
{
  "data": [
    { "id": 1, "nombre": "Super Administrador", "descripcion": "Acceso total al sistema", "estatus": "A", "nivel": 0, "fechaCreacion": "2026-01-01T00:00:00", "usuariosAsignados": 1 }
  ],
  "total": 5,
  "page": 1,
  "limit": 25
}
```

### POST /v1/perfiles

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Crea un nuevo perfil.

**Request:**
```json
{
  "nombre": "Contador",
  "descripcion": "Acceso a módulos financieros",
  "nivel": 2
}
```

### GET /v1/perfiles/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene detalle completo: info básica + módulos asignados + permisos + usuarios asignados.

### PUT /v1/perfiles/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Actualiza datos del perfil (nombre, descripción, estatus, nivel).

### DELETE /v1/perfiles/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Inactiva un perfil (soft-delete).

### POST /v1/perfiles/{id}/modulos

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Asigna módulos a un perfil.

**Request:**
```json
{
  "modulos": [1, 2, 3]
}
```

### DELETE /v1/perfiles/{id}/modulos/{moduloId}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Revoca un módulo específico de un perfil (soft-delete).

---

## 🔑 Módulos

### GET /v1/modulos

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Lista módulos del sistema.

**Query Parameters:**
- `nivel` (opcional) - Filtrar por nivel (0-4)

**Response 200:**
```json
[
  { "id": 1, "nombre": "Dashboard", "descripcion": "...", "nivel": 0 },
  { "id": 2, "nombre": "Administradoras", "descripcion": "...", "nivel": 0 }
]
```

---

## 🔑 CRUD Conjuntos

### GET /v1/conjuntos

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Lista conjuntos con filtros (estatus, search) y paginación.

### GET /v1/conjuntos/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene datos básicos de un conjunto.

### GET /v1/conjuntos/{id}/usuarios

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene los usuarios del conjunto con sus perfiles asignados.

### POST /v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Asigna perfiles a un usuario dentro del conjunto.

### DELETE /v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles/{perfilId}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Remueve un perfil de un usuario en el conjunto (soft-delete).

---

## 🔑 CRUD Administradoras

### GET /v1/administradoras

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Lista administradoras con filtros (estatus, search) y paginación.

### GET /v1/administradoras/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene datos básicos de una administradora.

### GET /v1/administradoras/{id}/usuarios

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene los usuarios de la administradora con sus perfiles asignados.

### POST /v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Asigna perfiles a un usuario dentro de la administradora.

### DELETE /v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles/{perfilId}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Remueve un perfil de un usuario en la administradora (soft-delete).

---

## 🔑 PROPIETARIOS (Legacy - /api/owners)

**Estado:** ⚠️ Legacy (ruta antigua, no migrada a /v1)

CRUD completo en `/api/owners` con paginación. Pendiente de migrar a `/v1/propietarios`.

---

## ❌ Endpoints Pendientes de Implementar

### PROPIEDADES (/v1/propiedades)
- ❌ POST - Crear propiedad
- ❌ GET - Listar propiedades
- ❌ GET /{id} - Obtener propiedad
- ❌ PUT /{id} - Actualizar propiedad
- ❌ DELETE /{id} - Eliminar propiedad

### INVITACIONES (/v1/invitaciones)
- ❌ POST - Crear invitación
- ❌ GET - Listar invitaciones
- ❌ GET /{uuid} - Obtener invitación por UUID
- ❌ PUT /{id} - Actualizar invitación
- ❌ DELETE /{id} - Eliminar invitación

### AUDITORÍA
- ❌ Tabla `log_operacion` con campos de auditoría
- ❌ Triggers para registro automático de operaciones

---

## 📊 Códigos de Error

| Código | Descripción |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 204 | No Content - Eliminación exitosa |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Conflicto (ej: duplicado) |
| 422 | Unprocessable Entity - Validación fallida |
| 500 | Internal Server Error |

---

## 📝 Notas Importantes

### Contraseñas en Base64
```bash
echo -n "Admin2024!" | base64
# Resultado: QWRtaW4yMDI0IQ==
```

### Cookie HttpOnly
El JWT se almacena en una cookie `jwt` con las siguientes características:
- `HttpOnly` - No accesible desde JavaScript
- `Secure` - Solo en HTTPS (configurable para desarrollo local)
- `SameSite` - Lax (dev) / None (producción)
- `MaxAge` - 24 horas

### Contexto Multi-tenant
Después del login, el usuario debe cambiar de contexto mediante `/v1/contexto/cambiar`. El nuevo token incluye los claims del contexto activo.

### Paginación
Todos los endpoints de listado usan el mismo formato:
- Parámetros: `page` (default 1), `limit` (default 25-50 según endpoint)
- Respuesta: `{ data: [...], total: N, page: N, limit: N }`

---

**Documento actualizado:** 09 de Mayo de 2026
