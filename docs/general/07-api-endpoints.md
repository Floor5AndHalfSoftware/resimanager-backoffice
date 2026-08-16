# API Endpoints - Documentación

**Última actualización:** 16 de Agosto de 2026  
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
| GET | `/v1/dashboard/stats` | Estadísticas del dashboard |
| POST | `/v1/conjuntos` | Crear conjunto |
| GET | `/v1/conjuntos` | Listar conjuntos |
| GET | `/v1/conjuntos/{id}` | Obtener conjunto |
| PUT | `/v1/conjuntos/{id}` | Actualizar conjunto |
| DELETE | `/v1/conjuntos/{id}` | Inactivar conjunto |
| GET | `/v1/conjuntos/{id}/usuarios` | Usuarios de un conjunto |
| POST | `/v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles` | Asignar perfiles en conjunto |
| DELETE | `/v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles/{perfilId}` | Remover perfil en conjunto |
| POST | `/v1/administradoras` | Crear administradora |
| GET | `/v1/administradoras` | Listar administradoras |
| GET | `/v1/administradoras/{id}` | Obtener administradora |
| PUT | `/v1/administradoras/{id}` | Actualizar administradora |
| DELETE | `/v1/administradoras/{id}` | Inactivar administradora |
| GET | `/v1/administradoras/{id}/usuarios` | Usuarios de una administradora |
| POST | `/v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles` | Asignar perfiles en administradora |
| DELETE | `/v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles/{perfilId}` | Remover perfil en administradora |
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
| POST | `/v1/conjuntos` | Crear conjunto |
| GET | `/v1/conjuntos` | Listar conjuntos |
| GET | `/v1/conjuntos/{id}` | Obtener conjunto por ID |
| PUT | `/v1/conjuntos/{id}` | Actualizar conjunto |
| DELETE | `/v1/conjuntos/{id}` | Inactivar conjunto |
| GET | `/v1/conjuntos/{id}/usuarios` | Usuarios de un conjunto |
| POST | `/v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles` | Asignar perfiles en conjunto |
| DELETE | `/v1/conjuntos/{conjId}/usuarios/{usuarioId}/perfiles/{perfilId}` | Remover perfil en conjunto |
| POST | `/v1/administradoras` | Crear administradora |
| GET | `/v1/administradoras` | Listar administradoras |
| GET | `/v1/administradoras/{id}` | Obtener administradora por ID |
| PUT | `/v1/administradoras/{id}` | Actualizar administradora |
| DELETE | `/v1/administradoras/{id}` | Inactivar administradora |
| GET | `/v1/administradoras/{id}/usuarios` | Usuarios de una administradora |
| POST | `/v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles` | Asignar perfiles en administradora |
| DELETE | `/v1/administradoras/{admId}/usuarios/{usuarioId}/perfiles/{perfilId}` | Remover perfil en administradora |
| GET | `/v1/dashboard/stats` | Obtener estadísticas del dashboard |
| GET | `/v1/propietarios` | Listar propietarios |
| GET | `/v1/propietarios/{conjId}/{perId}` | Obtener propietario |
| POST | `/v1/propietarios` | Crear propietario |
| PUT | `/v1/propietarios/{conjId}/{perId}` | Actualizar propietario |
| DELETE | `/v1/propietarios/{conjId}/{perId}` | Inactivar propietario |
| GET | `/v1/propiedades` | Listar propiedades |
| GET | `/v1/propiedades/{id}` | Obtener propiedad |
| POST | `/v1/propiedades` | Crear propiedad |
| PUT | `/v1/propiedades/{id}` | Actualizar propiedad |
| DELETE | `/v1/propiedades/{id}` | Inactivar propiedad |
| GET | `/v1/propiedades/clases` | Listar clases de propiedad |

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

### POST /v1/conjuntos

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Crea un nuevo conjunto. El ID se genera automáticamente.

**Request:**
```json
{
  "documento": "J-98765432-1",
  "nombre": "Conjunto Residencial Las Flores",
  "telefono": "+582121234567",
  "email": "lasflores@resimanager.com",
  "persContactoId": 1
}
```

### GET /v1/conjuntos/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene datos básicos de un conjunto.

