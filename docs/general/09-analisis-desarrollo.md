# Análisis de Desarrollo vs Especificación

**Fecha:** 2026-02-19  
**Versión:** 1.0

## Resumen Ejecutivo

| Aspecto | Estado | Completitud |
|---------|--------|-------------|
| Backend - Entidades | ⚠️ Parcial | 60% |
| Backend - Autenticación | ⚠️ Parcial | 40% |
| Backend - APIs CRUD | ❌ Pendiente | 5% |
| Frontend - Login | ⚠️ Parcial | 30% |
| Frontend - Dashboard | ⚠️ Básico | 20% |
| Frontend - Menú | ⚠️ Parcial | 50% |
| Base de Datos | ❌ Incompleto | 10% |

---

## 1. Backend (resimanager-backoffice)

### 1.1 Entidades JPA Implementadas

| Entidad | Archivo | Estado | Observaciones |
|---------|---------|--------|---------------|
| Persona | ✅ | Implementada | Nombres de columnas diferentes a la especificación |
| Administradora | ✅ | Implementada | OK |
| Conjunto | ✅ | Implementada | OK |
| Perfil | ✅ | Implementada | OK |
| MenuItem | ✅ | Implementada | OK |
| Modulo | ✅ | Implementada | OK |
| Opcion | ✅ | Implementada | OK |
| Accion | ✅ | Implementada | OK |
| Propiedad | ✅ | Implementada | OK |
| ClaseDePropiedad | ✅ | Implementada | OK |
| PersAdministradora | ✅ | Implementada | Relación |
| PersConjunto | ✅ | Implementada | Relación |
| PerfPersAdministradora | ✅ | Implementada | Relación |
| PerfPersConjunto | ✅ | Implementada | Relación |
| PerfAdministradora | ✅ | Implementada | Relación |
| PerfConjunto | ✅ | Implementada | Relación |
| ModPerfil | ✅ | Implementada | Relación |
| OpcPerfil | ✅ | Implementada | Relación |
| AccOpcion | ✅ | Implementada | Relación |
| AccOpcPerfil | ✅ | Implementada | Relación |
| **Invitacion** | ❌ | **Falta** | Crítico para flujo de registro |
| **InviAdministradora** | ❌ | **Falta** | Crítico |
| **InviConjunto** | ❌ | **Falta** | Crítico |
| **Moneda** | ❌ | **Falta** | Multimoneda |
| **TasaCambio** | ❌ | **Falta** | Multimoneda |
| **Pais/Estado/Ciudad** | ❌ | **Falta** | Localización |

### 1.2 Servicios Implementados

| Servicio | Estado | Observaciones |
|----------|--------|---------------|
| UserService | ⚠️ Parcial | Hardcodeado con usuario de prueba |
| JwtService | ✅ OK | Genera tokens correctamente |
| MenuService | ⚠️ Parcial | Solo obtiene menú sin filtrar por perfil |
| OwnerService | ✅ OK | CRUD básico para owners |

**Problema crítico en UserService:**

```java:resimanager-backoffice/src/main/java/com/resimanager/backoffice/service/UserService.java
// TODO: Usuario hardcodeado - NO consulta la base de datos
var pass = new String(Base64.getDecoder().decode("..."));
return AuthDto.builder()
    .username("test@test.com")
    .password(pass)
    .authorities(new HashSet<>(List.of("ROLE_ADMIN")))
    .build();
```

### 1.3 Controladores Implementados

| Endpoint | Método | Estado | Observaciones |
|----------|--------|--------|---------------|
| `/v1/login` | POST | ✅ OK | Retorna JWT |
| `/v1/owners` | GET/POST | ✅ OK | CRUD básico |
| `/v1/menu` | GET | ❌ Falta | No hay controller |
| `/v1/usuarios` | CRUD | ❌ Falta | - |
| `/v1/conjuntos` | CRUD | ❌ Falta | - |
| `/v1/propiedades` | CRUD | ❌ Falta | - |
| `/v1/invitaciones` | CRUD | ❌ Falta | - |
| `/v1/perfiles` | CRUD | ❌ Falta | - |

### 1.4 Seguridad

| Aspecto | Estado | Observaciones |
|---------|--------|---------------|
| JWT Generation | ✅ OK | SHA-512, 24h expiración |
| JWT Validation | ✅ OK | Filter implementado |
| CORS | ✅ OK | SimpleCORSFilter |
| Password Encoder | ✅ OK | BCrypt |
| **RBAC por perfil** | ❌ Falta | Solo ROLE_ADMIN genérico |
| **Filtro por contexto** | ❌ Falta | No valida administradora/conjunto |

---

## 2. Frontend (resimanager-spa)

### 2.1 Componentes Implementados

| Componente | Archivo | Estado | Observaciones |
|------------|---------|--------|---------------|
| App | App.jsx | ✅ OK | Routing configurado |
| LoginPage | LoginPage.jsx | ✅ OK | Layout AdminLTE |
| Login | Login.jsx | ⚠️ Parcial | No consume API, navega directo |
| DashboardPage | DashboardPage.jsx | ✅ OK | Layout AdminLTE |
| SideMenu | SideMenu.jsx | ⚠️ Parcial | Lee JSON local, no API |
| MenuBar | MenuBar.jsx | ⚠️ Básico | Sin selectores de contexto |
| Content | Content.jsx | ⚠️ Hardcodeado | Dashboard estático |
| Breadcrumb | Breadcrumb.jsx | ✅ OK | Componente básico |

