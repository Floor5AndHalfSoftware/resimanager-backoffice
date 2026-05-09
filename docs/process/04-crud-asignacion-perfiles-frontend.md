# Implementación Frontend - Asignación de Perfiles a Usuarios

## Estado de Implementación (2026-03-25, verificado 2026-03-29)

### ✅ Completado — Frontend

| Archivo | Estado | Notas |
|---|---|---|
| `src/pages/asignaciones/AdministradoraUsuariosPage.jsx` | ✅ Implementado | Import corregido: `useToast` → `showToast` |
| `src/pages/asignaciones/ConjuntoUsuariosPage.jsx` | ✅ Implementado | Import corregido: `useToast` → `showToast` |
| `src/pages/asignaciones/AsignarPerfilesPage.jsx` | ✅ Implementado | Import corregido: `useToast` → `showToast` |
| `src/pages/asignaciones/UsuarioPerfilesPage.jsx` | ✅ Implementado | Import corregido: `useToast` → `showToast` |
| `src/pages/UsuariosPage.jsx` | ✅ Implementado | **No estaba en el plan original.** Lista global de usuarios con búsqueda, filtro por estatus y navegación a perfiles |
| `src/services/api.js` | ✅ Implementado | 10 métodos agregados (ver tabla de endpoints) |
| `src/App.jsx` | ✅ Implementado | 5 rutas agregadas + ruta adicional `usuarios/list` para menú |

### ✅ Completado — Backend (no estaba en el plan del documento)

El documento original asumía que los endpoints del backend ya existían. **Ninguno existía** — todos fueron creados durante esta sesión:

| Endpoint | Archivo | Estado |
|---|---|---|
| `GET /v1/usuarios` | `UsuarioController` + `UsuarioService` | ✅ Creado |
| `GET /v1/usuarios/:id` | `UsuarioController` + `UsuarioService` | ✅ Creado |
| `GET /v1/usuarios/:id/perfiles` | `UsuarioController` + `UsuarioService` | ✅ Creado |
| `GET /v1/administradoras/:id` | `AdministradoraController` + `AdministradoraService` | ✅ Creado |
| `GET /v1/administradoras/:id/usuarios` | `AdministradoraController` + `AdministradoraService` | ✅ Creado |
| `POST /v1/administradoras/:id/usuarios/:uid/perfiles` | `AdministradoraController` + `AdministradoraService` | ✅ Creado |
| `DELETE /v1/administradoras/:id/usuarios/:uid/perfiles/:prfId` | `AdministradoraController` + `AdministradoraService` | ✅ Creado |
| `GET /v1/conjuntos/:id` | `ConjuntoController` + `ConjuntoService` | ✅ Creado |
| `GET /v1/conjuntos/:id/usuarios` | `ConjuntoController` + `ConjuntoService` | ✅ Creado |
| `POST /v1/conjuntos/:id/usuarios/:uid/perfiles` | `ConjuntoController` + `ConjuntoService` | ✅ Creado |
| `DELETE /v1/conjuntos/:id/usuarios/:uid/perfiles/:prfId` | `ConjuntoController` + `ConjuntoService` | ✅ Creado |

**Nuevos DTOs backend creados:**
- `UsuarioDTO` — campos: id, documento, nombre, apellido, email, telefono, usuario, estatus
- `UsuarioListResponse` — wrapper paginado para lista de usuarios
- `UsuarioPerfilesResponse` — perfiles agrupados por contexto (persona + contextos[])
- `ContextoUsuariosResponse` — usuarios de un contexto con sus perfiles
- `AsignarPerfilesRequest` — lista de IDs de perfiles a asignar

**Repositorios modificados:**
- `PersonaRepository` — agregado `findAllWithFilters` (búsqueda paginada)
- `PersAdministradoraRepository` — agregado `findActiveByAdmId`
- `PersConjuntoRepository` — agregado `findActiveByConjId`
- `PerfPersAdministradoraRepository` — agregado `findActiveByAdmIdAndPerId`
- `PerfPersConjuntoRepository` — agregado `findActiveByConjIdAndPerId`

### ✅ Completado — Base de Datos / Configuración

| Item | Estado | Notas |
|---|---|---|
| Migración `V2.0.12` | ✅ Aplicada | Elimina ítems de menú duplicados "Perfiles de acceso" (IDs 11 y 12) bajo grupo "Usuarios" |
| Migración `V2.0.13` | ✅ Aplicada | Agrega ítem de menú "Gestión de Usuarios" (ID 11) bajo grupo "Usuarios", ruta `/dashboard/usuarios/list` |
| `application.yml` | ✅ Modificado | Agregado `spring.flyway.postgresql.transactional-lock: false` para compatibilidad con Neon serverless |
| `.env` backend | ✅ Modificado | URL de BD cambiada a endpoint directo de Neon (sin `-pooler`) para soporte de advisory locks |

### ✅ Verificado (2026-03-29)

| Item | Estado | Descripción |
|---|---|---|
| Flujo completo `AsignarPerfilesPage` | ✅ Verificado | Endpoint `GET /v1/usuarios/:id` funciona. Flujo end-to-end confirmado. |
| Asignación de perfiles (POST) | ✅ Verificado | Campo `ppaid` con `COALESCE(MAX(ppaid),0)+1` funciona correctamente. |
| Remoción de perfiles (DELETE) | ✅ Verificado | Soft-delete (`pPASts='I'`) funciona correctamente. |
| `AdministradoraUsuariosPage` y `ConjuntoUsuariosPage` | ✅ Verificado | Páginas accesibles y funcionales. |

### ❌ No Implementado / Fuera de Scope

| Item | Notas |
|---|---|
| Validación de perfiles por nivel de contexto | Documentado en sección "Consideraciones" pero no implementado en el frontend. Los perfiles disponibles no se filtran por nivel. |
| Manejo granular de errores HTTP (400/409) | `AsignarPerfilesPage` usa manejo genérico de errores, no el switch por código documentado. |
| `Promise.all` en `AsignarPerfilesPage.fetchData` | Las llamadas se hacen en serie, no en paralelo como se recomienda en la sección de optimización. |
| Permisos de acceso por rol | La sección "Permisos de Acceso" documenta `canManageProfiles()` en AuthContext — no implementado. |

### 🐛 Bugs Corregidos Durante la Sesión

