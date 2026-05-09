# Endpoints CRUD - Módulos, Opciones y Acciones

## Descripción

API para la gestión de la estructura de seguridad del sistema: módulos, opciones y acciones.

## Jerarquía de Seguridad

```
MÓDULO (modulo)
  ├── OPCIÓN (opcion)
  │     └── ACCIÓN (accion)
  │           └── ACCIÓN POR OPCIÓN (AccOpcion)
  │
  └── MENÚ (menu_item)
```

Un **Módulo** es la unidad más grande de organización (ej: "Gestión de Usuarios").
Una **Opción** es una funcionalidad específica dentro de un módulo (ej: "Listar Usuarios").
Una **Acción** es lo que se puede hacer sobre una opción (ej: "Crear", "Editar", "Eliminar").

---

## Endpoints - Módulos

### GET /modulos

Lista todos los módulos del sistema.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| nivel | int | Filtrar por nivel (0=SuperAdmin, 1=Admin, 2=Conjunto) |
| estatus | string | Filtrar por estatus |

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "nombre": "Configuracion Sistema",
      "descripcion": "Configuracion general del sistema",
      "nivel": 0,
      "estatus": "A",
      "opciones_count": 5
    },
    {
      "id": 3,
      "nombre": "Gestion Conjuntos",
      "descripcion": "Administracion de conjuntos residenciales",
      "nivel": 1,
      "estatus": "A",
      "opciones_count": 8
    }
  ],
  "total": 10
}
```

---

### GET /modulos/{id}

Obtiene detalle de un módulo.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "id": 3,
  "nombre": "Gestion Conjuntos",
  "descripcion": "Administracion de conjuntos residenciales",
  "nivel": 1,
  "estatus": "A",
  "opciones": [
    {
      "id": 1,
      "nombre": "Listar Conjuntos",
      "descripcion": "Ver lista de conjuntos"
    },
    {
      "id": 2,
      "nombre": "Crear Conjunto",
      "descripcion": "Crear nuevo conjunto"
    }
  ],
  "perfiles_asignados": ["Administrador General", "Administrador de Conjunto"]
}
```

---

### POST /modulos

Crea un nuevo módulo.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Gestión de Pagos",
  "descripcion": "Administración de pagos y cobros",
  "nivel": 2
}
```

**Response 201:**
```json
{
  "id": 11,
  "nombre": "Gestión de Pagos",
  "message": "Módulo creado correctamente"
}
```

---

### PUT /modulos/{id}

Actualiza un módulo.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Gestión de Pagos y Cobros",
  "descripcion": "Administración completa de pagos",
  "estatus": "A"
}
```

**Response 200:**
```json
{
  "id": 11,
  "message": "Módulo actualizado correctamente"
}
```

---

### DELETE /modulos/{id}

Elimina (inactiva) un módulo.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Módulo inactivado correctamente"
}
```

---

## Endpoints - Opciones

### GET /modulos/{moduloId}/opciones

Lista opciones de un módulo.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "nombre": "Listar Conjuntos",
      "descripcion": "Ver lista de conjuntos",
      "estatus": "A",
      "acciones_disponibles": ["MNJ", "INS", "MOD", "VIS", "EL"]
    }
  ]
}
```

---

### POST /modulos/{moduloId}/opciones

Crea una opción en un módulo.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Reportes de Conjuntos",
  "descripcion": "Ver reportes de conjuntos"
}
```

**Response 201:**
```json
{
  "id": 15,
  "nombre": "Reportes de Conjuntos",
  "message": "Opción creada correctamente"
}
```

---

### PUT /opciones/{id}

Actualiza una opción.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "nombre": "Reportes de Gestión",
  "descripcion": "Ver reportes de gestión de conjuntos"
}
```

**Response 200:**
```json
{
  "message": "Opción actualizada correctamente"
}
```

---

### DELETE /opciones/{id}

Elimina una opción.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Opción inactivada correctamente"
}
```

---

## Endpoints - Acciones

### GET /acciones

Lista todas las acciones disponibles.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "data": [
    { "id": 1, "nombre": "MNJ", "descripcion": "Manejar - Acceso total" },
    { "id": 2, "nombre": "INS", "descripcion": "Insertar - Crear nuevos registros" },
    { "id": 3, "nombre": "MOD", "descripcion": "Modificar - Editar registros" },
    { "id": 4, "nombre": "VIS", "descripcion": "Visualizar - Solo lectura" },
    { "id": 5, "nombre": "EL", "descripcion": "Eliminar - Borrar registros" }
  ]
}
```

---

### POST /opciones/{opcionId}/acciones

Asigna acciones a una opción.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "acciones": [2, 3, 4]
}
```

**Response 200:**
```json
{
  "message": "Acciones asignadas a la opción",
  "acciones_asignadas": ["INS", "MOD", "VIS"]
}
```

---

### DELETE /opciones/{opcionId}/acciones/{accionId}

Revoca una acción de una opción.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Acción removida de la opción"
}
```

---

## Niveles de Módulos

| Nivel | Nombre | Descripción |
|-------|--------|-------------|
| 0 | Super Admin | Solo accesible por Super Administrador |
| 1 | Administradora | Accessible por Admin General |
| 2 | Conjunto | Accessible por Admin de Conjunto |
| 3 | Propietario | Accessible por Propietario/Residente |

---

## Códigos de Error

| Código | Descripción |
|--------|-------------|
| 200 | OK |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | No encontrado |
| 409 | Conflict - Tiene dependencias |
