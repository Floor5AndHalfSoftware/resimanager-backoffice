# Interfaz de Usuario (UX/UI)

## Plataforma Base

El sistema utiliza **AdminLTE 3** como framework de diseño, basado en Bootstrap 4.

### Características de AdminLTE 3

- Diseño responsive
- Menú lateral colapsable
- Barra de navegación superior
- Soporte para múltiples layouts
- Componentes pre-construidos (cards, modales, tablas)

## Layout Principal

### Distribución Global

```
┌─────────────────────────────────────────────────────────────┐
│                    BARRA DE NAVEGACIÓN                      │
│  [Logo]  [Contexto: Admin/Conjunto]  [Perfil]  [Logout]     │
├────────────┬────────────────────────────────────────────────┤
│            │                                                │
│   MENÚ     │              ÁREA DE TRABAJO                   │
│   LATERAL  │                                                │
│            │     ┌─────────────────────────────────┐       │
│  ┌──────┐  │     │  BREADCRUMB                     │       │
│  │Opción│  │     ├─────────────────────────────────┤       │
│  ├──────┤  │     │                                 │       │
│  │Opción│  │     │     CONTENIDO PRINCIPAL         │       │
│  ├──────┤  │     │     (DataTable / Formularios)   │       │
│  │Opción│  │     │                                 │       │
│  └──────┘  │     └─────────────────────────────────┘       │
│            │                                                │
├────────────┴────────────────────────────────────────────────┤
│                      PIE DE PÁGINA                          │
│                    [Versión] [Copyright]                    │
└─────────────────────────────────────────────────────────────┘
```

### Componentes del Layout

#### Barra de Navegación (Top Navbar)

```html
<nav class="main-header navbar">
  <ul class="navbar-nav">
    <!-- Logo / Toggle sidebar -->
    <li class="nav-item">
      <a class="nav-link" data-widget="pushmenu">
        <i class="fas fa-bars"></i>
      </a>
    </li>
  </ul>
  
  <ul class="navbar-nav ml-auto">
    <!-- Selector de contexto -->
    <li class="nav-item dropdown">
      <select id="select-administradora">...</select>
      <select id="select-conjunto">...</select>
      <select id="select-perfil">...</select>
    </li>
    
    <!-- Menú de usuario -->
    <li class="nav-item dropdown">
      <a class="nav-link" data-toggle="dropdown">
        <i class="far fa-user"></i>
        <span>{{usuario.nombre}}</span>
      </a>
      <div class="dropdown-menu">
        <a class="dropdown-item" href="/perfil">Mi Perfil</a>
        <div class="dropdown-divider"></div>
        <a class="dropdown-item" href="/logout">Cerrar Sesión</a>
      </div>
    </li>
  </ul>
</nav>
```

#### Menú Lateral (Sidebar)

```html
<aside class="main-sidebar sidebar-dark-primary">
  <div class="sidebar">
    <!-- Panel de búsqueda -->
    <div class="user-panel mt-3 pb-3 mb-3 d-flex">
      <div class="image">
        <img src="user-avatar.jpg" class="img-circle">
      </div>
      <div class="info">
        <a href="#" class="d-block">{{usuario.nombre}}</a>
      </div>
    </div>
    
    <!-- Navegación -->
    <nav class="mt-2">
      <ul class="nav nav-pills nav-sidebar flex-column">
        <li class="nav-item">
          <a class="nav-link" href="/dashboard">
            <i class="nav-icon fas fa-tachometer-alt"></i>
            <p>Dashboard</p>
          </a>
        </li>
        
        <!-- Menú dinámico según perfil -->
        <li class="nav-item menu-open">
          <a class="nav-link active">
            <i class="nav-icon fas fa-cog"></i>
            <p>
              Configuración
              <i class="right fas fa-angle-left"></i>
            </p>
          </a>
          <ul class="nav nav-treeview">
            <li class="nav-item">
              <a class="nav-link" href="/usuarios">
                <i class="far fa-circle nav-icon"></i>
                <p>Usuarios</p>
              </a>
            </li>
          </ul>
        </li>
      </ul>
    </nav>
  </div>
</aside>
```

