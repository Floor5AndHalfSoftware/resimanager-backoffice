# Resumen Ejecutivo

## Descripción del Sistema

**ResiManager** es una plataforma de gestión integral diseñada para la administración de condominios, residencias y conjuntos residenciales. El sistema permite gestionar múltiples entidades inmobiliarias bajo una arquitectura multi-tenant con control de acceso jerárquico.

## Propósito

Proporcionar a las administradoras de propiedades una herramienta centralizada para:

- Gestionar múltiples conjuntos residenciales desde una sola plataforma
- Controlar el acceso de usuarios mediante un sistema de permisos granular
- Administrar propiedades, propietarios y relaciones contractuales
- Mantener la seguridad mediante un flujo de invitaciones controlado

## Modelo de Negocio

```
┌─────────────────┐
│  Super Admin    │  ← Control total del sistema
└────────┬────────┘
         │ gestiona
         ▼
┌─────────────────┐
│ Administradora  │  ← Empresa que administra conjuntos
└────────┬────────┘
         │ administra
         ▼
┌─────────────────┐
│    Conjunto     │  ← Residencial/Edificio/Comercial
└────────┬────────┘
         │ contiene
         ▼
┌─────────────────┐
│   Propiedad     │  ← Apartamento/Casa/Local
└────────┬────────┘
         │ pertenece a
         ▼
┌─────────────────┐
│   Propietario   │  ← Persona natural/jurídica
└─────────────────┘
```

## Características Principales

### Gestión Multi-Nivel
- **Super Admin**: Acceso total al sistema
- **Administradora**: Gestiona sus conjuntos asignados
- **Conjunto**: Administra propiedades y residentes
- **Propietario**: Acceso limitado a sus propiedades

### Seguridad por Invitación
- No permite auto-registro
- Flujo de invitación con UUID único
- Vencimiento automático de invitaciones
- Limpieza programada de datos obsoletos

### Control de Acceso Granular (RBAC)
- Perfiles dinámicos por contexto
- Permisos a nivel de módulo, opción y acción
- Menú adaptativo según perfil seleccionado

### Interfaz Moderna
- Basada en AdminLTE 3
- DataTables para gestión de registros
- Ventanas modales para transacciones
- Diseño responsive

## Alcance Funcional

| Módulo | Super Admin | Administradora | Conjunto | Propietario |
|--------|-------------|----------------|----------|-------------|
| Módulos | ✓ | - | - | - |
| Acciones | ✓ | - | - | - |
| Administradoras | ✓ | ✓ | - | - |
| Conjuntos | ✓ | ✓ | ✓ | - |
| Perfiles de acceso | ✓ | ✓ | ✓ | - |
| Usuarios | ✓ | ✓ | ✓ | - |
| Clases de propiedad | - | - | ✓ | - |
| Propiedades | - | - | ✓ | ✓ |
| Propietarios | - | - | ✓ | - |

## Requisitos No Funcionales

- **Disponibilidad**: 99.5% uptime
- **Seguridad**: Encriptación de contraseñas (SHA-256/512)
- **Rendimiento**: Tiempo de respuesta < 2 segundos
- **Escalabilidad**: Soporte para múltiples administradoras simultáneas
- **Auditoría**: Registro de todas las operaciones críticas
