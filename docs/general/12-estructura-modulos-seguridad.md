# Estructura de Módulos, Opciones y Acciones (Seguridad)

**Fecha:** 28 de Mayo de 2026

---

## Jerarquía de Seguridad

El sistema de permisos se organiza en 3 niveles jerárquicos:

```
Módulo (modulo)
  └── Opción (opcion)
        └── Acción (accion)
              └── Perfil (ModPerfil / OpcPerfil / AccOpcPerfil)
```

Cada perfil tiene acceso a ciertos módulos, y dentro de esos módulos puede tener restricciones a nivel de opciones y acciones individuales.

---

## Tablas en Base de Datos

### modulo

Agrupaciones grandes del sistema. Cada módulo tiene un `mod_nivel` que indica qué tipo de usuario puede verlo.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| modid | INTEGER (PK) | ID del módulo |
| mod_nombre | VARCHAR(80) | Nombre del módulo |
| mod_descrip | VARCHAR(120) | Descripción |
| mod_nivel | SMALLINT | Nivel jerárquico: 0=SuperAdmin, 1=Admin General, 2=Admin Conjunto, 3=Propietario |
| mod_sts | VARCHAR(1) | Estatus A/I |

**Semilla actual (10 módulos):**

| ID | Nombre | Nivel |
|----|--------|-------|
| 1 | Configuracion Sistema | 0 |
| 2 | Gestion Administradoras | 0 |
| 3 | Gestion Conjuntos | 1 |
| 4 | Gestion Contratos | 1 |
| 5 | Usuarios Administradora | 1 |
| 6 | Gestion Propiedades | 2 |
| 7 | Gestion Propietarios | 2 |
| 8 | Usuarios Conjunto | 2 |
| 9 | Invitaciones | 2 |
| 10 | Mi Propiedad | 3 |

### opcion

Sub-funcionalidades dentro de cada módulo. Cada opción pertenece a un módulo (`opc_modid`).

| Columna | Tipo | Descripción |
|---------|------|-------------|
| opcid | INTEGER (PK) | ID de la opción |
| opc_modid | INTEGER (FK → modulo) | Módulo al que pertenece |
| opc_nombre | VARCHAR(80) | Nombre de la opción |
| opc_descrip | VARCHAR(120) | Descripción |
| opc_sts | VARCHAR(1) | Estatus A/I |

**Ejemplos de opciones por módulo:**

| Módulo | Opciones posibles |
|--------|-------------------|
| Gestion Administradoras | Listar, Crear, Editar, Asignar Usuarios |
| Gestion Conjuntos | Listar, Crear, Editar, Asignar Usuarios |
| Gestion Propiedades | Listar, Crear, Editar |
| Usuarios Conjunto | Listar, Invitar, Gestionar Perfiles |

### accion

Acciones atómicas que se pueden ejecutar sobre una opción. Son fijas y compartidas entre todos los módulos/opciones.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| accid | INTEGER (PK) | ID de la acción |
| acc_nombre | VARCHAR(80) | Código de la acción |
| acc_descrip | VARCHAR(120) | Descripción |
| acc_sts | VARCHAR(1) | Estatus A/I |

**Semilla actual (5 acciones):**

| ID | Código | Descripción |
|----|--------|-------------|
| 1 | MNJ | Manejar - Acceso total al módulo/opción |
| 2 | INS | Insertar - Crear nuevos registros |
| 3 | MOD | Modificar - Editar registros existentes |
| 4 | VIS | Visualizar - Solo lectura |
| 5 | EL | Eliminar - Borrar registros |

### "AccOpcion"

Define qué acciones están disponibles para cada opción dentro de un módulo.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| aoid | INTEGER (PK) | ID |
| ao_mod_id | INTEGER (FK → modulo) | Módulo |
| ao_opc_id | INTEGER (FK → opcion) | Opción |
| ao_acc_id | INTEGER (FK → accion) | Acción disponible |
| ao_cod_seg | INTEGER (UK) | Código de seguridad único |

