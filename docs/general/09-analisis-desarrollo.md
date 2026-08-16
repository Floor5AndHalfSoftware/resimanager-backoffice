# Análisis de Desarrollo vs Especificación

**Fecha:** 16 de Agosto de 2026  
**Versión:** 3.3 (CRUD Propiedades y Propietarios; verificación contra código real)  

---

## 📊 Resumen Ejecutivo

| Aspecto | Estado Anterior (01-May) | Estado Actual (09-May) | Cambio |
|---------|-------------------------|------------------------|--------|
| Backend - Autenticación | ✅ 95% | ✅ 100% | Cookie HttpOnly agregada |
| Backend - Contexto Multi-tenant | ✅ 90% | ✅ 100% | Cookie se actualiza en cambio de contexto |
| Backend - Menú Dinámico | ✅ 85% | ✅ 95% | Funcional |
| Backend - APIs CRUD | ⚠️ 20% | ✅ 95% | + CRUD completo Conjuntos, Administradoras, Propiedades y Propietarios |
| Backend - Dashboard | ❌ 0% | ✅ 95% | + DashboardController + DashboardService |
| Backend - Entidades | ✅ 100% | ✅ 100% | Sin cambios |
| Backend - BD + Migraciones | ✅ 100% | ✅ 100% | Sin cambios |
| Frontend - Páginas de negocio | ❌ 20% | ✅ 88% | 18 páginas (CRUD completo incluyendo Propiedades y Propietarios) |
| **TOTAL PROYECTO** | **~55%** | **~90%** | **+35%** |

---

## 1. Backend - Estado Actual

### 1.1 Endpoints Implementados (47)

| Categoría | Endpoints | Archivo |
|-----------|-----------|---------|
| Autenticación | POST /v1/login | LoginController.java |
| Contexto | POST /v1/contexto/cambiar | ContextoController.java |
| Menú | GET /v1/menu/perfil | ViewsController.java |
| Dashboard | GET /v1/dashboard/stats | DashboardController.java |
| Usuarios | GET, GET/{id}, PUT/{id}, DELETE/{id}, GET/{id}/perfiles | UsuarioController.java |
| Perfiles | GET, POST, GET/{id}, PUT/{id}, DELETE/{id}, POST/{id}/modulos, DELETE/{id}/modulos/{moduloId} | PerfilController.java |
| Módulos | GET | ModuloController.java |
| Conjuntos | GET, GET/{id}, POST, PUT/{id}, DELETE/{id}, GET/{id}/usuarios, POST/{conjId}/usuarios/{userId}/perfiles, DELETE/{conjId}/usuarios/{userId}/perfiles/{perfilId} | ConjuntoController.java |
| Administradoras | GET, GET/{id}, POST, PUT/{id}, DELETE/{id}, GET/{id}/usuarios, POST/{admId}/usuarios/{userId}/perfiles, DELETE/{admId}/usuarios/{userId}/perfiles/{perfilId} | AdministradoraController.java |
| Propiedades | GET, GET/{id}, POST, PUT/{id}, DELETE/{id}, GET/clases | PropiedadController.java |
| Propietarios | GET, GET/{conjId}/{perId}, POST, PUT/{conjId}/{perId}, DELETE/{conjId}/{perId} | PropietarioController.java |

### 1.2 Servicios (13)

| Servicio | Estado | Métodos principales |
|----------|--------|-------------------|
| UserService | ✅ | loadUserByUsername(), getUserByUsername() |
| UsuarioService | ✅ **Nuevo** | getUsuarios(), getUsuarioById(), updateUsuario(), deleteUsuario(), getUsuarioPerfiles() |
| PerfilService | ✅ **Nuevo** | getPerfiles(), getPerfilById(), createPerfil(), updatePerfil(), deletePerfil(), asignarModulos(), revocarModulo() |
| AdministradoraService | ✅ **Nuevo** | getAdministradoras(), getAdministradoraById(), getUsuarios(), asignarPerfiles(), removerPerfil() |
| ConjuntoService | ✅ **Nuevo** | getConjuntos(), getConjuntoById(), getUsuarios(), asignarPerfiles(), removerPerfil() |
| DashboardService | ✅ **Nuevo** | getStats() |
| MenuService | ✅ | menus(), menusByPerfil(), buildMenuHierarchy() |
| ModuloService | ✅ | getModulos() |
| ContextoService | ✅ | getContextosDisponibles(), validarYConstruirContexto() |
| JwtService | ✅ | generateToken(), generateTokenWithUserInfo(), generateTokenWithContext() |
| PermissionService | ⚠️ | hasPermission() - sin middleware automático |
| PropiedadService | ✅ **Nuevo** | getPropiedades(), getPropiedadById(), createPropiedad(), updatePropiedad(), deletePropiedad(), getClasesPropiedad() |
| PropietarioService | ✅ **Nuevo** | getPropietarios(), getPropietarioById(), createPropietario(), updatePropietario(), deletePropietario() |

### 1.3 Repositorios (16)

