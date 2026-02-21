# Configuración de Usuarios, Contextos y Permisos

## 🔐 Credenciales de Usuarios

Todos los usuarios tienen contraseñas hasheadas con BCrypt (strength 10).

---

## 👤 Usuario 1: ADMIN (Super Administrador)

**Credenciales:**
- Usuario: `admin`
- Contraseña: `Admin2024!`

**Contextos Disponibles:**
1. **Administradora: ResiManager - Administradora del Sistema (ID: 99)**
   - Perfil: Super Administrador (ID: 1)
   - Acceso total al sistema

**Configuración:**
- Definido en: `V2.0.5__INSERT_BASE_DATA.sql`
- Contexto asignado en: `V2.0.7__ASSIGN_ADMIN_CONTEXT.sql`

---

## 👤 Usuario 2: CARLOS MARTÍNEZ

**Credenciales:**
- Usuario: `cmartinez`
- Contraseña: `Carlos2024!`
- Email: carlos.martinez@inmobiliariaabc.com
- Teléfono: +58412-1234567

**Contextos Disponibles:**
1. **Administradora: Inmobiliaria ABC (ID: 1)**
   - Perfil: Administrador General (ID: 2)
   - Gestiona conjuntos administrados por Inmobiliaria ABC
   - Conjuntos bajo su administración: Residencial Las Flores

**Permisos:**
- Gestión completa de conjuntos de la administradora
- Creación y administración de contratos
- Gestión de propiedades y propietarios
- Reportes y facturación

---

## 👤 Usuario 3: MARÍA RODRÍGUEZ

**Credenciales:**
- Usuario: `mrodriguez`
- Contraseña: `Maria2024!`
- Email: maria.rodriguez@lasflores.com
- Teléfono: +58424-2345678

**Contextos Disponibles:**
1. **Conjunto: Residencial Las Flores (ID: 1)**
   - Perfil: Administrador de Conjunto (ID: 3)
   - Administra el conjunto residencial Las Flores

**Permisos:**
- Gestión de residentes y propietarios del conjunto
- Administración de áreas comunes
- Gestión de pagos de condominio
- Reportes del conjunto
- Comunicaciones con residentes

**Propiedades del Conjunto:**
- Apartamento A-101 (Propietario: Juan Pérez)
- Apartamento A-102 (Residente: Ana García)
- Casa C-01

---

## 👤 Usuario 4: JUAN PÉREZ

**Credenciales:**
- Usuario: `jperez`
- Contraseña: `Juan2024!`
- Email: juan.perez@email.com
- Teléfono: +58414-3456789

**Contextos Disponibles:**
1. **Conjunto: Residencial Las Flores (ID: 1)**
   - Perfil: Propietario (ID: 4)
   - Propietario de A-101

**Permisos:**
- Consulta de estados de cuenta
- Pago de cuotas de condominio
- Reportes de incidencias
- Reserva de áreas comunes
- Consulta de documentos del conjunto

**Propiedades:**
- A-101 en Residencial Las Flores (100% participación)

---

## 👤 Usuario 5: ANA GARCÍA

**Credenciales:**
- Usuario: `agarcia`
- Contraseña: `Ana2024!`
- Email: ana.garcia@email.com
- Teléfono: +58426-4567890

**Contextos Disponibles:**
1. **Conjunto: Residencial Las Flores (ID: 1)**
   - Perfil: Residente (ID: 5)
   - Residente de A-102 (arrendataria)

**Permisos:**
- Consulta de información del conjunto
- Reportes de incidencias
- Reserva de áreas comunes (si está habilitado)
- Consulta de documentos públicos del conjunto

**Propiedades:**
- A-102 en Residencial Las Flores (arrendataria - 0% participación)

---

## 👤 Usuario 6: LUIS GÓMEZ (MULTI-CONTEXTO)

**Credenciales:**
- Usuario: `lgomez`
- Contraseña: `Luis2024!`
- Email: luis.gomez@email.com
- Teléfono: +58412-5678901

**Contextos Disponibles:**
1. **Administradora: Inmobiliaria ABC (ID: 1)**
   - Perfil: Administrador General (ID: 2)
   - Gestiona conjuntos de Inmobiliaria ABC

2. **Administradora: Administradora XYZ (ID: 2)**
   - Perfil: Administrador General (ID: 2)
   - Gestiona conjuntos de Administradora XYZ

3. **Conjunto: Edificio Torre Mayor (ID: 2)**
   - Perfil 1: Administrador de Conjunto (ID: 3)
   - Perfil 2: Propietario (ID: 4)
   - Administra Torre Mayor Y es propietario del Penthouse PH-01

4. **Conjunto: Centro Comercial Plaza Norte (ID: 3)**
   - Perfil: Administrador de Conjunto (ID: 3)
   - Administra el centro comercial

**Permisos:**
- **Como Admin General:** Gestión completa de múltiples conjuntos
- **Como Admin Conjunto:** Administración de conjuntos específicos
- **Como Propietario:** Derechos de propiedad sobre PH-01 (Torre Mayor)

**Propiedades:**
- PH-01 Penthouse en Torre Mayor (100% participación)

---

## 📊 Resumen de Estructura

