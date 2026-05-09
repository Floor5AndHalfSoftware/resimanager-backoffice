# Endpoints CRUD - Asignación de Perfiles a Usuarios

## Descripción

API para gestionar la asignación de perfiles a usuarios dentro de un contexto específico (Administradora o Conjunto).

## Modelos de Datos

### Asignación en Administradora

Un usuario puede tener múltiples perfiles dentro de una administradora.

```json
{
  "id": 1,
  "persona": {
    "id": 2,
    "nombre": "Carlos",
    "apellido": "Martínez"
  },
  "administradora": {
    "id": 1,
    "nombre": "Inmobiliaria ABC"
  },
  "perfiles": [
    { "id": 2, "nombre": "Administrador General" }
  ]
}
```

### Asignación en Conjunto

Un usuario puede tener múltiples perfiles dentro de un conjunto.

```json
{
  "id": 1,
  "persona": {
    "id": 3,
    "nombre": "María",
    "apellido": "Rodríguez"
  },
  "conjunto": {
    "id": 1,
    "nombre": "Residencial Las Flores"
  },
  "perfiles": [
    { "id": 3, "nombre": "Administrador de Conjunto" }
  ]
}
```

---

## Endpoints - Administradora

### GET /administradoras/{id}/usuarios

Lista usuarios asignados a una administradora.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "data": [
    {
      "persona": {
        "id": 2,
        "documento": "V-12345678",
        "nombre": "Carlos",
        "apellido": "Martínez",
        "email": "carlos@inmobiliariaabc.com"
      },
      "perfiles": [
        { "id": 2, "nombre": "Administrador General" }
      ],
      "estatus": "A"
    }
  ],
  "total": 1
}
```

---

### POST /administradoras/{id}/usuarios/{usuarioId}/perfiles

Asigna uno o más perfiles a un usuario en una administradora.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "perfiles": [2]
}
```

**Response 200:**
```json
{
  "message": "Perfiles asignados correctamente",
  "perfiles_asignados": ["Administrador General"]
}
```

---

### DELETE /administradoras/{id}/usuarios/{usuarioId}/perfiles/{perfilId}

Revoca un perfil de un usuario en una administradora.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Perfil removido del usuario"
}
```

---

## Endpoints - Conjunto

### GET /conjuntos/{id}/usuarios

Lista usuarios asignados a un conjunto.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "data": [
    {
      "persona": {
        "id": 3,
        "documento": "V-87654321",
        "nombre": "María",
        "apellido": "Rodríguez",
        "email": "maria@lasflores.com"
      },
      "perfiles": [
        { "id": 3, "nombre": "Administrador de Conjunto" },
        { "id": 4, "nombre": "Propietario" }
      ],
      "estatus": "A"
    }
  ],
  "total": 1
}
```

---

### POST /conjuntos/{id}/usuarios/{usuarioId}/perfiles

Asigna uno o más perfiles a un usuario en un conjunto.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "perfiles": [3, 4]
}
```

**Response 200:**
```json
{
  "message": "Perfiles asignados correctamente",
  "perfiles_asignados": ["Administrador de Conjunto", "Propietario"]
}
```

---

### DELETE /conjuntos/{id}/usuarios/{usuarioId}/perfiles/{perfilId}

Revoca un perfil de un usuario en un conjunto.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "message": "Perfil removido del usuario"
}
```

---

## Endpoints - Usuario (Vista Global)

### GET /usuarios/{id}/perfiles

Obtiene todos los perfiles de un usuario en todos sus contextos.

**Headers:** `Authorization: Bearer <token>`

**Response 200:**
```json
{
  "persona": {
    "id": 2,
    "nombre": "Carlos",
    "apellido": "Martínez"
  },
  "contextos": [
    {
      "tipo": "ADMINISTRADORA",
      "entidad": {
        "id": 1,
        "nombre": "Inmobiliaria ABC"
      },
      "perfiles": [
        { "id": 2, "nombre": "Administrador General" }
      ]
    },
    {
      "tipo": "CONJUNTO",
      "entidad": {
        "id": 1,
        "nombre": "Residencial Las Flores"
      },
      "perfiles": [
        { "id": 3, "nombre": "Administrador de Conjunto" }
      ]
    }
  ]
}
```

---

## Flujo de Asignación

```
1. Admin crea usuario (si no existe)
   POST /usuarios

2. Admin relaciona usuario con administradora/conjunto
   POST /administradoras/{id}/usuarios
   POST /conjuntos/{id}/usuarios

3. Admin asigna perfiles al usuario en ese contexto
   POST /administradoras/{id}/usuarios/{userId}/perfiles
   POST /conjuntos/{id}/usuarios/{userId}/perfiles

4. Usuario puede acceder con los perfiles asignados
```

---

## Códigos de Error

| Código | Descripción |
|--------|-------------|
| 200 | OK |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized |
| 403 | Forbidden - Sin permisos |
| 404 | Usuario, administradora o conjunto no encontrado |
| 409 | Conflict - Perfil ya asignado |