## Páginas Principales

### Login

```
┌─────────────────────────────────────────────┐
│                                             │
│           ┌───────────────────┐             │
│           │      LOGO         │             │
│           ├───────────────────┤             │
│           │  Usuario          │             │
│           │  [______________] │             │
│           │                   │             │
│           │  Contraseña       │             │
│           │  [______________] │             │
│           │                   │             │
│           │  [  Iniciar  ]    │             │
│           │                   │             │
│           │  ¿Olvidó su       │             │
│           │  contraseña?      │             │
│           └───────────────────┘             │
│                                             │
└─────────────────────────────────────────────┘
```

**Componente:** `LoginPage.jsx`

```jsx
// Estructura de la página de login
<div className="login-page">
  <div className="login-box">
    <div className="login-logo">
      <img src="logo.png" alt="ResiManager" />
    </div>
    <div className="card">
      <div className="card-body login-card-body">
        <p className="login-box-msg">Inicie sesión</p>
        <form onSubmit={handleLogin}>
          <div className="input-group mb-3">
            <input type="text" className="form-control" 
                   placeholder="Usuario" 
                   value={usuario} 
                   onChange={e => setUsuario(e.target.value)} />
            <div className="input-group-append">
              <span className="fa fa-user"></span>
            </div>
          </div>
          <div className="input-group mb-3">
            <input type="password" className="form-control" 
                   placeholder="Contraseña" />
            <div className="input-group-append">
              <span className="fa fa-lock"></span>
            </div>
          </div>
          <button type="submit" className="btn btn-primary btn-block">
            Iniciar Sesión
          </button>
        </form>
      </div>
    </div>
  </div>
</div>
```

### Dashboard

```
┌─────────────────────────────────────────────────────────────┐
│  Breadcrumb: Inicio > Dashboard                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Conjuntos   │  │ Propiedades │  │ Usuarios    │         │
│  │     12      │  │    245      │  │     38      │         │
│  │  +2 este mes│  │  +15 mes    │  │  +5 mes     │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │             ACTIVIDAD RECIENTE                        │ │
│  │  • Nuevo usuario registrado - hace 2 horas            │ │
│  │  • Propiedad #45 actualizada - hace 5 horas           │ │
│  │  • Invitación enviada a user@email.com - ayer         │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Componente:** `DashboardPage.jsx`

### Lista de Registros (DataTable)

```
┌─────────────────────────────────────────────────────────────┐
│  Breadcrumb: Inicio > Configuración > Usuarios              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Usuarios                                    [ + Nuevo ]    │
│                                                             │
│  Buscar: [________________]                   Mostrar: 25▼  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ # │ Nombre  │ Email          │ Estatus │ Acciones    │ │
│  ├───┼─────────┼────────────────┼─────────┼─────────────┤ │
│  │ 1 │ Juan P  │ juan@email.com │ Activo  │ 🔍 ✏️ 🗑️   │ │
│  │ 2 │ María G │ maria@mail.com │ Activo  │ 🔍 ✏️ 🗑️   │ │
│  │ 3 │ Carlos  │ carlos@mail    │ Inactivo│ 🔍 ✏️ 🗑️   │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  Mostrando 1-25 de 38 registros                             │
│  [<] [1] [2] [>]                                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Patrones de Interacción

### Ventanas Modales

Las transacciones se realizan en ventanas emergentes sobre fondo opaco.