### Administradoras
- **Inmobiliaria ABC (ID: 1)**
  - Administradores: Carlos Martínez, Luis Gómez
  - Conjuntos gestionados: Residencial Las Flores

- **Administradora XYZ (ID: 2)**
  - Administradores: Luis Gómez
  - Conjuntos gestionados: Torre Mayor, Plaza Norte

### Conjuntos
- **Residencial Las Flores (ID: 1)**
  - Administrado por: Inmobiliaria ABC
  - Admin Conjunto: María Rodríguez
  - Propietarios: Juan Pérez (A-101)
  - Residentes: Ana García (A-102)

- **Edificio Torre Mayor (ID: 2)**
  - Administrado por: Administradora XYZ
  - Admin Conjunto: Luis Gómez
  - Propietarios: Luis Gómez (PH-01)

- **Centro Comercial Plaza Norte (ID: 3)**
  - Administrado por: Administradora XYZ
  - Admin Conjunto: Luis Gómez

---

## 🧪 Casos de Prueba

### 1. Usuario con Un Solo Contexto
**Usuario:** `cmartinez` / `Carlos2024!`
- Login → Se muestra 1 contexto → Auto-selección → Dashboard

### 2. Usuario Simple con Contexto de Conjunto
**Usuario:** `mrodriguez` / `Maria2024!`
- Login → 1 contexto de conjunto → Auto-selección → Dashboard de conjunto

### 3. Usuario Propietario
**Usuario:** `jperez` / `Juan2024!`
- Login → 1 contexto → Dashboard de propietario con sus propiedades

### 4. Usuario Residente (Permisos Limitados)
**Usuario:** `agarcia` / `Ana2024!`
- Login → 1 contexto → Dashboard de residente

### 5. Usuario Multi-Contexto (Caso Complejo)
**Usuario:** `lgomez` / `Luis2024!`
- Login → Selector de contexto con 4 opciones:
  1. Inmobiliaria ABC - Administrador General
  2. Administradora XYZ - Administrador General
  3. Torre Mayor - Administrador de Conjunto (también puede ver como Propietario)
  4. Plaza Norte - Administrador de Conjunto
- Usuario selecciona contexto → Dashboard correspondiente

### 6. Super Administrador
**Usuario:** `admin` / `Admin2024!`
- Login → 1 contexto (Sistema) → Dashboard con acceso total

---

## 🔑 IDs de Perfiles

| ID | Nombre Perfil | Nivel | Descripción |
|----|---------------|-------|-------------|
| 1 | Super Administrador | 0 | Acceso total al sistema |
| 2 | Administrador General | 1 | Gestión de administradora |
| 3 | Administrador de Conjunto | 2 | Gestión de conjunto |
| 4 | Propietario | 3 | Dueño de propiedad |
| 5 | Residente | 3 | Residente/Arrendatario |

---

## 📝 Notas de Implementación

1. **Contraseñas BCrypt**: Todas las contraseñas están hasheadas con BCrypt strength 10
2. **Contexto Múltiple**: Luis Gómez demuestra el flujo completo de selección de contexto
3. **Perfiles por Entidad**: Los perfiles se asignan específicamente a cada contexto (Administradora o Conjunto)
4. **Auto-selección**: Si el usuario tiene un solo contexto, se selecciona automáticamente
5. **Tokens JWT**: Incluyen información del contexto activo y perfil seleccionado

---

## 🚀 Flujo de Login Implementado

```
1. Usuario ingresa credenciales
   ↓
2. Backend valida usuario/contraseña
   ↓
3. Backend consulta contextosDisponibles (PersAdministradora + PersConjunto)
   ↓
4. Backend devuelve:
   - Token JWT inicial
   - usuario { id, nombre, apellido, email }
   - contextosDisponibles [ { tipo, administradora/conjunto, perfilesDisponibles } ]
   ↓
5. Frontend aplana contextos a: { tipo, entidadId, entidadNombre, perfilId, perfilNombre }
   ↓
6. SI tiene 1 solo contexto:
   → Auto-selección
   SINO:
   → Mostrar ContextSelector
   ↓
7. Frontend llama POST /v1/contexto/cambiar con { tipo, entidadId, perfilId }
   ↓
8. Backend valida acceso y devuelve:
   - Token JWT con contexto activo
   - contextoActual { tipo, entidadId, entidadNombre, perfilId, perfilNombre }
   ↓
9. Frontend llama GET /v1/menu/perfil con header X-Perfil-Id
   ↓
10. Backend devuelve menú filtrado por permisos del perfil
    ↓
11. Dashboard renderiza con menú personalizado
```

---

## ✅ Estado Actual

- ✅ Usuarios creados con contraseñas BCrypt únicas
- ✅ Administradoras y Conjuntos configurados
- ✅ Relaciones Persona ↔ Administradora establecidas
- ✅ Relaciones Persona ↔ Conjunto establecidas
- ✅ Perfiles asignados a cada contexto
- ✅ Propiedades y propietarios vinculados
- ✅ Contratos de administración creados
- ✅ Super Admin con contexto especial configurado
- ✅ Usuario multi-contexto (lgomez) para pruebas complejas

**Listo para pruebas de integración frontend-backend completas** ✨