### PUT /v1/conjuntos/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Actualiza datos de un conjunto.

**Request:**
```json
{
  "documento": "J-98765432-1",
  "nombre": "Conjunto Residencial Las Flores (Actualizado)",
  "telefono": "+582121234567",
  "email": "contacto@lasflores.com",
  "persContactoId": 1,
  "estatus": "A"
}
```

### DELETE /v1/conjuntos/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Inactiva un conjunto (soft-delete, cambia estatus a 'I').

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

### POST /v1/administradoras

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Crea una nueva administradora. El ID se genera automáticamente.

**Request:**
```json
{
  "documento": "J-12345678-9",
  "nombre": "Nueva Administradora C.A.",
  "telefono": "+582121234567",
  "email": "contacto@nueva-adm.com"
}
```

### GET /v1/administradoras/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene datos de una administradora. Retorna `AdministradoraDTO` con todos los campos.

**Response 200:**
```json
{
  "id": 1,
  "documento": "J-12345678-9",
  "nombre": "Inmobiliaria ABC",
  "telefono": "+582121234567",
  "email": "contacto@inmobiliariaabc.com",
  "estatus": "A"
}
```

### PUT /v1/administradoras/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Actualiza datos de una administradora.

**Request:**
```json
{
  "documento": "J-12345678-9",
  "nombre": "Inmobiliaria ABC (Actualizada)",
  "telefono": "+582121234567",
  "email": "contacto@abc-updated.com",
  "estatus": "A"
}
```

### DELETE /v1/administradoras/{id}

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Inactiva una administradora (soft-delete, cambia estatus a 'I').

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

## 🔑 PROPIETARIOS (/v1/propietarios)

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

CRUD completo en `/v1/propietarios` (GET, POST, GET/{conjId}/{perId}, PUT/{conjId}/{perId}, DELETE/{conjId}/{perId}) con paginación, en `PropietarioController`. La antigua ruta `/api/owners` y `OwnerController` fueron reemplazados.

## 🔑 PROPIEDADES (/v1/propiedades)

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

CRUD completo en `/v1/propiedades` (GET, POST, GET/{id}, PUT/{id}, DELETE/{id}) y `GET /v1/propiedades/clases`, en `PropiedadController`.

---

## 🔑 Dashboard

### GET /v1/dashboard/stats

**Estado:** ✅ IMPLEMENTADO Y FUNCIONAL

Obtiene estadísticas generales del sistema: conteos reales de entidades (administradoras, conjuntos, usuarios, propiedades, propietarios) y datos mock de facturas e incidencias.

**Response 200:**
```json
{
  "totalAdministradoras": 2,
  "totalConjuntos": 3,
  "totalUsuarios": 6,
  "totalPropiedades": 10,
  "totalPropietarios": 5,
  "totalFacturas": 156,
  "totalFacturasPagadas": 98,
  "totalFacturasPendientes": 58,
  "totalIncidencias": 23,
  "totalIncidenciasAbiertas": 8,
  "totalIncidenciasCerradas": 15
}
```

---

## ❌ Endpoints Pendientes de Implementar

### INVITACIONES (/v1/invitaciones)
- ❌ POST - Crear invitación
- ❌ GET - Listar invitaciones
- ❌ GET /{uuid} - Obtener invitación por UUID
- ❌ PUT /{id} - Actualizar invitación
- ❌ DELETE /{id} - Eliminar invitación
- Nota: solo existe la migración `V2.0.4__CREATE_INVITATION_TABLES.sql`; no hay entidad JPA ni controller.

### MÓDULOS / OPCIONES / ACCIONES (Jerarquía de Permisos)
- ❌ CRUD Opciones por módulo
- ❌ CRUD AccOpcion (acciones disponibles por opción)
- ❌ CRUD Permisos granulares (OpcPerfil + AccOpcPerfil)
- ❌ Middleware de autorización por opción+acción
- Ver `12-estructura-modulos-seguridad.md` para detalle completo de tablas, entidades y estado actual

### AUDITORÍA
- ❌ Tabla `log_operacion` con campos de auditoría
- ❌ Triggers para registro automático de operaciones

### SEGURIDAD ADICIONAL
- ❌ Refresh token (el endpoint de logout sí está implementado)

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
