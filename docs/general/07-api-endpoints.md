# API Endpoints

## Base URL

```
Local:   http://localhost:8080/api
Dev:     https://api-dev.resimanager.com/api
Prod:    https://api.resimanager.com/api
```

## Autenticación

Todas las peticiones (excepto login) requieren el header:

```
Authorization: Bearer <jwt_token>
```

## Documentación Swagger

```
http://localhost:8080/swagger-ui.html
```

---

## Endpoints

### Autenticación

#### POST /auth/login

Inicia sesión y obtiene token JWT.

**Request:**
```json
{
  "usuario": "jperez",
  "clave": "password123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "usuario": {
    "id": 123,
    "nombre": "Juan",
    "apellido": "Pérez",
    "usuario": "jperez"
  },
  "administradoras": [
    { "id": 1, "nombre": "Inmobiliaria ABC" }
  ],
  "conjuntos": [
    { "id": 5, "nombre": "Residencial Las Flores" }
  ],
  "perfiles": [
    { "id": 3, "nombre": "A- Administrador" }
  ]
}
```

**Response 401:**
```json
{
  "error": "Credenciales inválidas"
}
```

---

#### POST /auth/logout

Cierra la sesión del usuario.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Sesión cerrada correctamente"
}
```

---

#### POST /auth/refresh

Renueva el token JWT.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

### Menú

#### GET /menu

Obtiene las opciones del menú según el perfil seleccionado.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| perfil_id | int | Sí | ID del perfil seleccionado |
| conjunto_id | int | No | ID del conjunto (si aplica) |
| administradora_id | int | No | ID de administradora (si aplica) |

**Response 200:**
```json
{
  "menu": [
    {
      "id": 1,
      "nombre": "Super Admin",
      "tipo": "A",
      "estado": "A",
      "modulo": null,
      "submenus": [
        {
          "id": 3,
          "nombre": "Modulo",
          "tipo": "0",
          "estado": "A",
          "modulo": "Super-Admin",
          "controlador": "ccontrolador",
          "metodo": "index",
          "submenus": []
        },
        {
          "id": 4,
          "nombre": "Acciones",
          "tipo": "0",
          "estado": "A",
          "modulo": "Super-Admin",
          "controlador": "ccontrolador",
          "metodo": "index",
          "submenus": []
        }
      ]
    }
  ]
}
```

---

### Invitaciones

#### GET /invitaciones

Lista todas las invitaciones según el contexto del usuario.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| estatus | string | Filtrar por estatus: A, V, U |
| page | int | Número de página |
| limit | int | Registros por página |

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "email": "nuevo@usuario.com",
      "fecha_creacion": "2024-09-01T10:00:00Z",
      "fecha_vencimiento": "2024-09-08T10:00:00Z",
      "estatus": "A",
      "creado_por": "Juan Pérez"
    }
  ],
  "total": 15,
  "page": 1,
  "limit": 25
}
```

---

#### POST /invitaciones

Crea una nueva invitación.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "email": "nuevo@usuario.com",
  "tipo": "conjunto",
  "dias_vigencia": 7,
  "conjunto_id": 5,
  "perfiles": [3, 4],
  "propiedades": [101, 102]
}
```

**Response 201:**
```json
{
  "id": 1,
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "email": "nuevo@usuario.com",
  "link": "https://resimanager.com/invitacion/550e8400-e29b-41d4-a716-446655440000",
  "fecha_vencimiento": "2024-09-08T10:00:00Z"
}
```

---

#### GET /invitacion/{uuid}

Obtiene los datos de una invitación para mostrar el formulario de registro.

**Sin autenticación requerida**

**Response 200:**
```json
{
  "email": "nuevo@usuario.com",
  "tipo": "conjunto",
  "contexto": {
    "conjunto": {
      "id": 5,
      "nombre": "Residencial Las Flores"
    },
    "perfiles": [
      { "id": 3, "nombre": "Administrador" }
    ],
    "propiedades": [
      { "id": 101, "codigo": "A-101" }
    ]
  }
}
```

**Response 404:**
```json
{
  "error": "Invitación no encontrada"
}
```

**Response 410:**
```json
{
  "error": "La invitación ha expirado"
}
```

---

### Usuarios (Persona)

#### GET /usuarios

Lista usuarios según el contexto.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| search | string | Búsqueda por nombre/email |
| estatus | string | Filtrar por estatus |
| page | int | Número de página |
| limit | int | Registros por página |

**Response 200:**
```json
{
  "data": [
    {
      "id": 123,
      "documento": "V-12345678",
      "nombre": "Juan",
      "apellido": "Pérez",
      "email": "juan@email.com",
      "usuario": "jperez",
      "estatus": "A",
      "fecha_creacion": "2024-01-15T10:00:00Z"
    }
  ],
  "total": 38,
  "page": 1,
  "limit": 25
}
```

---

#### GET /usuarios/{id}

Obtiene detalle de un usuario.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "id": 123,
  "documento": "V-12345678",
  "tipo_documento": "V",
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@email.com",
  "usuario": "jperez",
  "telefono": "+58 412 1234567",
  "direccion": "Av. Principal, Edif. A",
  "estatus": "A",
  "administradoras": [
    { "id": 1, "nombre": "Inmobiliaria ABC" }
  ],
  "conjuntos": [
    { "id": 5, "nombre": "Residencial Las Flores" }
  ],
  "perfiles": [
    { "id": 3, "nombre": "Administrador", "contexto": "conjunto" }
  ]
}
```

---

#### POST /usuarios

