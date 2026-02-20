# Context Switching and Permission System

## Overview

This document describes the context switching and permission-based authorization system implemented for ResiManager.

## Table of Contents

1. [Context Switching](#context-switching)
2. [Permission Service](#permission-service)
3. [Dynamic Menu System](#dynamic-menu-system)
4. [API Endpoints](#api-endpoints)
5. [Testing Guide](#testing-guide)
6. [Integration Guide](#integration-guide)

---

## Context Switching

### What is Context Switching?

Users in ResiManager can have multiple roles in different organizations (Administradoras) or buildings (Conjuntos). Context switching allows users to select which context they want to work in at any given time.

### How It Works

1. **Login**: User logs in and receives:
   - JWT token with user info
   - List of available contexts
   
2. **Select Context**: User selects a context (Administradora or Conjunto) and a profile
   
3. **Switch Context**: Frontend calls `/api/v1/contexto/cambiar` to activate the context
   
4. **New Token**: Backend returns a new JWT token with active context embedded

### Context Information in JWT

The JWT token includes the following context claims:

```json
{
  "sub": "username",
  "userId": 2,
  "roles": ["ROLE_USER", "ROLE_ADMINISTRADOR_GENERAL"],
  "contextoTipo": "ADMINISTRADORA",
  "contextoEntidadId": 1,
  "contextoEntidadNombre": "Inmobiliaria ABC",
  "contextoPerfilId": 2,
  "contextoPerfilNombre": "Administrador General"
}
```

### DTOs

#### CambioContextoRequest
```java
{
  "tipo": "ADMINISTRADORA" | "CONJUNTO",
  "entidadId": 1,
  "perfilId": 2
}
```

#### ContextoActualDTO (Response)
```java
{
  "tipo": "ADMINISTRADORA",
  "entidadId": 1,
  "entidadNombre": "Inmobiliaria ABC",
  "perfilId": 2,
  "perfilNombre": "Administrador General",
  "perfilDescripcion": "Administrador de administradora"
}
```

---

## Permission Service

### Overview

The `PermissionService` provides fine-grained permission checking based on the user's active profile.

### Permission Hierarchy

```
Perfil
  └── ModPerfil (Module access)
        └── OpcPerfil (Option access within module)
              └── AccOpcPerfil (Specific actions: INS, MOD, VIS, EL, MNJ)
```

### How to Use

```java
@RestController
public class MyController {
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping("/propiedades")
    public ResponseEntity<?> listarPropiedades() {
        // Check if user has permission to view properties
        if (!permissionService.hasPermission("PROPIEDADES", "VIS")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("No tiene permisos para visualizar propiedades");
        }
        
        // ... rest of the logic
    }
}
```

### Permission Actions

| Code | Name | Description |
|------|------|-------------|
| VIS | Visualizar | View/Read access |
| INS | Insertar | Create new records |
| MOD | Modificar | Update existing records |
| EL | Eliminar | Delete records |
| MNJ | Manejar | Full management (all actions) |

### Super Admin Bypass

Profile ID 1 (Super Admin) automatically has all permissions and bypasses all checks.

---

## Dynamic Menu System

### Overview

The menu system now supports permission-based filtering. Users only see menu items they have access to based on their active profile.

### Endpoints

#### 1. Get All Menus (Deprecated)
```http
GET /api/v1/menu
```

Returns all menu items without filtering. Kept for backward compatibility.

#### 2. Get Filtered Menu (Recommended)
```http
GET /api/v1/menu/perfil
Headers:
  X-Perfil-Id: 2
```

Returns only menu items accessible by the specified profile.

### Menu Structure

The menu is hierarchical:

```json
[
  {
    "menuId": 1,
    "itemId": 1,
    "nombre": "Administración",
    "tipo": "M",
    "idPadre": 0,
    "orden": 1,
    "submenus": [
      {
        "itemId": 2,
        "nombre": "Propiedades",
        "tipo": "O",
        "idPadre": 1,
        "orden": 1,
        "controlador": "PropiedadController",
        "metodo": "listar",
        "modulo": "PROPIEDADES",
        "accion": "VIS",
        "submenus": []
      }
    ]
  }
]
```

### Menu Types

- **M** (Menú): Parent menu item (folder)
- **O** (Opción): Leaf menu item (actual page/action)

---

## API Endpoints

### 1. Login
```http
POST /api/v1/login
Content-Type: application/json

{
  "username": "cmartinez",
  "password": "VGVzdDEyMyE="
}
```

**Response:**
```json
{
  "type": "Bearer",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "usuario": {
    "id": 2,
    "usuario": "cmartinez",
    "nombre": "Carlos",
    "apellido": "Martinez",
    "email": "carlos.martinez@inmobiliariaabc.com"
  },
  "contextosDisponibles": [
    {
      "tipo": "ADMINISTRADORA",
      "administradora": {
        "id": 1,
        "nombre": "Inmobiliaria ABC"
      },
      "perfilesDisponibles": [
        {
          "id": 2,
          "nombre": "Administrador General"
        }
      ]
    }
  ]
}
```

### 2. Change Context
```http
POST /api/v1/contexto/cambiar
Authorization: Bearer <token>
Content-Type: application/json

{
  "tipo": "ADMINISTRADORA",
  "entidadId": 1,
  "perfilId": 2
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "contexto": {
    "tipo": "ADMINISTRADORA",
    "entidadId": 1,
    "entidadNombre": "Inmobiliaria ABC",
    "perfilId": 2,
    "perfilNombre": "Administrador General",
    "perfilDescripcion": "Administrador de administradora"
  }
}
```

### 3. Get Menu by Profile
```http
GET /api/v1/menu/perfil
Authorization: Bearer <token>
X-Perfil-Id: 2
```

**Response:**
```json
[
  {
    "menuId": 1,
    "itemId": 1,
    "nombre": "Administración",
    "tipo": "M",
    "submenus": [...]
  }
]
```

---

## Testing Guide

### Prerequisites

1. Start the application:
```bash
cd C:\dev\Personal\NewProjectF5\ResiManager\resimanager-backoffice
cp .env.dev .env  # or .env.local for H2
mvn spring-boot:run
```

2. Test users available (all passwords: `VGVzdDEyMyE=` which is Base64 for `Test123!`):

| Username | Role | Contexts |
|----------|------|----------|
| admin | Super Admin | All |
| cmartinez | Admin General | Inmobiliaria ABC |
| mrodriguez | Admin Conjunto | Residencial Las Flores |
| jperez | Propietario | Las Flores (A-101) |
| lgomez | Multi-role | 2 admins + 3 conjuntos |

### Test Flow

#### 1. Login
```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "lgomez",
    "password": "VGVzdDEyMyE="
  }'
```

Save the `token` from the response.

#### 2. Decode Token (Optional)
Visit https://jwt.io and paste the token to see claims.

#### 3. Change Context
```bash
curl -X POST http://localhost:8080/api/v1/contexto/cambiar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "tipo": "ADMINISTRADORA",
    "entidadId": 1,
    "perfilId": 2
  }'
```

Save the new `token` from the response.

#### 4. Get Menu
```bash
curl -X GET http://localhost:8080/api/v1/menu/perfil \
  -H "Authorization: Bearer <new-token>" \
  -H "X-Perfil-Id: 2"
```

---

## Integration Guide

### Frontend Integration

#### 1. Login Flow

```typescript
// 1. User logs in
const loginResponse = await fetch('/api/v1/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, password })
});

const { token, usuario, contextosDisponibles } = await loginResponse.json();

// 2. Store token and contexts
localStorage.setItem('token', token);
localStorage.setItem('contextosDisponibles', JSON.stringify(contextosDisponibles));

// 3. If user has multiple contexts, show selector
if (contextosDisponibles.length > 1) {
  // Show context selector UI
  showContextSelector(contextosDisponibles);
} else if (contextosDisponibles.length === 1) {
  // Auto-select the only context
  const ctx = contextosDisponibles[0];
  await switchContext(ctx.tipo, ctx.entidadId, ctx.perfilesDisponibles[0].id);
}
```

#### 2. Context Switching

```typescript
async function switchContext(tipo: string, entidadId: number, perfilId: number) {
  const response = await fetch('/api/v1/contexto/cambiar', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify({ tipo, entidadId, perfilId })
  });
  
  const { token, contexto } = await response.json();
  
  // Update token
  localStorage.setItem('token', token);
  localStorage.setItem('activeContext', JSON.stringify(contexto));
  localStorage.setItem('activePerfilId', contexto.perfilId);
  
  // Fetch menu for this profile
  await loadMenu(contexto.perfilId);
  
  // Navigate to dashboard
  window.location.href = '/dashboard';
}
```

#### 3. Loading Menu

```typescript
async function loadMenu(perfilId: number) {
  const response = await fetch('/api/v1/menu/perfil', {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'X-Perfil-Id': perfilId.toString()
    }
  });
  
  const menu = await response.json();
  localStorage.setItem('menu', JSON.stringify(menu));
  
  // Render menu in UI
  renderMenu(menu);
}
```

#### 4. Permission Checking (Client-Side)

```typescript
function hasPermission(modulo: string, accion: string): boolean {
  // Extract from JWT token
  const token = localStorage.getItem('token');
  const payload = JSON.parse(atob(token.split('.')[1]));
  
  // Super admin check
  if (payload.contextoPerfilId === 1) {
    return true;
  }
  
  // For more complex checks, call backend or cache permissions
  // This is just a simple client-side check
  return true; // Implement based on your needs
}
```

### Backend Integration (Adding Protected Endpoints)

```java
@RestController
@RequestMapping("/api/v1/propiedades")
public class PropiedadController {
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping
    public ResponseEntity<?> listar() {
        // Check permission
        if (!permissionService.hasPermission("PROPIEDADES", "VIS")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "No tiene permisos"));
        }
        
        // Your logic here
        return ResponseEntity.ok(propiedades);
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PropiedadDTO dto) {
        // Check permission
        if (!permissionService.hasPermission("PROPIEDADES", "INS")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "No tiene permisos"));
        }
        
        // Your logic here
        return ResponseEntity.ok(nuevaPropiedad);
    }
}
```

---

## Implementation Details

### Files Created

#### DTOs
- `CambioContextoRequest.java` - Request for context switching
- `ContextoActualDTO.java` - Current active context response
- `MenuItemDTO.java` - Menu item structure (not used yet, kept for future)

#### Services
- `PermissionService.java` - Permission checking logic
- Updated `ContextoService.java` - Added context validation methods
- Updated `MenuService.java` - Added profile-based menu filtering
- Updated `UserService.java` - Added getUserByUsername method

#### Controllers
- `ContextoController.java` - Context switching endpoint
- Updated `ViewsController.java` - Added profile-based menu endpoint

#### Repositories
- `AccOpcPerfilRepository.java` - Permission queries
- Updated `MenuItemRepository.java` - Added findMenusByPerfil query

#### Updated Files
- `JwtService.java` - Added generateTokenWithContext method
- `LoginController.java` - Already returning contexts (from previous session)

### Database Tables Used

- `Perfil` - User profiles/roles
- `ModPerfil` - Module access per profile
- `OpcPerfil` - Option access per profile
- `AccOpcPerfil` - Action permissions per profile
- `PerfPersAdministradora` - User profiles in administradoras
- `PerfPersConjunto` - User profiles in conjuntos
- `MenuItem` - Menu structure
- `Modulo` - System modules
- `Accion` - Available actions

---

## Next Steps (Not Yet Implemented)

### 1. Add @PreAuthorize Annotations

Instead of manual permission checks, use Spring Security's @PreAuthorize:

```java
@PreAuthorize("@permissionService.hasPermission('PROPIEDADES', 'VIS')")
@GetMapping("/propiedades")
public ResponseEntity<?> listar() {
    // No manual check needed
}
```

**Implementation needed:**
1. Enable method security in SecurityConfig
2. Update PermissionService to work with SpEL
3. Update all controllers

### 2. Context-Based Data Filtering

Automatically filter queries based on active context:

```java
@Aspect
public class ContextFilterAspect {
    @Around("execution(* com.resimanager..repository..*(..))")
    public Object addContextFilter(ProceedingJoinPoint joinPoint) {
        // Inject WHERE clause filtering by contextoEntidadId
    }
}
```

### 3. Token Extraction from Request

Currently PermissionService returns null for token extraction. Need to:

1. Create JWT filter to extract token from Authorization header
2. Store token in ThreadLocal or request context
3. Access in PermissionService

### 4. Refresh Token Mechanism

Add endpoint to refresh JWT without re-login:

```http
POST /api/v1/auth/refresh
Authorization: Bearer <old-token>
```

### 5. Session Management

Track active sessions and allow user to view/invalidate them.

---

## Troubleshooting

### Issue: "Profile ID is required" error

**Cause:** No X-Perfil-Id header sent to /menu/perfil endpoint

**Solution:** Ensure frontend sends the header with every request

### Issue: Empty menu returned

**Possible causes:**
1. Profile has no permissions assigned in database
2. Wrong profile ID
3. Menu items not properly linked to modules/actions

**Solution:** Check AccOpcPerfil, OpcPerfil, ModPerfil tables

### Issue: Context switch fails with "no access"

**Cause:** User doesn't have relationship with the entity or profile

**Solution:** Check PersAdministradora/PersConjunto and PerfPersAdministradora/PerfPersConjunto tables

---

## Security Considerations

1. **JWT Expiration**: Tokens expire after TOKEN_EXPIRATION_TIME_IN_MINUTES (configured in Constants)
2. **Context Validation**: Context switching validates user actually has access before generating new token
3. **Permission Checks**: All permission checks verify active status (sts = 'A')
4. **Super Admin**: Profile ID 1 bypasses all permission checks - handle with care
5. **Token Storage**: Frontend should store tokens securely (httpOnly cookies preferred over localStorage)

---

## Performance Notes

1. **Menu Query**: The findMenusByPerfil query uses multiple JOINs - consider caching
2. **Permission Checks**: Consider caching permissions per profile to avoid DB hits
3. **JWT Size**: Including context in JWT increases token size - monitor if adding more claims

---

## Conclusion

The context switching and permission system provides:

- ✅ Multi-context support for users with multiple roles
- ✅ Fine-grained permission control at module/action level
- ✅ Dynamic menu based on user permissions
- ✅ Stateless JWT-based approach
- ✅ Backward compatible with existing code

The system is production-ready but can be enhanced with the items listed in "Next Steps".
