# ResiManager - Documentación de Especificación

Sistema de gestión integral para condominios, residencias y conjuntos residenciales.

## Estructura de Documentos

| Archivo | Descripción |
|---------|-------------|
| [01-resumen-ejecutivo.md](./01-resumen-ejecutivo.md) | Visión general del sistema |
| [02-arquitectura.md](./02-arquitectura.md) | Stack tecnológico y componentes |
| [03-modelo-datos.md](./03-modelo-datos.md) | Estructura de base de datos |
| [04-seguridad.md](./04-seguridad.md) | RBAC, roles y permisos |
| [05-flujos-proceso.md](./05-flujos-proceso.md) | Procesos de negocio |
| [06-interfaz-usuario.md](./06-interfaz-usuario.md) | UX/UI y componentes |
| [07-api-endpoints.md](./07-api-endpoints.md) | Endpoints del backend |
| [08-consideraciones-tecnicas.md](./08-consideraciones-tecnicas.md) | Multimoneda, auditoría, mantenimiento |
| [09-analisis-desarrollo.md](./09-analisis-desarrollo.md) | Estado actual vs especificación |

## Stack Tecnológico Actual

```
Frontend:  React 19 + Vite + AdminLTE 3
Backend:   Spring Boot 3 + Java 17
Database:  PostgreSQL + Flyway
Deploy:    Docker + Vercel
```

## Estado del Proyecto

Basado en análisis de código (ver [09-analisis-desarrollo.md](./09-analisis-desarrollo.md)):

| Módulo | Completitud | Estado |
|--------|-------------|--------|
| Backend - Entidades JPA | 60% | ⚠️ Parcial |
| Backend - Autenticación | 40% | ⚠️ Hardcodeado |
| Backend - APIs CRUD | 5% | ❌ Pendiente |
| Frontend - Login | 30% | ⚠️ Sin API |
| Frontend - Dashboard | 20% | ⚠️ Estático |
| Frontend - Menú | 50% | ⚠️ JSON local |
| Base de Datos | 10% | ❌ Sin migraciones |

### Bloqueantes Críticos

1. **Migraciones de BD** - Las tablas no coinciden con entidades JPA
2. **UserService** - Conectar con tabla Persona real (no hardcodeado)
3. **AuthContext** - Manejo de sesión en frontend