1. **`useToast` no existe** — `Toast.jsx` exporta `showToast` directamente. Corregido en 5 páginas: `AdministradoraUsuariosPage`, `ConjuntoUsuariosPage`, `AsignarPerfilesPage`, `UsuarioPerfilesPage`, `UsuariosPage`.
2. **Comentario JSX sin cerrar** en `App.jsx` línea 68 — faltaba `}` al final del comentario, causaba error de parsing.
3. **Triple llamada a `/v1/usuarios`** en `UsuariosPage` — dos `useEffect` independientes que ambos disparaban al montar. Consolidado en uno.
4. **`.env.local` sobreescribía `.env`** — tenía `localhost:8080` en lugar de la URL de Koyeb. Vite da prioridad a `.env.local`.
5. **Flyway advisory lock atascado** — causado por usar endpoint pooler de Neon. Solución: endpoint directo + `transactional-lock: false`.
6. **Migración V2.0.13 con ID duplicado** — intentaba insertar `mitid=10` que ya existía. Corregido a `mitid=11`.

---

## Resumen Ejecutivo

Este documento describe la implementación completa del frontend para la gestión de asignación de perfiles a usuarios en contextos específicos (Administradoras o Conjuntos) en el SPA de ResiManager, integrándose con los endpoints del backend documentados en `/docs/api/02-CRUD-ASIGNACION-PERFILES.md`.

## Tabla de Contenidos

