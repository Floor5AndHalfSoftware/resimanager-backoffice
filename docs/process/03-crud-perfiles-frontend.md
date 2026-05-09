# Implementación Frontend - CRUD de Perfiles

## Resumen Ejecutivo

Este documento describe la implementación completa del frontend para la gestión de perfiles de usuario (CRUD) en el SPA de ResiManager, integrándose con los endpoints del backend documentados en `/docs/api/01-CRUD-PERFILES.md`.

## Tabla de Contenidos

1. [Arquitectura Frontend](#arquitectura-frontend)
2. [Componentes y Páginas](#componentes-y-páginas)
3. [Integración con API](#integración-con-api)
4. [Sistema de Notificaciones](#sistema-de-notificaciones)
5. [Flujos de Usuario](#flujos-de-usuario)
6. [Guía de Implementación](#guía-de-implementación)
7. [Testing Manual](#testing-manual)
8. [Consideraciones Importantes](#consideraciones-importantes)

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

### Estructura de Archivos

```
resimanager-spa/src/
├── services/
│   └── api.js                          [MODIFICAR]
│       └── Agregar 8 métodos de perfiles
│
├── components/
│   └── common/
│       └── Toast.jsx                   [CREAR]
│           ├── Toast (componente individual)
│           ├── ToastContainer (gestor de toasts)
│           └── useToast (hook personalizado)
│
├── pages/
│   └── perfiles/
│       ├── PerfilesPage.jsx           [CREAR]
│       │   └── Lista con búsqueda y acciones
│       ├── PerfilFormPage.jsx         [CREAR]
│       │   └── Crear y editar perfiles
│       └── PerfilDetailPage.jsx       [CREAR]
│           └── Detalle + gestión de módulos
│
└── App.jsx                             [MODIFICAR]
    └── Agregar 4 rutas de perfiles
```

### Flujo de Datos

```
┌────────────────────────────────────────────────────────────┐
│                      Usuario                                │
└──────────────────────┬─────────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────────┐
│                 Páginas React                               │
│  PerfilesPage → PerfilFormPage → PerfilDetailPage         │
└──────────────────────┬─────────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────────┐
│              services/api.js (Capa API)                     │
│  getPerfiles(), createPerfil(), updatePerfil()...          │
└──────────────────────┬─────────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────────┐
│                  Backend REST API                           │
│          https://.../v1/perfiles                            │
└────────────────────────────────────────────────────────────┘
```

---

## Componentes y Páginas

### 1. PerfilesPage.jsx (Lista)

**Ubicación**: `/src/pages/perfiles/PerfilesPage.jsx`

**Responsabilidades**:
- Listar todos los perfiles del sistema
- Búsqueda por nombre de perfil
- Paginación básica
- Acciones: Ver, Editar, Eliminar
- Botón "Nuevo Perfil"

**Características Principales**:

```javascript
// Estado
const [perfiles, setPerfiles] = useState([]);
const [loading, setLoading] = useState(true);
const [error, setError] = useState(null);
const [searchTerm, setSearchTerm] = useState('');
const [currentPage, setCurrentPage] = useState(1);
const [totalPages, setTotalPages] = useState(1);

// Definición de columnas
const columns = [
    { key: 'id', label: 'ID' },
    { key: 'nombre', label: 'Nombre' },
    { key: 'descripcion', label: 'Descripción' },
    { 
        key: 'estatus', 
        label: 'Estatus',
        render: (value) => (
            <span className={`badge ${value === 'A' ? 'badge-success' : 'badge-secondary'}`}>
                {value === 'A' ? 'Activo' : 'Inactivo'}
            </span>
        )
    },
    { 
        key: 'nivel', 
        label: 'Nivel',
        render: (value) => {
            const config = getNivelConfig(value);
            return <span className={`badge ${config.colorClass}`}>{config.label}</span>;
        }
    },
    { key: 'usuarios_asignados', label: 'Usuarios' },
];

// Configuración de niveles
const getNivelConfig = (nivel) => {
    const niveles = {
        0: { label: 'Super Admin', colorClass: 'badge-danger' },
        1: { label: 'Admin General', colorClass: 'badge-warning' },
        2: { label: 'Admin Conjunto', colorClass: 'badge-primary' },
        3: { label: 'Propietario', colorClass: 'badge-info' },
        4: { label: 'Residente', colorClass: 'badge-secondary' }
    };
    return niveles[nivel] || { label: 'Desconocido', colorClass: 'badge-dark' };
};
```

**UI Layout**:

```
┌─────────────────────────────────────────────────────────────┐
│ Gestión de Perfiles                          [Nuevo Perfil] │
├─────────────────────────────────────────────────────────────┤
│ Buscar: [________________]  [🔍]                            │
├─────────────────────────────────────────────────────────────┤
│ ID │ Nombre               │ Nivel         │ Estatus │ ...  │
├────┼──────────────────────┼───────────────┼─────────┼──────┤
│ 1  │ Super Administrador  │ Super Admin   │ Activo  │ [⚙] │
│ 2  │ Admin General        │ Admin General │ Activo  │ [⚙] │
│ 3  │ Admin Conjunto       │ Admin Conjunto│ Activo  │ [⚙] │
└─────────────────────────────────────────────────────────────┘
            [◀ Anterior]  [1] 2 3  [Siguiente ▶]
```

**Acciones por Fila**:
```javascript
const renderActions = (perfil) => (
    <div className="btn-group">
        <button 
            className="btn btn-sm btn-info" 
            title="Ver Detalle"
            onClick={() => navigate(`/dashboard/perfiles/${perfil.id}`)}
        >
            <i className="fas fa-eye"></i>
        </button>
        <button 
            className="btn btn-sm btn-primary" 
            title="Editar"
            onClick={() => navigate(`/dashboard/perfiles/editar/${perfil.id}`)}
        >
            <i className="fas fa-edit"></i>
        </button>
        <button 
            className="btn btn-sm btn-danger" 
            title="Eliminar"
            onClick={() => handleDelete(perfil)}
        >
            <i className="fas fa-trash"></i>
        </button>
    </div>
);

const handleDelete = async (perfil) => {
    if (window.confirm(`¿Está seguro de eliminar el perfil "${perfil.nombre}"?`)) {
        try {
            await deletePerfil(perfil.id);
            showToast('Perfil eliminado correctamente', 'success');
            fetchPerfiles(); // Recargar lista
        } catch (error) {
            if (error.status === 409) {
                showToast('No se puede eliminar: perfil tiene usuarios asignados', 'error');
            } else {
                showToast(error.message, 'error');
            }
        }
    }
};
```

---

### 2. PerfilFormPage.jsx (Crear/Editar)

**Ubicación**: `/src/pages/perfiles/PerfilFormPage.jsx`

**Responsabilidades**:
- Crear nuevo perfil
- Editar perfil existente
- Validación de formulario
- Feedback con toasts

**Modo Dual**:
```javascript
// Detectar modo según URL
const { id } = useParams();
const isEditMode = !!id;
const pageTitle = isEditMode ? 'Editar Perfil' : 'Nuevo Perfil';
```

**Estado del Formulario**:
```javascript
const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    nivel: 2, // Default: Admin Conjunto
    estatus: 'A'
});

const [errors, setErrors] = useState({});
const [loading, setLoading] = useState(false);
const [submitting, setSubmitting] = useState(false);
```

**Validación**:
```javascript
const validate = () => {
    const newErrors = {};
    
    if (!formData.nombre.trim()) {
        newErrors.nombre = 'El nombre es requerido';
    } else if (formData.nombre.length < 3) {
        newErrors.nombre = 'El nombre debe tener al menos 3 caracteres';
    }
    
    if (!formData.descripcion.trim()) {
        newErrors.descripcion = 'La descripción es requerida';
    }
    
    if (formData.nivel === null || formData.nivel === '') {
        newErrors.nivel = 'Debe seleccionar un nivel';
    }
    
    return newErrors;
};
```

**Submit Handler**:
```javascript
const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validar
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
        setErrors(validationErrors);
        return;
    }
    
    setSubmitting(true);
    
    try {
        if (isEditMode) {
            await updatePerfil(id, formData);
            showToast('Perfil actualizado correctamente', 'success');
        } else {
            const result = await createPerfil(formData);
            showToast('Perfil creado correctamente', 'success');
        }
        
        // Navegar de vuelta a la lista
        navigate('/dashboard/perfiles');
    } catch (error) {
        showToast(error.message, 'error');
    } finally {
        setSubmitting(false);
    }
};
```

**UI Layout**:

```
┌─────────────────────────────────────────────────────────────┐
│ Nuevo Perfil                                                 │
├─────────────────────────────────────────────────────────────┤
│ Nombre: *                                                    │
│ [_____________________________]                              │
│                                                              │
│ Descripción: *                                              │
│ [_____________________________]                              │
│ [_____________________________]                              │
│ [_____________________________]                              │
│                                                              │
│ Nivel: *                                                    │
│ [▼ Seleccionar nivel          ]                             │
│   └─ 0: Super Administrador                                │
│   └─ 1: Administrador General                              │
│   └─ 2: Administrador de Conjunto  [SELECTED]             │
│   └─ 3: Propietario                                        │
│   └─ 4: Residente                                          │
│                                                              │
│ [Solo en edición]                                           │
│ Estatus:                                                    │
│ (•) Activo  ( ) Inactivo                                   │
│                                                              │
│ [Guardar]  [Cancelar]                                       │
└─────────────────────────────────────────────────────────────┘
```

**Componentes de Formulario**:
```javascript
import { FormInput, FormTextarea, FormSelect } from '@/components/forms';

// Input de nombre
<FormInput
    id="nombre"
    label="Nombre del Perfil"
    value={formData.nombre}
    onChange={(e) => setFormData({...formData, nombre: e.target.value})}
    error={errors.nombre}
    required
/>

// Textarea de descripción
<FormTextarea
    id="descripcion"
    label="Descripción"
    value={formData.descripcion}
    onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
    error={errors.descripcion}
    rows={3}
    required
/>

// Select de nivel
<FormSelect
    id="nivel"
    label="Nivel Jerárquico"
    value={formData.nivel}
    onChange={(e) => setFormData({...formData, nivel: parseInt(e.target.value)})}
    error={errors.nivel}
    required
>
    <option value="">-- Seleccionar --</option>
    <option value="0">Super Administrador</option>
    <option value="1">Administrador General</option>
    <option value="2">Administrador de Conjunto</option>
    <option value="3">Propietario</option>
    <option value="4">Residente</option>
</FormSelect>

// Select de estatus (solo edición)
{isEditMode && (
    <FormSelect
        id="estatus"
        label="Estatus"
        value={formData.estatus}
        onChange={(e) => setFormData({...formData, estatus: e.target.value})}
    >
        <option value="A">Activo</option>
        <option value="I">Inactivo</option>
    </FormSelect>
)}
```

---

### 3. PerfilDetailPage.jsx (Detalle + Módulos)

**Ubicación**: `/src/pages/perfiles/PerfilDetailPage.jsx`

**Responsabilidades**:
- Mostrar información completa del perfil
- Listar módulos asignados
- Asignar nuevos módulos
- Remover módulos
- Mostrar permisos por módulo

**Estado**:
```javascript
const [perfil, setPerfil] = useState(null);
const [modulosDisponibles, setModulosDisponibles] = useState([]);
const [modulosSeleccionados, setModulosSeleccionados] = useState([]);
const [loading, setLoading] = useState(true);
const [submitting, setSubmitting] = useState(false);
```

**Carga de Datos**:
```javascript
useEffect(() => {
    const loadData = async () => {
        try {
            setLoading(true);
            
            // Cargar perfil con detalles
            const perfilData = await getPerfilById(id);
            setPerfil(perfilData);
            
            // Cargar módulos disponibles
            const todosModulos = await getModulos();
            
            // Filtrar módulos ya asignados
            const modulosAsignadosIds = perfilData.modulos.map(m => m.id);
            const disponibles = todosModulos.filter(
                m => !modulosAsignadosIds.includes(m.id)
            );
            
            setModulosDisponibles(disponibles);
        } catch (error) {
            showToast(error.message, 'error');
        } finally {
            setLoading(false);
        }
    };
    
    loadData();
}, [id]);
```

**Asignar Módulos**:
```javascript
const handleAsignarModulos = async () => {
    if (modulosSeleccionados.length === 0) {
        showToast('Debe seleccionar al menos un módulo', 'warning');
        return;
    }
    
    setSubmitting(true);
    
    try {
        await asignarModulosPerfil(id, modulosSeleccionados);
        showToast(`${modulosSeleccionados.length} módulo(s) asignado(s) correctamente`, 'success');
        
        // Recargar datos
        const perfilActualizado = await getPerfilById(id);
        setPerfil(perfilActualizado);
        
        // Actualizar disponibles
        const todosModulos = await getModulos();
        const modulosAsignadosIds = perfilActualizado.modulos.map(m => m.id);
        setModulosDisponibles(
            todosModulos.filter(m => !modulosAsignadosIds.includes(m.id))
        );
        
        // Limpiar selección
        setModulosSeleccionados([]);
    } catch (error) {
        showToast(error.message, 'error');
    } finally {
        setSubmitting(false);
    }
};
```

**Remover Módulo**:
```javascript
const handleRemoverModulo = async (moduloId, moduloNombre) => {
    if (!window.confirm(`¿Remover el módulo "${moduloNombre}" de este perfil?`)) {
        return;
    }
    
    try {
        await removerModuloPerfil(id, moduloId);
        showToast('Módulo removido correctamente', 'success');
        
        // Recargar datos
        const perfilActualizado = await getPerfilById(id);
        setPerfil(perfilActualizado);
        
        // Actualizar disponibles
        const todosModulos = await getModulos();
        const modulosAsignadosIds = perfilActualizado.modulos.map(m => m.id);
        setModulosDisponibles(
            todosModulos.filter(m => !modulosAsignadosIds.includes(m.id))
        );
    } catch (error) {
        showToast(error.message, 'error');
    }
};
```

**UI Layout**:

```
┌─────────────────────────────────────────────────────────────┐
│ Detalle del Perfil                      [Editar] [Volver]  │
├─────────────────────────────────────────────────────────────┤
│ Información General                                          │
├─────────────────────────────────────────────────────────────┤
│ Nombre: Super Administrador                                  │
│ Descripción: Acceso total al sistema                        │
│ Nivel: Super Admin (0) │ Estatus: [✓ Activo]               │
│ Usuarios Asignados: 5                                       │
│ Fecha de Creación: 01/01/2024 10:00                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Módulos Asignados (3)                                       │
├─────────────────────────────────────────────────────────────┤
│ • Configuracion Sistema                        [Remover]    │
│ • Gestion Administradoras                      [Remover]    │
│ • Perfiles                                     [Remover]    │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Asignar Nuevos Módulos                                      │
├─────────────────────────────────────────────────────────────┤
│ Seleccionar módulos:                                        │
│ [ ] Propiedades                                             │
│ [ ] Pagos                                                   │
│ [ ] Reportes                                                │
│                                                              │
│ [Asignar Seleccionados]                                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Permisos por Módulo                                         │
├─────────────────────────────────────────────────────────────┤
│ Configuracion Sistema                                        │
│   Acciones: [MNJ] [INS] [MOD] [VIS] [EL]                   │
│                                                              │
│ Gestion Administradoras                                      │
│   Acciones: [MNJ] [INS] [MOD] [VIS] [EL]                   │
└─────────────────────────────────────────────────────────────┘
```

**Renderizado de Permisos**:
```javascript
const renderPermisos = () => {
    if (!perfil || !perfil.permisos || perfil.permisos.length === 0) {
        return <p className="text-muted">No hay permisos configurados</p>;
    }
    
    const accionesLabels = {
        'MNJ': { label: 'Manejar', icon: 'fa-cogs', color: 'primary' },
        'INS': { label: 'Insertar', icon: 'fa-plus', color: 'success' },
        'MOD': { label: 'Modificar', icon: 'fa-edit', color: 'warning' },
        'VIS': { label: 'Visualizar', icon: 'fa-eye', color: 'info' },
        'EL': { label: 'Eliminar', icon: 'fa-trash', color: 'danger' }
    };
    
    return perfil.permisos.map(permiso => (
        <div key={permiso.modulo_id} className="mb-3">
            <h6>{permiso.modulo}</h6>
            <div>
                {permiso.acciones.map(accion => {
                    const config = accionesLabels[accion] || { label: accion, icon: 'fa-check', color: 'secondary' };
                    return (
                        <span 
                            key={accion} 
                            className={`badge badge-${config.color} mr-2`}
                            title={config.label}
                        >
                            <i className={`fas ${config.icon}`}></i> {config.label}
                        </span>
                    );
                })}
            </div>
        </div>
    ));
};
```

---

### 4. Toast.jsx (Sistema de Notificaciones)

**Ubicación**: `/src/components/common/Toast.jsx`

**Características**:
- Toasts con estilos AdminLTE
- Auto-ocultado configurable
- Soporte para múltiples toasts simultáneos
- Tipos: success, error, warning, info

**Implementación**:

```javascript
import React, { useState, useEffect, createContext, useContext } from 'react';

// Context para gestionar toasts globalmente
const ToastContext = createContext();

// Componente individual de Toast
const Toast = ({ id, message, type, duration, onClose }) => {
    useEffect(() => {
        const timer = setTimeout(() => {
            onClose(id);
        }, duration);
        
        return () => clearTimeout(timer);
    }, [id, duration, onClose]);
    
    const typeConfig = {
        success: {
            icon: 'fas fa-check-circle',
            bgClass: 'bg-success',
            title: 'Éxito'
        },
        error: {
            icon: 'fas fa-exclamation-circle',
            bgClass: 'bg-danger',
            title: 'Error'
        },
        warning: {
            icon: 'fas fa-exclamation-triangle',
            bgClass: 'bg-warning',
            title: 'Advertencia'
        },
        info: {
            icon: 'fas fa-info-circle',
            bgClass: 'bg-info',
            title: 'Información'
        }
    };
    
    const config = typeConfig[type] || typeConfig.info;
    
    return (
        <div 
            className={`toast show ${config.bgClass}`}
            style={{
                position: 'relative',
                marginBottom: '10px',
                minWidth: '250px',
                boxShadow: '0 0.5rem 1rem rgba(0,0,0,0.15)'
            }}
        >
            <div className="toast-header">
                <i className={`${config.icon} mr-2`}></i>
                <strong className="mr-auto">{config.title}</strong>
                <button 
                    type="button" 
                    className="ml-2 mb-1 close" 
                    onClick={() => onClose(id)}
                >
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div className="toast-body text-white">
                {message}
            </div>
        </div>
    );
};

// Contenedor de toasts
export const ToastContainer = () => {
    const { toasts, removeToast } = useContext(ToastContext);
    
    return (
        <div 
            style={{
                position: 'fixed',
                top: '20px',
                right: '20px',
                zIndex: 9999
            }}
        >
            {toasts.map(toast => (
                <Toast
                    key={toast.id}
                    {...toast}
                    onClose={removeToast}
                />
            ))}
        </div>
    );
};

// Provider del contexto
export const ToastProvider = ({ children }) => {
    const [toasts, setToasts] = useState([]);
    
    const addToast = (message, type = 'info', duration = 3000) => {
        const id = Date.now() + Math.random();
        setToasts(prev => [...prev, { id, message, type, duration }]);
    };
    
    const removeToast = (id) => {
        setToasts(prev => prev.filter(toast => toast.id !== id));
    };
    
    return (
        <ToastContext.Provider value={{ toasts, addToast, removeToast }}>
            {children}
            <ToastContainer />
        </ToastContext.Provider>
    );
};

// Hook personalizado para usar toasts
export const useToast = () => {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error('useToast debe usarse dentro de ToastProvider');
    }
    
    return {
        showToast: context.addToast,
        success: (msg) => context.addToast(msg, 'success'),
        error: (msg) => context.addToast(msg, 'error'),
        warning: (msg) => context.addToast(msg, 'warning'),
        info: (msg) => context.addToast(msg, 'info')
    };
};
```

**Uso en Componentes**:
```javascript
import { useToast } from '@/components/common/Toast';

function MyComponent() {
    const { success, error } = useToast();
    
    const handleSave = async () => {
        try {
            await api.save();
            success('Guardado correctamente');
        } catch (err) {
            error(err.message);
        }
    };
}
```

---

## Integración con API

### Métodos API en services/api.js

**Ubicación**: `/src/services/api.js`

**Métodos a Agregar**:

```javascript
/**
 * Get all perfiles with optional filters
 * @param {Object} params - Query parameters (estatus, nivel, search, page, limit)
 * @returns {Promise} List of perfiles with pagination
 */
export const getPerfiles = async (params = {}) => {
    const queryParams = new URLSearchParams();
    
    if (params.estatus) queryParams.append('estatus', params.estatus);
    if (params.nivel !== undefined && params.nivel !== null) queryParams.append('nivel', params.nivel);
    if (params.search) queryParams.append('search', params.search);
    if (params.page) queryParams.append('page', params.page);
    if (params.limit) queryParams.append('limit', params.limit);
    
    const queryString = queryParams.toString();
    const url = `${BASE_URL}/perfiles${queryString ? '?' + queryString : ''}`;
    
    const response = await fetch(url, {
        method: 'GET',
        headers: getHeaders(true)
    });
    
    return handleResponse(response);
};

/**
 * Get perfil by ID with full details
 * @param {number} id - Perfil ID
 * @returns {Promise} Perfil with modules and permissions
 */
export const getPerfilById = async (id) => {
    const response = await fetch(`${BASE_URL}/perfiles/${id}`, {
        method: 'GET',
        headers: getHeaders(true)
    });
    
    return handleResponse(response);
};

/**
 * Create a new perfil
 * @param {Object} perfilData - { nombre, descripcion, nivel }
 * @returns {Promise} Created perfil
 */
export const createPerfil = async (perfilData) => {
    const response = await fetch(`${BASE_URL}/perfiles`, {
        method: 'POST',
        headers: getHeaders(true),
        body: JSON.stringify(perfilData)
    });
    
    return handleResponse(response);
};

/**
 * Update an existing perfil
 * @param {number} id - Perfil ID
 * @param {Object} perfilData - { nombre, descripcion, estatus }
 * @returns {Promise} Update result
 */
export const updatePerfil = async (id, perfilData) => {
    const response = await fetch(`${BASE_URL}/perfiles/${id}`, {
        method: 'PUT',
        headers: getHeaders(true),
        body: JSON.stringify(perfilData)
    });
    
    return handleResponse(response);
};

/**
 * Delete (inactivate) a perfil
 * @param {number} id - Perfil ID
 * @returns {Promise} Delete result
 */
export const deletePerfil = async (id) => {
    const response = await fetch(`${BASE_URL}/perfiles/${id}`, {
        method: 'DELETE',
        headers: getHeaders(true)
    });
    
    return handleResponse(response);
};

/**
 * Assign modules to a perfil
 * @param {number} perfilId - Perfil ID
 * @param {Array<number>} modulosIds - Array of module IDs
 * @returns {Promise} Assignment result
 */
export const asignarModulosPerfil = async (perfilId, modulosIds) => {
    const response = await fetch(`${BASE_URL}/perfiles/${perfilId}/modulos`, {
        method: 'POST',
        headers: getHeaders(true),
        body: JSON.stringify({ modulos: modulosIds })
    });
    
    return handleResponse(response);
};

/**
 * Remove a module from a perfil
 * @param {number} perfilId - Perfil ID
 * @param {number} moduloId - Module ID
 * @returns {Promise} Removal result
 */
export const removerModuloPerfil = async (perfilId, moduloId) => {
    const response = await fetch(`${BASE_URL}/perfiles/${perfilId}/modulos/${moduloId}`, {
        method: 'DELETE',
        headers: getHeaders(true)
    });
    
    return handleResponse(response);
};

/**
 * Get all available modules
 * @returns {Promise} List of modules
 */
export const getModulos = async () => {
    const response = await fetch(`${BASE_URL}/modulos`, {
        method: 'GET',
        headers: getHeaders(true)
    });
    
    return handleResponse(response);
};
```

**Actualizar Exports**:
```javascript
export default {
    // ... métodos existentes ...
    getPerfiles,
    getPerfilById,
    createPerfil,
    updatePerfil,
    deletePerfil,
    asignarModulosPerfil,
    removerModuloPerfil,
    getModulos
};
```

---

## Sistema de Notificaciones

### Integración del ToastProvider

**En App.jsx o main.jsx**:

```javascript
import { ToastProvider } from './components/common/Toast';

function App() {
    return (
        <Router>
            <AuthProvider>
                <ToastProvider>
                    <InnerApp />
                </ToastProvider>
            </AuthProvider>
        </Router>
    );
}
```

### Estilos AdminLTE

Los toasts usan clases nativas de AdminLTE:
- `.toast` - Contenedor base
- `.bg-success`, `.bg-danger`, `.bg-warning`, `.bg-info` - Colores
- `.toast-header`, `.toast-body` - Estructura

**CSS Adicional** (si es necesario):

```css
/* En index.css */
.toast {
    min-width: 250px;
    max-width: 350px;
    font-size: 0.875rem;
}

.toast-header {
    background-color: rgba(255, 255, 255, 0.15);
    color: white;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.toast-body {
    padding: 0.75rem;
}
```

---

## Flujos de Usuario

### Flujo 1: Crear Nuevo Perfil

```
1. Usuario: Dashboard → Perfiles → [Nuevo Perfil]
   ↓
2. Sistema: Navegar a /dashboard/perfiles/nuevo
   ↓
3. Usuario: Llenar formulario (nombre, descripción, nivel)
   ↓
4. Usuario: Click [Guardar]
   ↓
5. Sistema: Validar campos
   ├─ Errores → Mostrar mensajes de error en campos
   └─ OK → Continuar
   ↓
6. Sistema: POST /api/v1/perfiles
   ├─ Error → Toast de error
   └─ 201 Created → Toast de éxito
   ↓
7. Sistema: Navegar a /dashboard/perfiles
   ↓
8. Usuario: Ver perfil nuevo en la lista
```

### Flujo 2: Editar Perfil

```
1. Usuario: Lista de perfiles → Click [Editar] en fila
   ↓
2. Sistema: Navegar a /dashboard/perfiles/editar/:id
   ↓
3. Sistema: GET /api/v1/perfiles/:id
   ├─ Error 404 → Toast + Redirect a lista
   └─ 200 OK → Cargar datos en formulario
   ↓
4. Usuario: Modificar campos
   ↓
5. Usuario: Click [Guardar]
   ↓
6. Sistema: PUT /api/v1/perfiles/:id
   ├─ Error → Toast de error
   └─ 200 OK → Toast de éxito + Navegar a lista
```

### Flujo 3: Ver Detalle y Gestionar Módulos

```
1. Usuario: Lista → Click [Ver] en fila
   ↓
2. Sistema: Navegar a /dashboard/perfiles/:id
   ↓
3. Sistema: GET /api/v1/perfiles/:id
            GET /api/v1/modulos (paralelo)
   ├─ Error → Toast + Redirect
   └─ OK → Mostrar detalle
   ↓
4. Usuario: Ver información del perfil
   ↓
5. Usuario: Seleccionar módulos disponibles
   ↓
6. Usuario: Click [Asignar Seleccionados]
   ↓
7. Sistema: POST /api/v1/perfiles/:id/modulos
   ├─ Error → Toast de error
   └─ 200 OK → Toast de éxito + Recargar datos
   ↓
8. Usuario: Ver módulos asignados actualizados
   ↓
9. Usuario: Click [Remover] en un módulo
   ↓
10. Sistema: Confirmar acción
    ├─ Cancelar → No hacer nada
    └─ Confirmar → DELETE /api/v1/perfiles/:id/modulos/:moduloId
       ├─ Error → Toast de error
       └─ 200 OK → Toast de éxito + Recargar datos
```

### Flujo 4: Eliminar Perfil

```
1. Usuario: Lista → Click [Eliminar] en fila
   ↓
2. Sistema: Confirmar acción con window.confirm()
   ├─ Cancelar → No hacer nada
   └─ Confirmar → Continuar
   ↓
3. Sistema: DELETE /api/v1/perfiles/:id
   ├─ 200 OK → Toast de éxito + Recargar lista
   ├─ 409 Conflict → Toast "Perfil tiene usuarios asignados"
   └─ Otro error → Toast de error genérico
```

### Flujo 5: Búsqueda

```
1. Usuario: Escribir en campo de búsqueda
   ↓
2. Sistema: Debounce (500ms)
   ↓
3. Sistema: GET /api/v1/perfiles?search=<término>
   ├─ Error → Toast de error
   └─ 200 OK → Actualizar tabla
   ↓
4. Usuario: Ver resultados filtrados
```

---

## Guía de Implementación

### Orden de Implementación

```
Fase 1: Base
├─ 1. Toast.jsx (componente + provider + hook)
├─ 2. Métodos API en services/api.js
└─ 3. Integrar ToastProvider en App.jsx

Fase 2: Páginas Core
├─ 4. PerfilesPage.jsx (lista + búsqueda + acciones)
└─ 5. PerfilFormPage.jsx (crear + editar)

Fase 3: Páginas Avanzadas
└─ 6. PerfilDetailPage.jsx (detalle + módulos)

Fase 4: Integración
├─ 7. Agregar rutas en App.jsx
└─ 8. Agregar opción al menú (si es estático)

Fase 5: Testing
└─ 9. Pruebas manuales de todos los flujos
```

### Comandos de Desarrollo

```bash
# Iniciar el frontend
cd /path/to/resimanager-spa
npm run dev

# Abrir en navegador
# http://localhost:5173
```

### Variables de Entorno

Verificar `.env` en el SPA:

```env
VITE_API_BASE_URL=https://chilly-libbey-wtysoftware-aab36281.koyeb.app
VITE_API_VERSION=/v1
```

---

## Testing Manual

### Checklist de Pruebas

#### PerfilesPage (Lista)

```
[ ] Se cargan todos los perfiles al montar
[ ] Loading spinner se muestra mientras carga
[ ] Badge de estatus: Verde (Activo), Gris (Inactivo)
[ ] Badge de nivel: Colores correctos según jerarquía
[ ] Búsqueda por nombre funciona
[ ] Búsqueda con debounce (no consulta en cada tecla)
[ ] Botón "Nuevo Perfil" navega a formulario
[ ] Botón "Ver" navega a detalle
[ ] Botón "Editar" navega a formulario con datos
[ ] Botón "Eliminar" muestra confirmación
[ ] Eliminar exitoso muestra toast y recarga lista
[ ] Eliminar con error 409 muestra mensaje específico
[ ] Paginación funciona (si hay múltiples páginas)
[ ] Tabla vacía muestra mensaje "No hay registros"
[ ] Error de API muestra mensaje de error
```

#### PerfilFormPage (Crear)

```
[ ] Formulario carga vacío
[ ] Select de nivel tiene valor default (Admin Conjunto)
[ ] Campo "Estatus" NO se muestra en modo crear
[ ] Validación de nombre vacío funciona
[ ] Validación de descripción vacía funciona
[ ] Validación de nivel sin seleccionar funciona
[ ] Mensajes de error se muestran en los campos
[ ] Botón "Guardar" está deshabilitado mientras envía
[ ] Creación exitosa muestra toast de éxito
[ ] Creación exitosa navega a lista
[ ] Error de API muestra toast de error
[ ] Botón "Cancelar" navega a lista sin guardar
```

#### PerfilFormPage (Editar)

```
[ ] Formulario carga con datos del perfil
[ ] Campo "Estatus" SÍ se muestra en modo editar
[ ] Valores preseleccionados coinciden con BD
[ ] Actualización exitosa muestra toast
[ ] Actualización exitosa navega a lista
[ ] Error 404 (perfil no existe) redirige a lista
```

#### PerfilDetailPage

```
[ ] Información del perfil se muestra completa
[ ] Badge de estatus muestra color correcto
[ ] Badge de nivel muestra texto correcto
[ ] Lista de módulos asignados se carga
[ ] Lista de módulos disponibles se carga
[ ] Módulos disponibles NO incluyen los ya asignados
[ ] Checkbox de selección múltiple funciona
[ ] Asignar módulos sin selección muestra warning
[ ] Asignar módulos actualiza las listas
[ ] Toast de éxito al asignar módulos
[ ] Botón "Remover" muestra confirmación
[ ] Remover módulo actualiza las listas
[ ] Toast de éxito al remover módulo
[ ] Permisos por módulo se muestran correctamente
[ ] Badges de acciones tienen colores apropiados
[ ] Botón "Editar" navega a formulario
[ ] Botón "Volver" navega a lista
```

#### Sistema de Toasts

```
[ ] Toast de éxito: Fondo verde, icono check
[ ] Toast de error: Fondo rojo, icono exclamation
[ ] Toast de warning: Fondo amarillo, icono triangle
[ ] Toast de info: Fondo azul, icono info
[ ] Toast se auto-oculta después de 3 segundos
[ ] Botón X cierra el toast manualmente
[ ] Múltiples toasts se apilan correctamente
[ ] Toasts se posicionan en esquina superior derecha
[ ] Toasts no interfieren con la UI
```

#### Integración General

```
[ ] Token se envía en header Authorization
[ ] Error 401 redirige a login
[ ] Navegación con breadcrumbs funciona
[ ] Menú lateral muestra opción "Perfiles"
[ ] URL changes reflejan en navegación
[ ] Back button del navegador funciona
[ ] Refresh de página mantiene autenticación
```

### Datos de Prueba

**Usuarios para Testing** (de la documentación del backend):

```
Usuario: admin
Password: Admin2024! (Base64: QWRtaW4yMDI0IQ==)
Perfil: Super Administrador
```

**Perfiles Existentes** (bootstrap data):

```
ID 1: Super Administrador (Nivel 0)
ID 2: Administrador General (Nivel 1)
ID 3: Administrador de Conjunto (Nivel 2)
ID 4: Propietario (Nivel 3)
ID 5: Residente (Nivel 4)
```

### Casos de Prueba Específicos

#### CP-01: Crear perfil válido

```
Precondiciones: Usuario autenticado con permisos
Pasos:
1. Navegar a /dashboard/perfiles
2. Click "Nuevo Perfil"
3. Ingresar nombre: "Contador"
4. Ingresar descripción: "Acceso a módulos financieros"
5. Seleccionar nivel: 2
6. Click "Guardar"

Resultado Esperado:
- Toast verde: "Perfil creado correctamente"
- Navegación a /dashboard/perfiles
- "Contador" aparece en la lista
```

#### CP-02: Editar perfil con usuarios asignados

```
Precondiciones: Perfil con usuarios existe
Pasos:
1. En lista, click "Editar" en perfil con usuarios
2. Cambiar nombre a "Contador Senior"
3. Click "Guardar"

Resultado Esperado:
- Toast verde: "Perfil actualizado correctamente"
- Nombre actualizado en la lista
```

#### CP-03: Intentar eliminar perfil con usuarios

```
Precondiciones: Perfil ID 2 tiene usuarios asignados
Pasos:
1. En lista, click "Eliminar" en perfil ID 2
2. Confirmar eliminación

Resultado Esperado:
- Toast rojo: "No se puede eliminar: perfil tiene usuarios asignados"
- Perfil sigue en la lista
- Estatus no cambia
```

#### CP-04: Asignar módulos a perfil

```
Precondiciones: Perfil sin módulos asignados
Pasos:
1. Click "Ver" en perfil
2. En sección "Asignar Módulos", seleccionar 3 módulos
3. Click "Asignar Seleccionados"

Resultado Esperado:
- Toast verde: "3 módulo(s) asignado(s) correctamente"
- Módulos aparecen en "Módulos Asignados"
- Módulos desaparecen de "Módulos Disponibles"
```

#### CP-05: Remover módulo de perfil

```
Precondiciones: Perfil con módulos asignados
Pasos:
1. Click "Ver" en perfil
2. Click "Remover" en un módulo
3. Confirmar acción

Resultado Esperado:
- Toast verde: "Módulo removido correctamente"
- Módulo desaparece de "Módulos Asignados"
- Módulo aparece en "Módulos Disponibles"
```

---

## Consideraciones Importantes

### 1. Endpoint de Módulos

**⚠️ IMPORTANTE**: La documentación del backend no incluye el endpoint `GET /api/v1/modulos`.

**Soluciones posibles**:

**A. Verificar si existe el endpoint**:
```bash
curl -X GET https://.../v1/modulos \
  -H "Authorization: Bearer <token>"
```

**B. Si no existe, crear mock temporal**:
```javascript
// En services/api.js (temporal)
export const getModulos = async () => {
    // Mock data hasta que el endpoint esté disponible
    return Promise.resolve([
        { id: 1, nombre: 'Configuracion Sistema' },
        { id: 2, nombre: 'Gestion Administradoras' },
        { id: 3, nombre: 'Perfiles' },
        { id: 4, nombre: 'Propiedades' },
        { id: 5, nombre: 'Pagos' }
    ]);
};
```

**C. Usar módulos del perfil detalle**:
```javascript
// Obtener módulos desde otros perfiles
const getAllModulosFromPerfiles = async () => {
    const response = await getPerfiles();
    const modulos = new Map();
    
    for (const perfil of response.data) {
        const detail = await getPerfilById(perfil.id);
        detail.modulos.forEach(m => modulos.set(m.id, m));
    }
    
    return Array.from(modulos.values());
};
```

### 2. Manejo de Errores HTTP

**Códigos de Error Específicos**:

```javascript
try {
    await deletePerfil(id);
} catch (error) {
    switch (error.status) {
        case 400:
            showToast('Solicitud inválida', 'error');
            break;
        case 401:
            showToast('Sesión expirada', 'warning');
            navigate('/login');
            break;
        case 403:
            showToast('No tiene permisos para esta acción', 'error');
            break;
        case 404:
            showToast('Perfil no encontrado', 'error');
            break;
        case 409:
            showToast('No se puede eliminar: perfil tiene usuarios asignados', 'error');
            break;
        default:
            showToast(error.message || 'Error desconocido', 'error');
    }
}
```

### 3. Filtros en la Lista

La documentación menciona filtrar por "username", pero los endpoints soportan:
- `search` - Búsqueda por nombre de perfil
- `estatus` - Filtro por estatus (A/I)
- `nivel` - Filtro por nivel jerárquico

**Decisión**: Implementar solo búsqueda por nombre (según conversación).

**Futuro**: Agregar filtros avanzados en un panel colapsable.

### 4. Paginación

Backend soporta paginación con:
- `page` - Número de página (default: 1)
- `limit` - Registros por página (default: 25)

**Response**:
```json
{
    "data": [...],
    "total": 50,
    "page": 1,
    "limit": 25
}
```

**Implementación Frontend**:
```javascript
const [pagination, setPagination] = useState({
    currentPage: 1,
    limit: 25,
    total: 0,
    totalPages: 0
});

const handlePageChange = (newPage) => {
    setPagination(prev => ({ ...prev, currentPage: newPage }));
    fetchPerfiles({ page: newPage, limit: pagination.limit });
};
```

### 5. Optimización de Búsqueda (Debounce)

```javascript
import { useState, useEffect } from 'react';

const useDebounce = (value, delay = 500) => {
    const [debouncedValue, setDebouncedValue] = useState(value);
    
    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedValue(value);
        }, delay);
        
        return () => clearTimeout(handler);
    }, [value, delay]);
    
    return debouncedValue;
};

// En PerfilesPage
const [searchTerm, setSearchTerm] = useState('');
const debouncedSearch = useDebounce(searchTerm, 500);

useEffect(() => {
    fetchPerfiles({ search: debouncedSearch });
}, [debouncedSearch]);
```

### 6. Menú Estático vs Dinámico

**Si el menú es dinámico** (viene del backend según perfil):
- No se requiere modificar `Menu.json`
- El menú se obtendrá automáticamente vía `GET /api/v1/menu/perfil`
- Asegurar que el backend incluya la opción "Perfiles"

**Si el menú es estático** (`hooks/Menu.json`):
```json
{
    "id": "perfiles",
    "label": "Perfiles",
    "icon": "fas fa-user-shield",
    "path": "/dashboard/perfiles",
    "tipo": "O"
}
```

### 7. Permisos de Acciones

Códigos de acciones en el sistema:

| Código | Nombre | Descripción | Color Badge |
|--------|--------|-------------|-------------|
| MNJ | Manejar | Gestión completa | primary |
| INS | Insertar | Crear registros | success |
| MOD | Modificar | Actualizar registros | warning |
| VIS | Visualizar | Solo lectura | info |
| EL | Eliminar | Eliminar registros | danger |

### 8. Validaciones Frontend

**Reglas de Validación**:

```javascript
const validationRules = {
    nombre: {
        required: true,
        minLength: 3,
        maxLength: 100,
        pattern: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/  // Solo letras y espacios
    },
    descripcion: {
        required: true,
        minLength: 10,
        maxLength: 500
    },
    nivel: {
        required: true,
        in: [0, 1, 2, 3, 4]
    },
    estatus: {
        in: ['A', 'I']
    }
};
```

### 9. Loading States

**Estados de Carga Importantes**:

```javascript
// En lista
const [loading, setLoading] = useState(true);      // Carga inicial
const [refreshing, setRefreshing] = useState(false); // Recarga

// En formulario
const [submitting, setSubmitting] = useState(false); // Envío

// En detalle
const [loadingPerfil, setLoadingPerfil] = useState(true);
const [loadingModulos, setLoadingModulos] = useState(true);
const [assigningModulos, setAssigningModulos] = useState(false);
```

### 10. Accesibilidad

**Buenas Prácticas**:

```javascript
// Labels accesibles
<button 
    className="btn btn-sm btn-danger" 
    title="Eliminar perfil"
    aria-label={`Eliminar perfil ${perfil.nombre}`}
    onClick={() => handleDelete(perfil)}
>
    <i className="fas fa-trash"></i>
</button>

// Estados de loading
<div role="status" aria-live="polite">
    {loading && 'Cargando datos...'}
</div>

// Mensajes de error
<div role="alert" className="alert alert-danger">
    {error}
</div>
```

---

## Próximos Pasos

### Mejoras Futuras

1. **Filtros Avanzados**:
   - Panel colapsable con filtros adicionales
   - Filtro por estatus (Activo/Inactivo)
   - Filtro por nivel jerárquico
   - Filtro combinado

2. **Exportación**:
   - Exportar lista a CSV/Excel
   - Exportar detalle de perfil a PDF

3. **Auditoría**:
   - Mostrar histórico de cambios
   - Quién modificó y cuándo

4. **Permisos Granulares**:
   - UI para configurar permisos por módulo/acción
   - Asignación masiva de permisos

5. **Ordenamiento**:
   - Click en columnas para ordenar
   - Ordenamiento multi-columna

6. **Búsqueda Avanzada**:
   - Búsqueda por múltiples campos
   - Operadores AND/OR

7. **Bulk Actions**:
   - Selección múltiple de perfiles
   - Acciones en lote (eliminar, cambiar estatus)

### Validación con Backend

Antes de considerar completo, validar:

```
[ ] Endpoint GET /modulos existe o implementar alternativa
[ ] Response structures coinciden con documentación
[ ] Error codes son los esperados (400, 401, 403, 404, 409)
[ ] Token JWT se valida correctamente
[ ] Permisos de perfil controlan acceso a endpoints
[ ] Contexto activo afecta operaciones
```

---

## Archivos del Proyecto

### Archivos Creados

```
src/
├── components/
│   └── common/
│       └── Toast.jsx                    [NUEVO - 150 líneas]
│
└── pages/
    └── perfiles/
        ├── PerfilesPage.jsx            [NUEVO - 250 líneas]
        ├── PerfilFormPage.jsx          [NUEVO - 200 líneas]
        └── PerfilDetailPage.jsx        [NUEVO - 350 líneas]
```

### Archivos Modificados

```
src/
├── services/
│   └── api.js                          [MODIFICADO - +150 líneas]
│       └── 8 métodos nuevos de perfiles
│
├── App.jsx                             [MODIFICADO - +4 rutas]
│   └── Rutas de perfiles
│
└── hooks/
    └── Menu.json                       [MODIFICADO - +1 item]
        └── Opción "Perfiles" (si es estático)
```

### Total de Líneas de Código

```
Toast.jsx:              ~150 líneas
PerfilesPage.jsx:       ~250 líneas
PerfilFormPage.jsx:     ~200 líneas
PerfilDetailPage.jsx:   ~350 líneas
api.js (agregado):      ~150 líneas
App.jsx (modificado):   ~10 líneas
Menu.json (modificado): ~5 líneas
─────────────────────────────────────
TOTAL:                  ~1,115 líneas
```

---

## Referencias

- **Documentación API Backend**: `/docs/api/01-CRUD-PERFILES.md`
- **Autenticación**: `/docs/process/01-autenticacion-autorizacion.md`
- **Contextos y Permisos**: `/docs/process/02-context-switching-permissions.md`
- **Modelo de Datos**: `/docs/03-modelo-datos.md`
- **AdminLTE Docs**: https://adminlte.io/docs/3.0/
- **React Router**: https://reactrouter.com/en/main

---

## Conclusión

Este documento describe la implementación completa del frontend para el CRUD de perfiles, integrándose perfectamente con:

- ✅ Backend REST API (8 endpoints)
- ✅ Sistema de autenticación JWT
- ✅ Control de permisos por perfil
- ✅ UI AdminLTE profesional
- ✅ Arquitectura React moderna
- ✅ Gestión completa de módulos
- ✅ Sistema de notificaciones AdminLTE nativo

La implementación está lista para desarrollo siguiendo el orden recomendado en la Guía de Implementación.

**Estado**: Documentación completa - Listo para implementación
**Fecha**: Marzo 2026
**Versión**: 1.0