```
┌─────────────────────────────────────────────────────────────┐
│ ███████████████████████████████████████████████████████████ │
│ ██████████ ┌─────────────────────────────────┐ ████████████ │
│ ██████████ │  Editar Usuario              ✕  │ ████████████ │
│ ██████████ ├─────────────────────────────────┤ ████████████ │
│ ██████████ │                                 │ ████████████ │
│ ██████████ │  [General] [Permisos] [Logs]    │ ████████████ │
│ ██████████ │  ─────────────────────────────  │ ████████████ │
│ ██████████ │                                 │ ████████████ │
│ ██████████ │  Nombre: [_______________]      │ ████████████ │
│ ██████████ │  Email:  [_______________]      │ ████████████ │
│ ██████████ │  Estatus: [Activo ▼]            │ ████████████ │
│ ██████████ │                                 │ ████████████ │
│ ██████████ │  [Cancelar]  [Guardar]          │ ████████████ │
│ ██████████ │                                 │ ████████████ │
│ ██████████ └─────────────────────────────────┘ ████████████ │
│ ███████████████████████████████████████████████████████████ │
└─────────────────────────────────────────────────────────────┘
```

### Organización por Pestañas

Las ventanas modales pueden contener múltiples pestañas:

```jsx
<div className="modal-body">
  <ul className="nav nav-tabs" id="tabContent">
    <li className="nav-item">
      <a className="nav-link active" data-toggle="tab" href="#general">
        General
      </a>
    </li>
    <li className="nav-item">
      <a className="nav-link" data-toggle="tab" href="#permisos">
        Permisos
      </a>
    </li>
    <li className="nav-item">
      <a className="nav-link" data-toggle="tab" href="#historial">
        Historial
      </a>
    </li>
  </ul>
  
  <div className="tab-content">
    <div className="tab-pane active" id="general">
      {/* Formulario de datos generales */}
    </div>
    <div className="tab-pane" id="permisos">
      {/* Asignación de permisos */}
    </div>
    <div className="tab-pane" id="historial">
      {/* Log de cambios */}
    </div>
  </div>
</div>
```

### Confirmación de Eliminación

```
┌─────────────────────────────────────┐
│  ⚠️ Confirmar Eliminación           │
├─────────────────────────────────────┤
│                                     │
│  ¿Está seguro que desea eliminar    │
│  el usuario "Juan Pérez"?           │
│                                     │
│  Esta acción no se puede deshacer.  │
│                                     │
│         [Cancelar]  [Eliminar]      │
│                                     │
└─────────────────────────────────────┘
```

## Componentes Reutilizables

### DataTable Configurado

```jsx
// Componente DataTable estándar
<DataTable
  columns={[
    { data: 'id', title: '#' },
    { data: 'nombre', title: 'Nombre' },
    { data: 'email', title: 'Email' },
    { data: 'estatus', title: 'Estatus' },
    { 
      data: null, 
      title: 'Acciones',
      render: (data, type, row) => `
        <button class="btn-ver" onclick="ver(${row.id})">🔍</button>
        <button class="btn-editar" onclick="editar(${row.id})">✏️</button>
        <button class="btn-eliminar" onclick="eliminar(${row.id})">🗑️</button>
      `
    }
  ]}
  ajax={{
    url: '/api/usuarios',
    dataSrc: 'data'
  }}
  buttons={[
    { extend: 'create', text: '<i class="fa fa-plus"></i> Nuevo' }
  ]}
/>
```

### Selectores de Contexto

```jsx
// Selects en cascada para cambio de contexto
<div className="context-selectors">
  <select 
    id="select-administradora"
    onChange={handleAdministradoraChange}
    value={administradoraId}
  >
    <option value="">Seleccione Administradora</option>
    {administradoras.map(adm => (
      <option key={adm.id} value={adm.id}>
        {adm.nombre}
      </option>
    ))}
  </select>
  
  <select 
    id="select-conjunto"
    onChange={handleConjuntoChange}
    value={conjuntoId}
    disabled={!administradoraId}
  >
    <option value="">Seleccione Conjunto</option>
    {conjuntos.map(conj => (
      <option key={conj.id} value={conj.id}>
        {conj.nombre}
      </option>
    ))}
  </select>
  
  <select 
    id="select-perfil"
    onChange={handlePerfilChange}
    value={perfilId}
    disabled={!conjuntoId}
  >
    <option value="">Seleccione Perfil</option>
    {perfiles.map(prf => (
      <option key={prf.id} value={prf.id}>
        {prf.nombre}
      </option>
    ))}
  </select>
</div>
```