1. [Arquitectura Frontend](#arquitectura-frontend)
2. [Componentes y Páginas](#componentes-y-páginas)
3. [Integración con API](#integración-con-api)
4. [Flujos de Usuario](#flujos-de-usuario)
5. [Guía de Implementación](#guía-de-implementación)
6. [Testing Manual](#testing-manual)
7. [Consideraciones Importantes](#consideraciones-importantes)
8. [Próximos Pasos](#próximos-pasos)

---

## Arquitectura Frontend

### Stack Tecnológico

- **Framework**: React 19.2.3
- **Build Tool**: Vite 5.4.1
- **Router**: React Router DOM 7.3.0
- **State Management**: React Context API
- **HTTP Client**: Native Fetch API
- **UI Library**: AdminLTE 3 + Bootstrap 4
- **Icons**: FontAwesome 6.6.0
- **Notifications**: Toast Component (ya existente)

### Estructura de Archivos

```
resimanager-spa/src/
├── services/
│   └── api.js                                  [MODIFICAR]
│       └── Agregar 6 métodos de asignación
│
├── components/
│   └── common/
│       └── Toast.jsx                           [EXISTENTE]
│           └── Sistema de notificaciones
│
├── pages/
│   └── asignaciones/
│       ├── AdministradoraUsuariosPage.jsx     [CREAR]
│       │   └── Lista usuarios de administradora
│       ├── ConjuntoUsuariosPage.jsx           [CREAR]
│       │   └── Lista usuarios de conjunto
│       ├── AsignarPerfilesPage.jsx            [CREAR]
│       │   └── Asignar perfiles a usuario
│       └── UsuarioPerfilesPage.jsx            [CREAR]
│           └── Vista global de perfiles de usuario
│
└── App.jsx                                     [MODIFICAR]
    └── Agregar 4 rutas de asignaciones
```

### Flujo de Datos

```
┌────────────────────────────────────────────────────────────┐
│                      Usuario Admin                          │
└──────────────────────┬─────────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────────┐
│              Páginas React (Asignaciones)                   │
│  AdministradoraUsuarios → AsignarPerfiles                  │
│  ConjuntoUsuarios → AsignarPerfiles                        │
│  UsuarioPerfiles (Vista Global)                            │
└──────────────────────┬─────────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────────┐
│           services/api.js (Capa API)                        │
│  getAdministradoraUsuarios()                               │
│  getConjuntoUsuarios()                                     │
│  asignarPerfilesAdministradora()                           │
│  asignarPerfilesConjunto()                                 │
│  removerPerfilAdministradora()                             │
│  removerPerfilConjunto()                                   │
│  getUsuarioPerfiles()                                      │
└──────────────────────┬─────────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────────┐
│                  Backend REST API                           │
│    /administradoras/{id}/usuarios                          │
│    /conjuntos/{id}/usuarios                                │
└────────────────────────────────────────────────────────────┘
```

---

## Componentes y Páginas

### 1. AdministradoraUsuariosPage.jsx (Lista)

**Ubicación**: `/src/pages/asignaciones/AdministradoraUsuariosPage.jsx`

**Responsabilidades**:
- Listar usuarios asignados a una administradora
- Mostrar perfiles por usuario
- Búsqueda por nombre o documento
- Acciones: Gestionar Perfiles, Ver Detalles
- Indicador de estatus de usuario

**Estructura UI**:

```jsx
┌───────────────────────────────────────────────────┐
│  Breadcrumb: Inicio > Administradoras > Usuarios  │
├───────────────────────────────────────────────────┤
│  [ Card: Usuarios de "Inmobiliaria ABC" ]        │
│                                                    │
│  [Buscar usuario...]  [🔄 Recargar]               │
│                                                    │
│  ┌─────────────────────────────────────────────┐  │
│  │ Nombre    │ Documento  │ Perfiles │ Acciones││
│  ├─────────────────────────────────────────────┤  │
│  │ Carlos M. │ V-1234567  │ [Admin] │ [Gest.] ││
│  │ Juan Pérez│ V-7654321  │ [Prop.] │ [Gest.] ││
│  └─────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────┘
```

**Código Base**:

```jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { 
  getAdministradoraById, 
  getAdministradoraUsuarios 
} from '../../services/api';
import { useToast } from '../../components/common/Toast';

const AdministradoraUsuariosPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { showToast } = useToast();
  
  const [administradora, setAdministradora] = useState(null);
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    setLoading(true);
    try {
      // Obtener datos de la administradora
      const adminResponse = await getAdministradoraById(id);
      setAdministradora(adminResponse);
      
      // Obtener usuarios de la administradora
      const usuariosResponse = await getAdministradoraUsuarios(id);
      setUsuarios(usuariosResponse.data);
    } catch (error) {
      showToast(error.message || 'Error al cargar usuarios', 'error');
      if (error.status === 404) {
        navigate('/dashboard/administradoras');
      }
    } finally {
      setLoading(false);
    }
  };

  const filteredUsuarios = usuarios.filter(usuario =>
    `${usuario.persona.nombre} ${usuario.persona.apellido} ${usuario.persona.documento}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="content-wrapper">
        <section className="content">
          <div className="text-center mt-5">
            <i className="fas fa-spinner fa-spin fa-3x"></i>
          </div>
        </section>
      </div>
    );
  }

  return (
    <div className="content-wrapper">
      {/* Content Header */}
      <section className="content-header">
        <div className="container-fluid">
          <div className="row mb-2">
            <div className="col-sm-6">
              <h1>Usuarios - {administradora?.nombre}</h1>
            </div>
            <div className="col-sm-6">
              <ol className="breadcrumb float-sm-right">
                <li className="breadcrumb-item">
                  <Link to="/dashboard">Inicio</Link>
                </li>
                <li className="breadcrumb-item">
                  <Link to="/dashboard/administradoras">Administradoras</Link>
                </li>
                <li className="breadcrumb-item active">Usuarios</li>
              </ol>
            </div>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="content">
        <div className="container-fluid">
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">
                <i className="fas fa-users mr-2"></i>
                Usuarios Asignados
              </h3>
              <div className="card-tools">
                <button 
                  className="btn btn-sm btn-primary" 
                  onClick={fetchData}
                >
                  <i className="fas fa-sync-alt"></i> Recargar
                </button>
              </div>
            </div>
            
            <div className="card-body">
              {/* Search Bar */}
              <div className="row mb-3">
                <div className="col-md-6">
                  <div className="input-group">
                    <div className="input-group-prepend">
                      <span className="input-group-text">
                        <i className="fas fa-search"></i>
                      </span>
                    </div>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Buscar por nombre o documento..."
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                    />
                  </div>
                </div>
              </div>

              {/* Users Table */}
              {filteredUsuarios.length === 0 ? (
                <div className="alert alert-info">
                  <i className="fas fa-info-circle"></i> No hay usuarios asignados
                </div>
              ) : (
                <div className="table-responsive">
                  <table className="table table-bordered table-hover">
                    <thead className="thead-light">
                      <tr>
                        <th>Nombre</th>
                        <th>Documento</th>
                        <th>Email</th>
                        <th>Perfiles</th>
                        <th>Estatus</th>
                        <th>Acciones</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredUsuarios.map((usuario) => (
                        <tr key={usuario.persona.id}>
                          <td>
                            {usuario.persona.nombre} {usuario.persona.apellido}
                          </td>
                          <td>{usuario.persona.documento}</td>
                          <td>{usuario.persona.email}</td>
                          <td>
                            {usuario.perfiles.map((perfil) => (
                              <span 
                                key={perfil.id} 
                                className="badge badge-info mr-1"
                              >
                                {perfil.nombre}
                              </span>
                            ))}
                          </td>
                          <td>
                            <span 
                              className={`badge badge-${
                                usuario.estatus === 'A' ? 'success' : 'secondary'
                              }`}
                            >
                              {usuario.estatus === 'A' ? 'Activo' : 'Inactivo'}
                            </span>
                          </td>
                          <td>
                            <button
                              className="btn btn-sm btn-primary"
                              onClick={() => navigate(
                                `/dashboard/administradoras/${id}/usuarios/${usuario.persona.id}/perfiles`
                              )}
                              title="Gestionar Perfiles"
                            >
                              <i className="fas fa-user-shield"></i>
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default AdministradoraUsuariosPage;
```

---

### 2. ConjuntoUsuariosPage.jsx (Lista)

**Ubicación**: `/src/pages/asignaciones/ConjuntoUsuariosPage.jsx`

**Responsabilidades**:
- Similar a AdministradoraUsuariosPage pero para conjuntos
- Listar usuarios asignados a un conjunto
- Mostrar perfiles por usuario
- Gestionar perfiles de usuarios

**Nota**: La implementación es muy similar a `AdministradoraUsuariosPage.jsx`, cambiando:
- `getAdministradoraById` → `getConjuntoById`
- `getAdministradoraUsuarios` → `getConjuntoUsuarios`
- Rutas y breadcrumbs adaptados a conjuntos

---

### 3. AsignarPerfilesPage.jsx (Gestión)

**Ubicación**: `/src/pages/asignaciones/AsignarPerfilesPage.jsx`

**Responsabilidades**:
- Mostrar perfiles actuales del usuario en el contexto
- Listar perfiles disponibles para asignar
- Asignar nuevos perfiles al usuario
- Remover perfiles del usuario
- Validar perfiles según nivel de contexto

**Estructura UI**:

```jsx
┌─────────────────────────────────────────────────────┐
│  Breadcrumb: Inicio > Administradora > Usuarios >   │
│              Gestionar Perfiles                      │
├─────────────────────────────────────────────────────┤
│  [ Card: Información del Usuario ]                  │
│  Nombre: Carlos Martínez                            │
│  Documento: V-12345678                              │
│  Email: carlos@example.com                          │
│  Contexto: Inmobiliaria ABC (Administradora)        │
│                                                      │
│  [ Card: Perfiles Asignados ]                       │
│  ┌─────────────────────────────────────────────┐    │
│  │ [Admin General] [x Remover]                 │    │
│  │ [Propietario] [x Remover]                   │    │
│  └─────────────────────────────────────────────┘    │
│                                                      │
│  [ Card: Asignar Nuevos Perfiles ]                  │
│  ☑ Super Administrador (Nivel 0)                    │
│  ☑ Administrador de Conjunto (Nivel 2)              │
│  ☐ Residente (Nivel 4)                              │
│                                                      │
│  [Asignar Seleccionados] [Volver]                   │
└─────────────────────────────────────────────────────┘
```

**Código Base**:

```jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { 
  getUsuarioById,
  getAdministradoraById,
  getConjuntoById,
  getPerfiles,
  asignarPerfilesAdministradora,
  asignarPerfilesConjunto,
  removerPerfilAdministradora,
  removerPerfilConjunto
} from '../../services/api';
import { useToast } from '../../components/common/Toast';

const AsignarPerfilesPage = () => {
  const { contextType, contextId, usuarioId } = useParams();
  const navigate = useNavigate();
  const { showToast } = useToast();
  
  const [usuario, setUsuario] = useState(null);
  const [contexto, setContexto] = useState(null);
  const [perfilesActuales, setPerfilesActuales] = useState([]);
  const [perfilesDisponibles, setPerfilesDisponibles] = useState([]);
  const [selectedPerfiles, setSelectedPerfiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const isAdministradora = contextType === 'administradora';

  useEffect(() => {
    fetchData();
  }, [contextId, usuarioId, contextType]);

  const fetchData = async () => {
    setLoading(true);
    try {
      // Obtener datos del usuario
      const usuarioResponse = await getUsuarioById(usuarioId);
      setUsuario(usuarioResponse);
      
      // Obtener datos del contexto
      let contextResponse;
      let usuariosResponse;
      
      if (isAdministradora) {
        contextResponse = await getAdministradoraById(contextId);
        usuariosResponse = await getAdministradoraUsuarios(contextId);
      } else {
        contextResponse = await getConjuntoById(contextId);
        usuariosResponse = await getConjuntoUsuarios(contextId);
      }
      
      setContexto(contextResponse);
      
      // Encontrar perfiles del usuario en este contexto
      const usuarioEnContexto = usuariosResponse.data.find(
        u => u.persona.id === parseInt(usuarioId)
      );
      
      setPerfilesActuales(usuarioEnContexto?.perfiles || []);
      
      // Obtener todos los perfiles
      const perfilesResponse = await getPerfiles();
      
      // Filtrar perfiles disponibles (no asignados)
      const perfilesYaAsignados = usuarioEnContexto?.perfiles.map(p => p.id) || [];
      const disponibles = perfilesResponse.data.filter(
        p => !perfilesYaAsignados.includes(p.id)
      );
      
      setPerfilesDisponibles(disponibles);
      
    } catch (error) {
      showToast(error.message || 'Error al cargar datos', 'error');
      navigate(-1);
    } finally {
      setLoading(false);
    }
  };

  const handleTogglePerfil = (perfilId) => {
    setSelectedPerfiles(prev =>
      prev.includes(perfilId)
        ? prev.filter(id => id !== perfilId)
        : [...prev, perfilId]
    );
  };

  const handleAsignarPerfiles = async () => {
    if (selectedPerfiles.length === 0) {
      showToast('Debe seleccionar al menos un perfil', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      if (isAdministradora) {
        await asignarPerfilesAdministradora(
          contextId, 
          usuarioId, 
          { perfiles: selectedPerfiles }
        );
      } else {
        await asignarPerfilesConjunto(
          contextId, 
          usuarioId, 
          { perfiles: selectedPerfiles }
        );
      }
      
      showToast(
        `${selectedPerfiles.length} perfil(es) asignado(s) correctamente`, 
        'success'
      );
      
      setSelectedPerfiles([]);
      await fetchData();
      
    } catch (error) {
      showToast(error.message || 'Error al asignar perfiles', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleRemoverPerfil = async (perfilId, perfilNombre) => {
    if (!window.confirm(`¿Remover el perfil "${perfilNombre}"?`)) {
      return;
    }

    try {
      if (isAdministradora) {
        await removerPerfilAdministradora(contextId, usuarioId, perfilId);
      } else {
        await removerPerfilConjunto(contextId, usuarioId, perfilId);
      }
      
      showToast('Perfil removido correctamente', 'success');
      await fetchData();
      
    } catch (error) {
      showToast(error.message || 'Error al remover perfil', 'error');
    }
  };

  if (loading) {
    return (
      <div className="content-wrapper">
        <section className="content">
          <div className="text-center mt-5">
            <i className="fas fa-spinner fa-spin fa-3x"></i>
          </div>
        </section>
      </div>
    );
  }

  return (
    <div className="content-wrapper">
      {/* Content Header */}
      <section className="content-header">
        <div className="container-fluid">
          <div className="row mb-2">
            <div className="col-sm-6">
              <h1>Gestionar Perfiles</h1>
            </div>
            <div className="col-sm-6">
              <ol className="breadcrumb float-sm-right">
                <li className="breadcrumb-item">
                  <Link to="/dashboard">Inicio</Link>
                </li>
                <li className="breadcrumb-item">
                  <Link to={`/dashboard/${contextType}s`}>
                    {isAdministradora ? 'Administradoras' : 'Conjuntos'}
                  </Link>
                </li>
                <li className="breadcrumb-item">
                  <Link to={`/dashboard/${contextType}s/${contextId}/usuarios`}>
                    Usuarios
                  </Link>
                </li>
                <li className="breadcrumb-item active">Gestionar Perfiles</li>
              </ol>
            </div>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="content">
        <div className="container-fluid">
          
          {/* Usuario Info Card */}
          <div className="card card-primary card-outline">
            <div className="card-header">
              <h3 className="card-title">
                <i className="fas fa-user mr-2"></i>
                Información del Usuario
              </h3>
            </div>
            <div className="card-body">
              <div className="row">
                <div className="col-md-6">
                  <dl className="row">
                    <dt className="col-sm-4">Nombre:</dt>
                    <dd className="col-sm-8">
                      {usuario?.nombre} {usuario?.apellido}
                    </dd>
                    <dt className="col-sm-4">Documento:</dt>
                    <dd className="col-sm-8">{usuario?.documento}</dd>
                    <dt className="col-sm-4">Email:</dt>
                    <dd className="col-sm-8">{usuario?.email}</dd>
                  </dl>
                </div>
                <div className="col-md-6">
                  <dl className="row">
                    <dt className="col-sm-4">Contexto:</dt>
                    <dd className="col-sm-8">{contexto?.nombre}</dd>
                    <dt className="col-sm-4">Tipo:</dt>
                    <dd className="col-sm-8">
                      <span className="badge badge-secondary">
                        {isAdministradora ? 'Administradora' : 'Conjunto'}
                      </span>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <div className="row">
            {/* Perfiles Actuales */}
            <div className="col-md-6">
              <div className="card card-success card-outline">
                <div className="card-header">
                  <h3 className="card-title">
                    <i className="fas fa-check-circle mr-2"></i>
                    Perfiles Asignados
                  </h3>
                </div>
                <div className="card-body">
                  {perfilesActuales.length === 0 ? (
                    <div className="alert alert-info mb-0">
                      <i className="fas fa-info-circle"></i> 
                      No tiene perfiles asignados
                    </div>
                  ) : (
                    <div className="list-group">
                      {perfilesActuales.map((perfil) => (
                        <div 
                          key={perfil.id} 
                          className="list-group-item d-flex justify-content-between align-items-center"
                        >
                          <span>
                            <i className="fas fa-user-shield mr-2 text-success"></i>
                            {perfil.nombre}
                          </span>
                          <button
                            className="btn btn-sm btn-danger"
                            onClick={() => handleRemoverPerfil(perfil.id, perfil.nombre)}
                            title="Remover perfil"
                          >
                            <i className="fas fa-times"></i>
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Perfiles Disponibles */}
            <div className="col-md-6">
              <div className="card card-info card-outline">
                <div className="card-header">
                  <h3 className="card-title">
                    <i className="fas fa-plus-circle mr-2"></i>
                    Asignar Nuevos Perfiles
                  </h3>
                </div>
                <div className="card-body">
                  {perfilesDisponibles.length === 0 ? (
                    <div className="alert alert-warning mb-0">
                      <i className="fas fa-exclamation-triangle"></i> 
                      No hay más perfiles disponibles
                    </div>
                  ) : (
                    <>
                      <div className="list-group mb-3">
                        {perfilesDisponibles.map((perfil) => (
                          <label 
                            key={perfil.id} 
                            className="list-group-item d-flex align-items-center"
                            style={{ cursor: 'pointer' }}
                          >
                            <input
                              type="checkbox"
                              className="mr-3"
                              checked={selectedPerfiles.includes(perfil.id)}
                              onChange={() => handleTogglePerfil(perfil.id)}
                            />
                            <div className="flex-grow-1">
                              <strong>{perfil.nombre}</strong>
                              <br />
                              <small className="text-muted">
                                {perfil.descripcion}
                              </small>
                              <br />
                              <span className="badge badge-secondary">
                                Nivel {perfil.nivel}
                              </span>
                            </div>
                          </label>
                        ))}
                      </div>
                      
                      <button
                        className="btn btn-success btn-block"
                        onClick={handleAsignarPerfiles}
                        disabled={submitting || selectedPerfiles.length === 0}
                      >
                        {submitting ? (
                          <>
                            <i className="fas fa-spinner fa-spin mr-2"></i>
                            Asignando...
                          </>
                        ) : (
                          <>
                            <i className="fas fa-check mr-2"></i>
                            Asignar Seleccionados ({selectedPerfiles.length})
                          </>
                        )}
                      </button>
                    </>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Botones de Acción */}
          <div className="row">
            <div className="col-12">
              <button
                className="btn btn-secondary"
                onClick={() => navigate(-1)}
              >
                <i className="fas fa-arrow-left mr-2"></i>
                Volver
              </button>
            </div>
          </div>

        </div>
      </section>
    </div>
  );
};

export default AsignarPerfilesPage;
```

---

### 4. UsuarioPerfilesPage.jsx (Vista Global)

**Ubicación**: `/src/pages/asignaciones/UsuarioPerfilesPage.jsx`

**Responsabilidades**:
- Mostrar todos los perfiles de un usuario
- Agrupar por tipo de contexto (Administradora/Conjunto)
- Vista de solo lectura (informativa)
- Navegación rápida a gestión de perfiles

**Estructura UI**:

```jsx
┌─────────────────────────────────────────────────────┐
│  Breadcrumb: Inicio > Usuarios > Perfiles           │
├─────────────────────────────────────────────────────┤
│  [ Card: Carlos Martínez - V-12345678 ]            │
│                                                      │
│  📧 carlos@example.com                              │
│                                                      │
│  [ Card: Administradoras ]                          │
│  ┌─────────────────────────────────────────────┐    │
│  │ Inmobiliaria ABC                            │    │
│  │   • Administrador General                   │    │
│  │   • Supervisor                              │    │
│  │   [Gestionar Perfiles]                      │    │
│  └─────────────────────────────────────────────┘    │
│                                                      │
│  [ Card: Conjuntos ]                                │
│  ┌─────────────────────────────────────────────┐    │
│  │ Residencial Las Flores                      │    │
│  │   • Administrador de Conjunto               │    │
│  │   [Gestionar Perfiles]                      │    │
│  │                                              │    │
│  │ Torre del Mar                               │    │
│  │   • Propietario                             │    │
│  │   • Residente                               │    │
│  │   [Gestionar Perfiles]                      │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

**Código Base**:

```jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getUsuarioPerfiles } from '../../services/api';
import { useToast } from '../../components/common/Toast';

const UsuarioPerfilesPage = () => {
  const { usuarioId } = useParams();
  const navigate = useNavigate();
  const { showToast } = useToast();
  
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [usuarioId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const response = await getUsuarioPerfiles(usuarioId);
      setData(response);
    } catch (error) {
      showToast(error.message || 'Error al cargar perfiles', 'error');
      if (error.status === 404) {
        navigate('/dashboard/usuarios');
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="content-wrapper">
        <section className="content">
          <div className="text-center mt-5">
            <i className="fas fa-spinner fa-spin fa-3x"></i>
          </div>
        </section>
      </div>
    );
  }

  const administradoras = data?.contextos.filter(c => c.tipo === 'ADMINISTRADORA') || [];
  const conjuntos = data?.contextos.filter(c => c.tipo === 'CONJUNTO') || [];

  return (
    <div className="content-wrapper">
      {/* Content Header */}
      <section className="content-header">
        <div className="container-fluid">
          <div className="row mb-2">
            <div className="col-sm-6">
              <h1>Perfiles del Usuario</h1>
            </div>
            <div className="col-sm-6">
              <ol className="breadcrumb float-sm-right">
                <li className="breadcrumb-item">
                  <Link to="/dashboard">Inicio</Link>
                </li>
                <li className="breadcrumb-item">
                  <Link to="/dashboard/usuarios">Usuarios</Link>
                </li>
                <li className="breadcrumb-item active">Perfiles</li>
              </ol>
            </div>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="content">
        <div className="container-fluid">
          
          {/* Usuario Info */}
          <div className="card card-primary card-outline">
            <div className="card-body">
              <h4>
                <i className="fas fa-user mr-2"></i>
                {data?.persona.nombre} {data?.persona.apellido}
              </h4>
              <p className="text-muted mb-0">
                <i className="fas fa-id-card mr-2"></i>
                {data?.persona.documento}
                <span className="ml-3">
                  <i className="fas fa-envelope mr-2"></i>
                  {data?.persona.email}
                </span>
              </p>
            </div>
          </div>

          {/* Administradoras */}
          {administradoras.length > 0 && (
            <div className="card card-info card-outline">
              <div className="card-header">
                <h3 className="card-title">
                  <i className="fas fa-building mr-2"></i>
                  Administradoras
                </h3>
              </div>
              <div className="card-body">
                {administradoras.map((contexto) => (
                  <div key={contexto.entidad.id} className="callout callout-info mb-3">
                    <h5>{contexto.entidad.nombre}</h5>
                    <ul className="mb-2">
                      {contexto.perfiles.map((perfil) => (
                        <li key={perfil.id}>
                          <i className="fas fa-user-shield mr-2"></i>
                          {perfil.nombre}
                        </li>
                      ))}
                    </ul>
                    <Link
                      to={`/dashboard/administradora/${contexto.entidad.id}/usuarios/${usuarioId}/perfiles`}
                      className="btn btn-sm btn-primary"
                    >
                      <i className="fas fa-cog mr-2"></i>
                      Gestionar Perfiles
                    </Link>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Conjuntos */}
          {conjuntos.length > 0 && (
            <div className="card card-success card-outline">
              <div className="card-header">
                <h3 className="card-title">
                  <i className="fas fa-home mr-2"></i>
                  Conjuntos Residenciales
                </h3>
              </div>
              <div className="card-body">
                {conjuntos.map((contexto) => (
                  <div key={contexto.entidad.id} className="callout callout-success mb-3">
                    <h5>{contexto.entidad.nombre}</h5>
                    <ul className="mb-2">
                      {contexto.perfiles.map((perfil) => (
                        <li key={perfil.id}>
                          <i className="fas fa-user-shield mr-2"></i>
                          {perfil.nombre}
                        </li>
                      ))}
                    </ul>
                    <Link
                      to={`/dashboard/conjunto/${contexto.entidad.id}/usuarios/${usuarioId}/perfiles`}
                      className="btn btn-sm btn-success"
                    >
                      <i className="fas fa-cog mr-2"></i>
                      Gestionar Perfiles
                    </Link>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Sin Perfiles */}
          {administradoras.length === 0 && conjuntos.length === 0 && (
            <div className="alert alert-warning">
              <i className="fas fa-exclamation-triangle mr-2"></i>
              Este usuario no tiene perfiles asignados en ningún contexto.
            </div>
          )}

          {/* Botón Volver */}
          <button
            className="btn btn-secondary"
            onClick={() => navigate('/dashboard/usuarios')}
          >
            <i className="fas fa-arrow-left mr-2"></i>
            Volver a Usuarios
          </button>

        </div>
      </section>
    </div>
  );
};

export default UsuarioPerfilesPage;
```

---

## Integración con API

### Métodos a Agregar en `services/api.js`

```javascript
// ==========================================
// ASIGNACIÓN DE PERFILES
// ==========================================

/**
 * Obtiene usuarios de una administradora
 */
export const getAdministradoraUsuarios = async (administradoraId) => {
  return await apiRequest(`/administradoras/${administradoraId}/usuarios`, {
    method: 'GET'
  });
};

/**
 * Obtiene usuarios de un conjunto
 */
export const getConjuntoUsuarios = async (conjuntoId) => {
  return await apiRequest(`/conjuntos/${conjuntoId}/usuarios`, {
    method: 'GET'
  });
};

/**
 * Asigna perfiles a un usuario en una administradora
 */
export const asignarPerfilesAdministradora = async (
  administradoraId, 
  usuarioId, 
  data
) => {
  return await apiRequest(
    `/administradoras/${administradoraId}/usuarios/${usuarioId}/perfiles`, 
    {
      method: 'POST',
      body: JSON.stringify(data)
    }
  );
};

/**
 * Asigna perfiles a un usuario en un conjunto
 */
export const asignarPerfilesConjunto = async (conjuntoId, usuarioId, data) => {
  return await apiRequest(
    `/conjuntos/${conjuntoId}/usuarios/${usuarioId}/perfiles`, 
    {
      method: 'POST',
      body: JSON.stringify(data)
    }
  );
};

/**
 * Remueve un perfil de un usuario en una administradora
 */
export const removerPerfilAdministradora = async (
  administradoraId, 
  usuarioId, 
  perfilId
) => {
  return await apiRequest(
    `/administradoras/${administradoraId}/usuarios/${usuarioId}/perfiles/${perfilId}`, 
    {
      method: 'DELETE'
    }
  );
};

/**
 * Remueve un perfil de un usuario en un conjunto
 */
export const removerPerfilConjunto = async (conjuntoId, usuarioId, perfilId) => {
  return await apiRequest(
    `/conjuntos/${conjuntoId}/usuarios/${usuarioId}/perfiles/${perfilId}`, 
    {
      method: 'DELETE'
    }
  );
};

/**
 * Obtiene todos los perfiles de un usuario en todos sus contextos
 */
export const getUsuarioPerfiles = async (usuarioId) => {
  return await apiRequest(`/usuarios/${usuarioId}/perfiles`, {
    method: 'GET'
  });
};
```

---

## Flujos de Usuario

### Flujo 1: Ver Usuarios de Administradora

```
1. Usuario navega a "Administradoras"
   ↓
2. Click en "Ver Usuarios" de una administradora
   GET /administradoras/{id}/usuarios
   ↓
3. Se muestra AdministradoraUsuariosPage
   - Lista de usuarios
   - Perfiles asignados a cada uno
   - Botón "Gestionar Perfiles"
```

### Flujo 2: Asignar Perfiles a Usuario

```
1. En AdministradoraUsuariosPage, click "Gestionar Perfiles"
   ↓
2. Se carga AsignarPerfilesPage
   GET /usuarios/{id}
   GET /administradoras/{id}
   GET /perfiles
   ↓
3. Usuario ve:
   - Perfiles ya asignados
   - Perfiles disponibles para asignar
   ↓
4. Usuario selecciona perfiles y click "Asignar"
   POST /administradoras/{id}/usuarios/{uid}/perfiles
   Body: { "perfiles": [2, 3] }
   ↓
5. Toast de éxito → Recarga datos
```

### Flujo 3: Remover Perfil

```
1. En AsignarPerfilesPage, usuario ve perfiles asignados
   ↓
2. Click en botón "X" de un perfil
   ↓
3. Confirmación: "¿Remover el perfil X?"
   ↓
4. Usuario confirma
   DELETE /administradoras/{id}/usuarios/{uid}/perfiles/{pid}
   ↓
5. Toast de éxito → Perfil removido de la lista
```

### Flujo 4: Vista Global de Perfiles

```
1. Usuario navega a "Usuarios"
   ↓
2. Click en "Ver Perfiles" de un usuario
   GET /usuarios/{id}/perfiles
   ↓
3. Se muestra UsuarioPerfilesPage
   - Contextos agrupados (Administradoras/Conjuntos)
   - Perfiles por contexto
   - Botones de navegación a gestión
```

---

## Guía de Implementación

### Orden de Implementación

```
Fase 1: Base API
├─ 1. Agregar métodos en services/api.js (7 métodos)
└─ 2. Verificar Toast.jsx ya existe (de CRUD anterior)

Fase 2: Páginas Core
├─ 3. AdministradoraUsuariosPage.jsx (lista)
├─ 4. ConjuntoUsuariosPage.jsx (lista)
└─ 5. AsignarPerfilesPage.jsx (gestión)

Fase 3: Página Vista Global
└─ 6. UsuarioPerfilesPage.jsx (vista global)

Fase 4: Integración
├─ 7. Agregar rutas en App.jsx (4 rutas)
└─ 8. Agregar opciones al menú si es estático

Fase 5: Testing
└─ 9. Pruebas manuales de todos los flujos
```

### Rutas a Agregar en `App.jsx`

```jsx
// En App.jsx, dentro de las rutas protegidas
<Routes>
  {/* ... rutas existentes ... */}
  
  {/* Asignación de Perfiles - Administradoras */}
  <Route 
    path="/dashboard/administradoras/:id/usuarios" 
    element={<AdministradoraUsuariosPage />} 
  />
  <Route 
    path="/dashboard/administradora/:contextId/usuarios/:usuarioId/perfiles" 
    element={<AsignarPerfilesPage contextType="administradora" />} 
  />
  
  {/* Asignación de Perfiles - Conjuntos */}
  <Route 
    path="/dashboard/conjuntos/:id/usuarios" 
    element={<ConjuntoUsuariosPage />} 
  />
  <Route 
    path="/dashboard/conjunto/:contextId/usuarios/:usuarioId/perfiles" 
    element={<AsignarPerfilesPage contextType="conjunto" />} 
  />
  
  {/* Vista Global de Perfiles de Usuario */}
  <Route 
    path="/dashboard/usuarios/:usuarioId/perfiles" 
    element={<UsuarioPerfilesPage />} 
  />
</Routes>
```

---

## Testing Manual

### Checklist de Pruebas

#### AdministradoraUsuariosPage

```
[ ] Se cargan usuarios de la administradora al montar
[ ] Loading spinner se muestra mientras carga
[ ] Tabla muestra: nombre, documento, email, perfiles, estatus
[ ] Badges de perfiles usan color info (azul)
[ ] Badge de estatus: Verde (Activo), Gris (Inactivo)
[ ] Búsqueda por nombre/documento funciona
[ ] Búsqueda filtra en tiempo real
[ ] Botón "Recargar" actualiza la lista
[ ] Botón "Gestionar Perfiles" navega correctamente
[ ] Tabla vacía muestra mensaje apropiado
[ ] Error 404 redirige a lista de administradoras
[ ] Breadcrumb funciona correctamente
```

#### ConjuntoUsuariosPage

```
[ ] Similar a AdministradoraUsuariosPage
[ ] Contexto de conjunto funciona correctamente
[ ] Navegación a gestión de perfiles usa ruta de conjunto
[ ] Error 404 redirige a lista de conjuntos
```

#### AsignarPerfilesPage

```
[ ] Información del usuario se muestra completa
[ ] Información del contexto se muestra
[ ] Badge de tipo de contexto es correcto
[ ] Perfiles actuales se listan correctamente
[ ] Botón "Remover" funciona en cada perfil
[ ] Confirmación al remover perfil aparece
[ ] Perfiles disponibles NO incluyen los ya asignados
[ ] Checkbox de selección funciona
[ ] Validación: no asignar sin selección
[ ] Asignar perfiles actualiza ambas listas
[ ] Toast de éxito al asignar
[ ] Toast de éxito al remover
[ ] Contador de seleccionados es correcto
[ ] Botón "Asignar" se deshabilita mientras envía
[ ] Si no hay perfiles disponibles, muestra mensaje
[ ] Botón "Volver" navega correctamente
```

#### UsuarioPerfilesPage

```
[ ] Información del usuario se muestra
[ ] Contextos se agrupan correctamente
[ ] Administradoras en sección separada
[ ] Conjuntos en sección separada
[ ] Perfiles por contexto se listan
[ ] Botones "Gestionar Perfiles" navegan correctamente
[ ] Si no hay perfiles, muestra mensaje apropiado
[ ] Loading funciona correctamente
[ ] Error 404 redirige a usuarios
[ ] Botón "Volver" funciona
```

#### Integración General

```
[ ] Token JWT se envía en headers
[ ] Error 401 redirige a login
[ ] Error 403 muestra mensaje de permisos
[ ] Error 404 se maneja apropiadamente
[ ] Navegación entre páginas fluida
[ ] Breadcrumbs reflejan ruta actual
[ ] Toasts se muestran en todas las acciones
[ ] URLs son RESTful y coherentes
```

---

## Consideraciones Importantes

### 1. Endpoints Requeridos

**⚠️ VERIFICAR** que estos endpoints existan en el backend:

```
GET    /administradoras/{id}
GET    /conjuntos/{id}
GET    /usuarios/{id}
GET    /perfiles
```

Si no existen, implementar alternativas o mocks temporales.

### 2. Validación de Perfiles por Nivel

**Regla de negocio**: Un usuario en una administradora (nivel 1) no debería tener perfiles de nivel 3 o 4 (Propietario/Residente).

**Implementación**:
```javascript
// Filtrar perfiles según el contexto
const getPerfilesValidosParaContexto = (perfiles, contextType) => {
  if (contextType === 'administradora') {
    // Solo niveles 0, 1, 2
    return perfiles.filter(p => p.nivel <= 2);
  } else {
    // Conjuntos: niveles 0, 2, 3, 4
    return perfiles.filter(p => p.nivel === 0 || p.nivel >= 2);
  }
};
```

### 3. Manejo de Errores HTTP

```javascript
try {
  await asignarPerfilesAdministradora(adminId, userId, data);
} catch (error) {
  switch (error.status) {
    case 400:
      showToast('Datos inválidos', 'error');
      break;
    case 404:
      showToast('Usuario o administradora no encontrado', 'error');
      break;
    case 409:
      showToast('El perfil ya está asignado', 'warning');
      break;
    default:
      showToast(error.message || 'Error desconocido', 'error');
  }
}
```

### 4. Optimización de Llamadas API

**Problema**: `AsignarPerfilesPage` hace múltiples llamadas al cargar.

**Solución**: Usar `Promise.all` para paralelizar:

```javascript
const fetchData = async () => {
  setLoading(true);
  try {
    const [usuarioData, contextData, perfilesData] = await Promise.all([
      getUsuarioById(usuarioId),
      isAdministradora 
        ? getAdministradoraById(contextId)
        : getConjuntoById(contextId),
      getPerfiles()
    ]);
    
    setUsuario(usuarioData);
    setContexto(contextData);
    // ... procesar perfiles
    
  } catch (error) {
    // manejar error
  } finally {
    setLoading(false);
  }
};
```

### 5. Navegación Dinámica

Las rutas deben ser flexibles para manejar tanto administradoras como conjuntos:

```jsx
// En AsignarPerfilesPage
const { contextType, contextId, usuarioId } = useParams();

// contextType puede ser 'administradora' o 'conjunto'
// Esto permite reutilizar el mismo componente
```

### 6. Consistencia en Badges

**Colores de badges**:
- Perfiles asignados: `badge-info` (azul)
- Estatus Activo: `badge-success` (verde)
- Estatus Inactivo: `badge-secondary` (gris)
- Tipo Administradora: `badge-primary` (azul oscuro)
- Tipo Conjunto: `badge-success` (verde)

### 7. Permisos de Acceso

**Consideración futura**: Validar que el usuario actual tenga permisos para:
- Ver usuarios de una administradora/conjunto
- Asignar perfiles a otros usuarios
- Remover perfiles

```javascript
// En AuthContext, agregar:
const canManageProfiles = () => {
  const userPermissions = currentUser?.permissions || [];
  return userPermissions.includes('MNJ') || userPermissions.includes('INS');
};
```

---

## Próximos Pasos

### Mejoras Futuras

1. **Asignación Masiva**:
   - Seleccionar múltiples usuarios
   - Asignar el mismo perfil a todos

2. **Filtros Avanzados**:
   - Filtrar por tipo de perfil
   - Filtrar por estatus de usuario
   - Filtrar por contexto

3. **Exportación**:
   - Exportar lista de usuarios con perfiles a CSV
   - Generar reporte de asignaciones

4. **Auditoría**:
   - Mostrar histórico de cambios de perfiles
   - Quién asignó/removió y cuándo

5. **Notificaciones**:
   - Notificar al usuario cuando se le asigna un perfil
   - Email de confirmación

6. **Búsqueda Global**:
   - Buscar usuarios por nombre a través de todos los contextos
   - Sugerir usuarios al asignar

7. **Drag & Drop**:
   - Arrastrar perfiles disponibles a asignados
   - Interfaz más visual

### Validación con Backend

Antes de considerar completo, validar:

```
[ ] Todos los endpoints existen y responden correctamente
[ ] Response structures coinciden con documentación
[ ] Error codes son los esperados (400, 401, 403, 404, 409)
[ ] Token JWT se valida en todas las peticiones
[ ] CORS configurado para dominios de frontend
[ ] Permisos de perfil controlan acceso
[ ] Contexto activo afecta las operaciones
```

---

## Archivos del Proyecto

### Archivos a Crear

```
src/pages/asignaciones/
├── AdministradoraUsuariosPage.jsx      [NUEVO - 300 líneas]
├── ConjuntoUsuariosPage.jsx            [NUEVO - 300 líneas]
├── AsignarPerfilesPage.jsx             [NUEVO - 450 líneas]
└── UsuarioPerfilesPage.jsx             [NUEVO - 250 líneas]
```

### Archivos a Modificar

```
src/
├── services/
│   └── api.js                          [MODIFICAR - +200 líneas]
│       └── 7 métodos nuevos
│
├── App.jsx                             [MODIFICAR - +15 líneas]
│   └── 5 rutas nuevas
│
└── hooks/
    └── Menu.json                       [MODIFICAR - opcional]
        └── Opciones de asignación
```

### Total de Líneas de Código

```
AdministradoraUsuariosPage:   ~300 líneas
ConjuntoUsuariosPage:         ~300 líneas
AsignarPerfilesPage:          ~450 líneas
UsuarioPerfilesPage:          ~250 líneas
api.js (agregado):            ~200 líneas
App.jsx (modificado):         ~15 líneas
─────────────────────────────────────────
TOTAL:                        ~1,515 líneas
```

---

## Referencias

- **Documentación API Backend**: `/docs/api/02-CRUD-ASIGNACION-PERFILES.md`
- **CRUD Perfiles Frontend**: `/docs/process/03-crud-perfiles-frontend.md`
- **Autenticación**: `/docs/process/01-autenticacion-autorizacion.md`
- **Contextos y Permisos**: `/docs/process/02-context-switching-permissions.md`
- **Modelo de Datos**: `/docs/general/03-modelo-datos.md`

---

## Conclusión

Este documento describe la implementación completa del frontend para la **Asignación de Perfiles a Usuarios**, integrándose perfectamente con:

- ✅ Backend REST API (7 endpoints)
- ✅ Sistema de autenticación JWT
- ✅ Control de contextos (Administradora/Conjunto)
- ✅ UI AdminLTE profesional
- ✅ Sistema de notificaciones Toast (ya existente)
- ✅ Arquitectura React moderna
- ✅ Navegación dinámica y RESTful

La implementación está lista para desarrollo siguiendo el orden recomendado en la Guía de Implementación.

**Estado**: Documentación completa - Listo para implementación  
**Fecha**: 25 de Marzo de 2026  
**Versión**: 1.0  
**Precedido por**: CRUD Perfiles (03-crud-perfiles-frontend.md)  
**Siguiente**: CRUD Módulos, Opciones y Acciones