### 2.2 Hooks

| Hook | Estado | Observaciones |
|------|--------|---------------|
| useMenuData | ⚠️ Simulado | Carga de `Menu.json` local con timeout |

```javascript:resimanager-spa/src/hooks/useMenuData.jsx
// PROBLEMA: No consume API, usa JSON local
const simulatedMenuJson = jsonMenu;
await new Promise(resolve => setTimeout(resolve, 1000));
setMenuData(simulatedMenuJson);
```

### 2.3 Faltantes Críticos

| Componente | Prioridad | Descripción |
|------------|-----------|-------------|
| AuthContext | Alta | Context para manejar sesión/token |
| PrivateRoute | Alta | Proteger rutas autenticadas |
| useAuth | Alta | Hook para autenticación |
| ContextSelectors | Alta | Select Admin/Conjunto/Perfil |
| DataTable | Media | Componente genérico para listados |
| Modal | Media | Ventanas emergentes para CRUD |
| InvitacionPage | Media | Formulario de registro por invitación |

---

## 3. Base de Datos

### 3.1 Migraciones Flyway

| Archivo | Contenido | Problema |
|---------|-----------|----------|
| V1.0.0.0 | `owners` table | ❌ No coincide con especificación |
| V1.0.0.1 | `users` table | ❌ No es parte del modelo |
| V1.0.0.2 | Insert users | ❌ Datos de prueba |
| V1.0.0.3 | Insert owners | ❌ Datos de prueba |

**Problema crítico:** Las migraciones no crean las tablas según la especificación (`Persona`, `Administradora`, `Conjunto`, etc.). Las entidades JPA están mapeadas a tablas que NO existen en las migraciones.

### 3.2 Gap entre Entidades y Migraciones

```
Entidad JPA          Migración DB         Estado
─────────────────────────────────────────────────
Persona              ❌ No creada         FALTA
Administradora       ❌ No creada         FALTA
Conjunto             ❌ No creada         FALTA
MenuItem             ❌ No creada         FALTA
...                  ...                  ...
owners               owners               ❌ No mapeada
users                users                ❌ No mapeada
```

---

## 4. Discrepancias con la Especificación

### 4.1 Nomenclatura de Columnas

**Especificación:**
```sql
per_id, per_documento, per_nombre, per_clave, per_sts
```

**Implementado:**
```java
perid, per_doc_ident, per_nombre, per_clave, per_sts
```

### 4.2 Flujo de Login

**Especificación:** Validar usuario → Cargar administradoras → Cargar conjuntos → Cargar perfiles → Retornar contexto

**Implementado:** Solo retorna token JWT sin contexto

### 4.3 Menú Dinámico

**Especificación:** Filtrar por perfil seleccionado con consulta compleja

**Implementado:** Retorna todos los menús sin filtrar

### 4.4 Sistema de Invitaciones

**Especificación:** Flujo completo con UUID, vencimiento, limpieza automática

**Implementado:** ❌ No existe

---

## 5. Prioridades de Desarrollo

### Crítico (Bloqueante)

1. **Migraciones de BD** - Crear tablas según especificación
2. **UserService** - Conectar con tabla Persona real
3. **AuthContext** - Manejo de sesión en frontend
4. **Endpoint /menu** - Con filtrado por perfil
5. **API de Invitaciones** - Flujo de registro

### Alta Prioridad

6. ContextSelectors (Admin/Conjunto/Perfil)
7. Endpoint /usuarios CRUD
8. Endpoint /conjuntos CRUD
9. Endpoint /propiedades CRUD
10. Componente DataTable genérico

### Media Prioridad

11. Componentes modales para CRUD
12. Sistema de auditoría (logs)
13. Multimoneda
14. Localización

---

## 6. Recomendaciones

### Inmediato

1. **Sincronizar entidades con migraciones** - Las entidades JPA no corresponden a las tablas creadas
2. **Eliminar hardcodeo** en UserService
3. **Crear AuthContext** en frontend con persistencia de token
4. **Implementar endpoint /menu** que filtre por perfil

### Arquitectura

1. Crear servicios específicos por entidad (UsuarioService, ConjuntoService, etc.)
2. Implementar DTOs para requests/responses
3. Agregar validación con Bean Validation
4. Crear exception handlers globales (ya existe base)

### Frontend

1. Crear hook `useAuth` para manejo de sesión
2. Crear componente `PrivateRoute` para rutas protegidas
3. Conectar Login.jsx con API real
4. Crear servicio API centralizado con interceptors para JWT

---

## 7. Checklist de Completitud

### Backend

- [ ] Migraciones completas según especificación
- [ ] UserService conectado a BD
- [ ] LoginController retorna contexto (admin/conjunto/perfil)
- [ ] MenuController con filtrado RBAC
- [ ] CRUD Usuarios
- [ ] CRUD Conjuntos
- [ ] CRUD Propiedades
- [ ] Sistema de Invitaciones
- [ ] CRUD Perfiles
- [ ] Auditoría automática

### Frontend

- [ ] AuthContext + useAuth
- [ ] PrivateRoute
- [ ] Login conectado a API
- [ ] Selectores de contexto
- [ ] Menú desde API
- [ ] DataTable genérico
- [ ] Modales CRUD
- [ ] Página de invitación
- [ ] Gestión de tokens (refresh)
