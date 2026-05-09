# Endpoints CRUD - Perfiles

## Descripción

API para la gestión de perfiles de acceso del sistema. Los perfiles definen los niveles de acceso de los usuarios.

## Estructura de Perfil

```json
{
  "id": 1,
  "nombre": "Super Administrador",
  "descripcion": "Acceso total al sistema",
  "estatus": "A",
  "nivel": 0,
  "fecha_creacion": "2024-01-01T10:00:00Z",
  "usuarios_asignados": 5
}
```

## Endpoints

### GET /perfiles

Lista todos los perfiles del sistema.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| estatus | string | Filtrar por estatus (A, I) |
| nivel | int | Filtrar por nivel jerárquico |
| search | string | Búsqueda por nombre |
| page | int | Página |
| limit | int | Registros por página |

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "nombre": "Super Administrador",
      "descripcion": "Acceso total al sistema",
      "estatus": "A",
      "nivel": 0,
      "usuarios_asignados": 1
    },
    {
      "id": 2,
      "nombre": "Administrador General",
      "descripcion": "Administrador de administradora",
      "estatus": "A",
      "nivel": 1,
      "usuarios_asignados": 2
    }
  ],
  "total": 5,
  "page": 1,
  "limit": 25
}
```

---

### GET /perfiles/{id}

Obtiene detalle de un perfil específico.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "id": 1,
  "nombre": "Super Administrador",
  "descripcion": "Acceso total al sistema",
  "estatus": "A",
  "nivel": 0,
  "modulos": [
    { "id": 1, "nombre": "Configuracion Sistema" },
    { "id": 2, "nombre": "Gestion Administradoras" }
  ],
  "permisos": [
    { "modulo_id": 1, "modulo": "Configuracion", "acciones": ["MNJ", "INS", "MOD", "VIS", "EL"] }
  ],
  "usuarios_asignados": 1,
  "fecha_creacion": "2024-01-01T10:00:00Z"
}
```

---

### POST /perfiles

Crea un nuevo perfil.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Contador",
  "descripcion": "Acceso a módulos financieros",
  "nivel": 2
}
```

**Response 201:**
```json
{
  "id": 6,
  "nombre": "Contador",
  "descripcion": "Acceso a módulos financieros",
  "estatus": "A",
  "nivel": 2,
  "message": "Perfil creado correctamente"
}
```

---

### PUT /perfiles/{id}

Actualiza un perfil existente.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Contador Senior",
  "descripcion": "Acceso total a módulos financieros",
  "estatus": "A"
}
```

**Response 200:**
```json
{
  "id": 6,
  "message": "Perfil actualizado correctamente"
}
```

---

### DELETE /perfiles/{id}

Elimina (inactiva) un perfil.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Perfil inactivado correctamente"
}
```

---

### POST /perfiles/{id}/modulos

Asigna módulos a un perfil.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "modulos": [3, 4, 5]
}
```

**Response 200:**
```json
{
  "message": "Módulos asignados correctamente",
  "modulos_asignados": 3
}
```

---

### DELETE /perfiles/{id}/modulos/{moduloId}

Revoca un módulo de un perfil.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Módulo removido del perfil"
}
```

---

## Niveles de Perfil

| Nivel | Nombre | Descripción |
|-------|--------|-------------|
| 0 | Super Administrador | Acceso total al sistema |
| 1 | Administrador General | Gestión de administradora |
| 2 | Administrador de Conjunto | Gestión de conjunto |
| 3 | Propietario | Dueño de propiedad |
| 4 | Residente | Residente/Arrendatario |

---

## Códigos de Error

| Código | Descripción |
|--------|-------------|
| 200 | OK |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found |
| 409 | Conflict - Perfil con usuarios asignados |
