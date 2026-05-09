# Endpoints CRUD - Permisos de Perfiles

## Descripción

API para gestionar los permisos (acciones) que tiene cada perfil sobre las opciones del sistema.

## Estructura de Permisos

```
PERFIL (Perfil)
  ├── MÓDULOS (ModPerfil)
  │     └── Qué módulos puede acceder
  │
  ├── OPCIONES (OpcPerfil)
  │     └── Qué opciones puede ver
  │
  └── ACCIONES POR OPCIÓN (AccOpcPerfil)
        └── Qué acciones puede realizar en cada opción
```

Un permiso se define por: **perfil + módulo + opción + acción**

---

## Endpoints

### GET /perfiles/{id}/permisos

Obtiene todos los permisos de un perfil.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "perfil": {
    "id": 2,
    "nombre": "Administrador General"
  },
  "modulos": [
    {
      "id": 3,
      "nombre": "Gestion Conjuntos",
      "tiene_acceso": true,
      "opciones": [
        {
          "id": 10,
          "nombre": "Listar Conjuntos",
          "tiene_acceso": true,
          "acciones": [
            { "id": 1, "nombre": "MNJ", "tiene_permiso": true },
            { "id": 2, "nombre": "INS", "tiene_permiso": true },
            { "id": 3, "nombre": "MOD", "tiene_permiso": true },
            { "id": 4, "nombre": "VIS", "tiene_permiso": true },
            { "id": 5, "nombre": "EL", "tiene_permiso": false }
          ]
        }
      ]
    }
  ]
}
```

---

### POST /perfiles/{id}/permisos

Asigna permisos completos a un perfil.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "permisos": [
    {
      "modulo_id": 3,
      "opcion_id": 10,
      "acciones": ["MNJ", "INS", "MOD", "VIS"]
    },
    {
      "modulo_id": 4,
      "opcion_id": 15,
      "acciones": ["MNJ", "VIS"]
    }
  ]
}
```

**Response 200:**
```json
{
  "message": "Permisos actualizados correctamente",
  "modulos_asignados": 2,
  "opciones_asignadas": 2
}
```

---

### PUT /perfiles/{id}/permisos/modulo

Asigna un módulo completo a un perfil (todas las opciones y acciones).

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "modulo_id": 3,
  "opciones": [
    {
      "opcion_id": 10,
      "acciones": ["MNJ", "INS", "MOD", "VIS"]
    },
    {
      "opcion_id": 11,
      "acciones": ["MNJ", "VIS"]
    }
  ]
}
```

**Response 200:**
```json
{
  "message": "Permisos de módulo actualizados",
  "modulo": "Gestion Conjuntos",
  "opciones_actualizadas": 2
}
```

---

### DELETE /perfiles/{id}/permisos/modulo/{moduloId}

Revoca todos los permisos de un módulo para un perfil.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Permisos del módulo revocados"
}
```

---

### POST /perfiles/{id}/permisos/opcion

Agrega permisos específicos sobre una opción.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "modulo_id": 3,
  "opcion_id": 10,
  "acciones": ["INS", "MOD"]
}
```

**Response 200:**
```json
{
  "message": "Permisos de opción agregados",
  "opcion": "Listar Conjuntos",
  "acciones": ["INS", "MOD"]
}
```

---

### DELETE /perfiles/{id}/permisos/opcion/{opcionId}/accion/{accionId}

Revoca una acción específica sobre una opción.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Permiso revoked"
}
```

---

## Asignación Masiva de Permisos

### POST /perfiles/{id}/permisos/copiar

Copia todos los permisos de un perfil a otro.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "perfil_origen_id": 2
}
```

**Response 200:**
```json
{
  "message": "Permisos copiados correctamente",
  "perfil_origen": "Administrador General",
  "perfiles_afectados": 1
}
```

---

### POST /perfiles/{id}/permisos/heredar

Copia permisos de un perfil padre (basado en nivel jerárquico).

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "perfil_padre_id": 1
}
```

**Response 200:**
```json
{
  "message": "Permisos heredados correctamente",
  "perfil_padre": "Super Administrador"
}
```

---

## Permisos por Defecto según Nivel

| Perfil | Nivel | Módulos Asignados |
|--------|-------|-------------------|
| Super Administrador | 0 | Todos |
| Administrador General | 1 | Nivel 1 y 2 |
| Administrador Conjunto | 2 | Nivel 2 |
| Propietario | 3 | Nivel 3 |
| Residente | 3 | Nivel 3 (solo lectura) |

---

## Validaciones

- Un perfil no puede tener más acciones de las definidas en la opción
- Al agregar una acción a una opción, primero debe existir la opción
- Al agregar una opción, primero debe existir el módulo
- No se puede revocar MNJ si hay otras acciones activas (MNJ implica todas)

---

## Códigos de Error

| Código | Descripción |
|--------|-------------|
| 200 | OK |
| 400 | Bad Request - Permiso inválido |
| 401 | Unauthorized |
| 403 | Forbidden - Sin permisos |
| 404 | Perfil, módulo, opción o acción no encontrada |
| 409 | Conflict - Dependencias no cumplidas |

---

## Ejemplo: Flujo Completo de Configuración de Permisos

```bash
# 1. Crear perfil
POST /perfiles
{ "nombre": "Contador", "descripcion": "Acceso financiero", "nivel": 2 }

# 2. Asignar módulo
POST /perfiles/6/permisos/modulo
{ "modulo_id": 11 }

# 3. Asignar opciones específicas con acciones
POST /perfiles/6/permisos/opcion
{
  "modulo_id": 11,
  "opcion_id": 20,
  "acciones": ["VIS", "MOD"]
}

# 4. Verificar permisos
GET /perfiles/6/permisos

# 5. Revocar acción específica
DELETE /perfiles/6/permisos/opcion/20/accion/3
```