### Breadcrumb

```jsx
// Componente Breadcrumb.jsx
function Breadcrumb({ items }) {
  return (
    <ol className="breadcrumb">
      {items.map((item, index) => (
        <li 
          key={index} 
          className={`breadcrumb-item ${index === items.length - 1 ? 'active' : ''}`}
        >
          {index === items.length - 1 ? (
            item.label
          ) : (
            <a href={item.href}>{item.label}</a>
          )}
        </li>
      ))}
    </ol>
  );
}

// Uso
<Breadcrumb items={[
  { label: 'Inicio', href: '/dashboard' },
  { label: 'Configuración', href: '/configuracion' },
  { label: 'Usuarios' }
]} />
```

## Iconografía

### Iconos FontAwesome

| Acción | Icono | Clase |
|--------|-------|-------|
| Ver | 🔍 | `fa-search` o `fa-eye` |
| Editar | ✏️ | `fa-edit` o `fa-pencil` |
| Eliminar | 🗑️ | `fa-trash` o `fa-times` |
| Crear | + | `fa-plus` |
| Guardar | 💾 | `fa-save` |
| Cancelar | ✕ | `fa-times` |
| Configurar | ⚙️ | `fa-cog` |
| Usuario | 👤 | `fa-user` |
| Menú | ☰ | `fa-bars` |
| Logout | 🚪 | `fa-sign-out-alt` |

### Colores de Estado

| Estado | Color | Clase Bootstrap |
|--------|-------|-----------------|
| Activo | Verde | `badge-success` |
| Inactivo | Rojo | `badge-danger` |
| Pendiente | Amarillo | `badge-warning` |
| Vencido | Gris | `badge-secondary` |

## Responsive Design

### Breakpoints

| Dispositivo | Ancho | Comportamiento |
|-------------|-------|----------------|
| Desktop | > 992px | Layout completo |
| Tablet | 768-991px | Sidebar colapsable |
| Mobile | < 768px | Sidebar oculto, hamburger menu |

### Adaptaciones Mobile

```css
/* Sidebar oculto en mobile */
@media (max-width: 767.98px) {
  .main-sidebar {
    transform: translateX(-250px);
  }
  
  .sidebar-open .main-sidebar {
    transform: translateX(0);
  }
}
```

## Estados de Carga

### Spinner Global

```jsx
function LoadingSpinner() {
  return (
    <div className="overlay">
      <i className="fas fa-3x fa-sync-alt fa-spin"></i>
      <div className="text">Cargando...</div>
    </div>
  );
}
```

### Skeleton Loading

```jsx
function TableSkeleton() {
  return (
    <div className="skeleton-table">
      {[...Array(5)].map((_, i) => (
        <div key={i} className="skeleton-row">
          <div className="skeleton-cell"></div>
          <div className="skeleton-cell"></div>
          <div className="skeleton-cell"></div>
        </div>
      ))}
    </div>
  );
}
```

## Notificaciones

### Toast Messages

```jsx
// Notificación de éxito
toast.success('Usuario creado correctamente');

// Notificación de error
toast.error('Error al guardar los cambios');

// Notificación de advertencia
toast.warning('La sesión expirará en 5 minutos');
```

### Alertas Inline

```jsx
<div className="alert alert-info alert-dismissible">
  <button type="button" className="close" data-dismiss="alert">×</button>
  <h5><i className="icon fas fa-info"></i> Información</h5>
  Los cambios se guardarán automáticamente.
</div>
```