Crea un nuevo usuario (desde administrador, no desde invitación).

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "documento": "V-12345678",
  "tipo_documento": "V",
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@email.com",
  "usuario": "jperez",
  "clave": "password123",
  "telefono": "+58 412 1234567",
  "direccion": "Av. Principal, Edif. A"
}
```

**Response 201:**
```json
{
  "id": 124,
  "nombre": "Juan",
  "apellido": "Pérez",
  "usuario": "jperez"
}
```

---

#### PUT /usuarios/{id}

Actualiza un usuario existente.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Juan Carlos",
  "telefono": "+58 412 7654321"
}
```

**Response 200:**
```json
{
  "id": 123,
  "message": "Usuario actualizado correctamente"
}
```

---

#### DELETE /usuarios/{id}

Elimina (inactiva) un usuario.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Usuario inactivado correctamente"
}
```

---

### Conjuntos

#### GET /conjuntos

Lista conjuntos accesibles para el usuario.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "data": [
    {
      "id": 5,
      "documento": "J-123456789",
      "nombre": "Residencial Las Flores",
      "tipo": "R",
      "direccion": "Urbanización Las Flores",
      "estatus": "A",
      "total_propiedades": 120,
      "propietarios_activos": 95
    }
  ],
  "total": 3
}
```

---

#### GET /conjuntos/{id}

Obtiene detalle de un conjunto.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "id": 5,
  "documento": "J-123456789",
  "nombre": "Residencial Las Flores",
  "tipo": "R",
  "direccion": "Urbanización Las Flores",
  "estatus": "A",
  "clases_propiedad": [
    { "id": 1, "nombre": "Apartamento Tipo A" },
    { "id": 2, "nombre": "Apartamento Tipo B" }
  ],
  "administradora": {
    "id": 1,
    "nombre": "Inmobiliaria ABC"
  }
}
```

---

#### POST /conjuntos

Crea un nuevo conjunto.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "documento": "J-987654321",
  "nombre": "Centro Comercial Plaza",
  "tipo": "C",
  "direccion": "Av. Principal, Centro"
}
```

**Response 201:**
```json
{
  "id": 10,
  "nombre": "Centro Comercial Plaza"
}
```

---

#### PUT /conjuntos/{id}

Actualiza un conjunto.

**Headers:** `Authorization: Bearer <token>`

---

#### DELETE /conjuntos/{id}

Inactiva un conjunto.

**Headers:** `Authorization: Bearer <token>`

---

### Propiedades

#### GET /conjuntos/{conjunto_id}/propiedades

Lista propiedades de un conjunto.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| search | string | Búsqueda por código |
| clase_id | int | Filtrar por clase de propiedad |
| estatus | string | Filtrar por estatus |

**Response 200:**
```json
{
  "data": [
    {
      "id": 101,
      "codigo": "A-101",
      "piso": "1",
      "mt2": 85.50,
      "clase": "Apartamento Tipo A",
      "estatus": "A",
      "propietarios": [
        { "id": 123, "nombre": "Juan Pérez", "tipo": "P" }
      ]
    }
  ],
  "total": 120
}
```

---

#### POST /conjuntos/{conjunto_id}/propiedades

Crea una nueva propiedad.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "codigo": "A-102",
  "piso": "1",
  "mt2": 85.50,
  "clase_id": 1
}
```

---

#### PUT /propiedades/{id}

Actualiza una propiedad.

**Headers:** `Authorization: Bearer <token>`

---

#### POST /propiedades/{id}/propietarios

Asigna propietario a una propiedad.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "persona_id": 123,
  "tipo": "P",
  "pct_participacion": 100,
  "fecha_desde": "2024-01-01"
}
```

---

#### DELETE /propiedades/{id}/propietarios/{persona_id}

Desvincula propietario de una propiedad.

**Headers:** `Authorization: Bearer <token>`

---

### Perfiles

#### GET /perfiles

Lista perfiles según contexto.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "data": [
    {
      "id": 3,
      "nombre": "Administrador Conjunto",
      "descripcion": "Acceso total al conjunto",
      "estatus": "A",
      "usuarios_asignados": 5
    }
  ]
}
```

---

#### POST /perfiles

Crea un nuevo perfil.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Contador",
  "descripcion": "Acceso a módulos financieros",
  "permisos": [
    { "modulo_id": 1, "opcion_id": 1, "accion_id": 4 },
    { "modulo_id": 2, "opcion_id": 3, "accion_id": 1 }
  ]
}
```

---

#### PUT /perfiles/{id}

Actualiza un perfil y sus permisos.

**Headers:** `Authorization: Bearer <token>`

---

## Códigos de Error

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
| 410 | Gone - Recurso expirado |
| 422 | Unprocessable Entity - Validación fallida |
| 500 | Internal Server Error - Error del servidor |

## Formato de Errores

```json
{
  "error": "ValidationError",
  "message": "Los datos proporcionados no son válidos",
  "details": [
    {
      "field": "email",
      "message": "El email no tiene un formato válido"
    },
    {
      "field": "documento",
      "message": "El documento ya está registrado"
    }
  ]
}
```

## Paginación

Parámetros estándar:

| Parámetro | Default | Máximo |
|-----------|---------|--------|
| page | 1 | - |
| limit | 25 | 100 |

Respuesta:

```json
{
  "data": [...],
  "total": 150,
  "page": 1,
  "limit": 25,
  "total_pages": 6
}
```

## Filtros y Búsqueda

### Búsqueda genérica

```
GET /usuarios?search=juan
```

### Filtros específicos

```
GET /propiedades?estatus=A&clase_id=1
```

### Ordenamiento

```
GET /usuarios?sort=nombre&order=asc
GET /propiedades?sort=codigo&order=desc
```
