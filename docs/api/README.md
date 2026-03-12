# Documentación de API - ResiManager

## Índice de Endpoints

Esta sección contiene la documentación de los endpoints disponibles en el sistema.

### Endpoints Existentes

| Documento | Descripción |
|-----------|-------------|
| [07-api-endpoints.md](../general/07-api-endpoints.md) | Endpoints generales del sistema |

### Endpoints de Seguridad (Pendientes de Implementar)

| Documento | Descripción |
|-----------|-------------|
| [01-CRUD-PERFILES.md](01-CRUD-PERFILES.md) | CRUD de perfiles de acceso |
| [02-CRUD-ASIGNACION-PERFILES.md](02-CRUD-ASIGNACION-PERFILES.md) | Asignar perfiles a usuarios |
| [03-CRUD-MODULOS-OPCIONES-ACCIONES.md](03-CRUD-MODULOS-OPCIONES-ACCIONES.md) | Gestión de módulos, opciones y acciones |
| [04-CRUD-PERMISOS-PERFILES.md](04-CRUD-PERMISOS-PERFILES.md) | Permisos de perfiles sobre opciones |

---

## Resumen de Entidades

### 1. Perfiles (`Perfil`)
- Definición de roles de usuario
- Niveles: Super Admin, Admin General, Admin Conjunto, Propietario, Residente

### 2. Asignación de Perfiles
- **PersAdministradora**: Usuario → Administradora
- **PersConjunto**: Usuario → Conjunto
- **PerfPersAdministradora**: Perfil de usuario en administradora
- **PerfPersConjunto**: Perfil de usuario en conjunto

### 3. Módulos (`Modulo`)
- Unidades de organización del sistema
- Nivel 0: Super Admin
- Nivel 1: Administradora
- Nivel 2: Conjunto
- Nivel 3: Propietario/Residente

### 4. Opciones (`Opcion`)
- Funcionalidades específicas dentro de un módulo

### 5. Acciones (`Accion`)
- MNJ (Manejar - Control total)
- INS (Insertar)
- MOD (Modificar)
- VIS (Visualizar)
- EL (Eliminar)

### 6. Permisos
- **ModPerfil**: Módulos accesibles por perfil
- **OpcPerfil**: Opciones visibles para perfil
- **AccOpcPerfil**: Acciones permitidas por perfil

---

## Data Actual

La data de seguridad actual está en migraciones Flyway:
- `V2.0.5__INSERT_BASE_DATA.sql` - Perfiles, módulos, acciones base
- `V2.0.7__ASSIGN_ADMIN_CONTEXT.sql` - Contexto del admin
- `V2.0.8__ASSIGN_PROFILE_PERMISSIONS.md` - Permisos por perfil