| Repositorio | Estado |
|-------------|--------|
| PersonaRepository | ✅ |
| PerfilRepository | ✅ **Nuevo** |
| ModuloRepository | ✅ **Nuevo** |
| ModPerfilRepository | ✅ **Nuevo** |
| AdministradoraRepository | ✅ |
| ConjuntoRepository | ✅ |
| MenuItemRepository | ✅ |
| AccOpcPerfilRepository | ✅ |
| PerfPersAdministradoraRepository | ✅ |
| PerfPersConjuntoRepository | ✅ |
| PersAdministradoraRepository | ✅ |
| PersConjuntoRepository | ✅ |
| PropiedadRepository | ✅ **Nuevo** |
| PropietarioRepository | ✅ **Nuevo** |
| ClaseDePropiedadRepository | ✅ **Nuevo** |
| UserRepository | ✅ |

### 1.4 DTOs (31)

DTOs para todos los endpoints: Login, Contexto, Menú, Usuario, Perfil, Módulo, Conjunto, Administradora, Propiedad, Propietario.

### 1.5 Seguridad

- ✅ JWT con cookie HttpOnly (Secure, SameSite configurable)
- ✅ Autenticación por header (backward compatible)
- ✅ CORS con allowCredentials=true
- ✅ Rate limiting (5 intentos máximos por usuario)
- ✅ Exception handlers globales

---

## 2. Frontend - Estado Actual

### 2.1 Páginas Implementadas (20)

| Página | Archivo | Funcionalidad |
|--------|---------|---------------|
| Login | LoginPage.jsx + Login.jsx | Autenticación con cookie HttpOnly |
| ContextSelector | ContextSelectorPage.jsx | Selección de contexto multi-tenant |
| Dashboard | DashboardPage.jsx | Layout con menú dinámico |
| Home | HomePage.jsx | Dashboard dinámico con estadísticas reales |
| Usuarios | UsuariosPage.jsx | Listado con filtros y paginación |
| UsuarioForm | UsuarioFormPage.jsx | Edición de usuario |
| Perfiles | perfiles/PerfilesPage.jsx | Listado con filtros y paginación |
| PerfilForm | perfiles/PerfilFormPage.jsx | Creación/edición de perfil |
| PerfilDetail | perfiles/PerfilDetailPage.jsx | Detalle con módulos y permisos |
| Administradoras | AdministradorasPage.jsx | Listado + CRUD (editar, inactivar) |
| AdministradoraForm | AdministradoraFormPage.jsx | Creación/edición de administradora |
| Conjuntos | ConjuntosPage.jsx | Listado + CRUD (editar, inactivar) |
| ConjuntoForm | ConjuntoFormPage.jsx | Creación/edición de conjunto |
| Propiedades | PropiedadesPage.jsx | Listado + CRUD de propiedades |
| PropiedadForm | PropiedadFormPage.jsx | Creación/edición de propiedad |
| Propietarios | PropertiesPage.jsx | Listado + CRUD de propietarios |
| AdministradoraUsuarios | asignaciones/AdministradoraUsuariosPage.jsx | Usuarios de administradora |
| ConjuntoUsuarios | asignaciones/ConjuntoUsuariosPage.jsx | Usuarios de conjunto |
| AsignarPerfiles | asignaciones/AsignarPerfilesPage.jsx | Asignación de perfiles |
| UsuarioPerfiles | asignaciones/UsuarioPerfilesPage.jsx | Perfiles globales del usuario |

### 2.2 Componentes Compartidos

- ✅ PageLayout - Wrapper con breadcrumbs
- ✅ DataTable - Tabla con loading/error/empty states
- ✅ Toast - Notificaciones animadas
- ✅ 5 Form components (FormInput, FormCheckbox, FormRadio, FormSelect, FormTextarea)

### 2.3 Capa de API (api.js)

50 funciones que cubren todos los endpoints del backend (incluyendo Propiedades y Propietarios), con manejo de errores unificado.

---

## 3. Lo que Aún Falta

### Crítico
- ❌ **Tests** - 0% en backend y frontend (solo `spring-boot-starter-test` declarado en `pom.xml`; no hay `src/test` ni casos; el SPA no tiene script de test)
- ❌ Auditoría de operaciones - tabla `log_operacion` y triggers sin implementar
- ❌ Sistema de invitaciones - solo migración `V2.0.4__CREATE_INVITATION_TABLES.sql`; sin entidad JPA, controller, service ni frontend

### Importante
- ❌ **CRUD Módulos/Opciones/Acciones** — ver `12-estructura-modulos-seguridad.md`. Solo `GET /v1/modulos`; tablas `AccOpcion`, `OpcPerfil` y `AccOpcPerfil` sin poblar; sin middleware de autorización por opción+acción (`PermissionService.hasPermission()` sin enganche automático)
- ⚠️ Datos mock en dashboard - facturas (156/98/58) e incidencias (23/8/15) hardcodeados en `DashboardService`; no hay tablas/entidades de facturas ni incidencias

### Deseable
- ❌ Refresh token (el endpoint de logout ya está implementado)
- ❌ Reportes y estadísticas
- ❌ Pagos / Facturación electrónica
- ❌ Multimoneda, localización geográfica, backup/recuperación, monitoreo (definidos en `08-consideraciones-tecnicas.md`)

> ✅ Resueltos en esta revisión: migración `/api/owners` → `/v1/propietarios`, CRUD de Propiedades y Bean Validation (aplicado en controllers y entidades).

---

## 📊 Resumen Final

**Backend:** ~92% completitud  
**Frontend:** ~88% completitud  
**TOTAL PROYECTO:** ~90% completitud

---

**Documento actualizado:** 16 de Agosto de 2026