**Estado actual:** Vacía — nunca se poblaron datos semilla.

### "ModPerfil"

Módulos asignados a cada perfil. Determina a qué módulos tiene acceso un perfil.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| mpid | INTEGER (PK) | ID |
| mp_prf_id | INTEGER (FK → Perfil) | Perfil |
| mp_mod_id | INTEGER (FK → modulo) | Módulo asignado |
| mp_sts | VARCHAR(1) | Estatus A/I |

**Estado actual:** Poblada con asignaciones básicas. Super Admin (perfil 1) tiene todos los módulos. Los demás perfiles tienen según su nivel.

### "OpcPerfil"

Opciones asignadas a un perfil dentro de un módulo. Permite restringir a nivel de opción.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| opid | INTEGER (PK) | ID |
| op_prf_id | INTEGER (FK → Perfil) | Perfil |
| op_mod_id | INTEGER (FK → modulo) | Módulo |
| op_opc_id | INTEGER (FK → opcion) | Opción asignada |
| op_sts | VARCHAR(1) | Estatus A/I |

**Estado actual:** Vacía — solo se usa `"ModPerfil"`.

### "AccOpcPerfil"

Permiso final: asigna una acción específica sobre una opción a un perfil. Es el nivel más granular.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| aopid | INTEGER (PK) | ID |
| aop_prf_id | INTEGER (FK → Perfil) | Perfil |
| aop_mod_id | INTEGER (FK → modulo) | Módulo |
| aop_opc_id | INTEGER (FK → opcion) | Opción |
| aop_acc_id | INTEGER (FK → accion) | Acción permitida |
| aop_sts | VARCHAR(1) | Estatus A/I |

**Estado actual:** Vacía — nunca se pobló.

---

## Estado Actual del Sistema de Permisos

Actualmente el sistema funciona solo a nivel de **módulos** (`"ModPerfil"`):
- `GET /v1/modulos` lista los módulos disponibles
- Cada perfil tiene módulos asignados
- El menú dinámico se filtra según los módulos del perfil activo

**No se usa** `opcion`, `"AccOpcion"`, `"OpcPerfil"` ni `"AccOpcPerfil"` en la lógica actual.

---

## Lo Que Falta Implementar

### 1. CRUD Opciones
- Endpoints para crear, listar, editar e inactivar opciones dentro de un módulo
- Frontend para administrar opciones por módulo

### 2. Poblar "AccOpcion"
- Definir qué acciones aplican a cada opción
- Migración Flyway con datos semilla
- CRUD para mantener la relación

### 3. CRUD Permisos Granulares
- UI para asignar opciones+acciones a un perfil
- Backend para guardar en `"OpcPerfil"` y `"AccOpcPerfil"`
- Validación de permisos en endpoints (middleware)

### 4. Middleware de Autorización
- Interceptor/anotación que verifique permisos a nivel de opción+acción
- Reemplazar la verificación actual solo por módulo

---

## Migraciones Flyway Relacionadas

| Archivo | Contenido |
|---------|-----------|
| `V2.0.2__CREATE_SECURITY_TABLES.sql` | Creación de todas las tablas de seguridad |
| `V2.0.5__INSERT_BASE_DATA.sql` | Datos semilla: acciones, módulos, módulos por perfil |
| `V2.0.8__ASSIGN_PROFILE_PERMISSIONS.sql` | Asignación de módulos a perfiles no-admin |

---

## Entidades JPA Relacionadas

| Entidad | Tabla | Ubicación |
|---------|-------|-----------|
| Modulo | modulo | entity/Modulo.java |
| Opcion | opcion | entity/Opcion.java |
| Accion | accion | entity/Accion.java |
| AccOpcion | "AccOpcion" | entity/AccOpcion.java |
| ModPerfil | "ModPerfil" | entity/ModPerfil.java |
| OpcPerfil | "OpcPerfil" | entity/OpcPerfil.java |
| AccOpcPerfil | "AccOpcPerfil" | entity/AccOpcPerfil.java |
