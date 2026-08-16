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
| Backend - Entidades JPA | 100% | ✅ Completa |
| Backend - Autenticación | 100% | ✅ JWT + cookie HttpOnly |
| Backend - APIs CRUD | 95% | ✅ Usuarios, Perfiles, Conjuntos, Administradoras, Propiedades, Propietarios |
| Frontend - Login | 100% | ✅ Con API |
| Frontend - Dashboard | 95% | ✅ Dinámico (facturas/incidencias mock) |
| Frontend - Menú | 95% | ✅ Dinámico por perfil |
| Base de Datos | 100% | ✅ Flyway migraciones |

### Pendientes principales

1. **Sistema de Invitaciones** - Solo migración `V2.0.4`; sin entidad, controller, service ni frontend
2. **Auditoría** - Tabla `log_operacion` y triggers sin implementar
3. **Jerarquía Módulos/Opciones/Acciones** - Solo `GET /v1/modulos`; sin middleware de autorización granular
4. **Tests** - 0% en backend y frontend
5. **Refresh token** - No implementado (logout sí está)
