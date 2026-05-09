# 🔐 Credenciales para Testing

## Tabla Rápida de Usuarios

| Usuario | Contraseña | Rol Principal | Contexto(s) | Estado |
|---------|-----------|---------------|-------------|--------|
| `admin` | `Admin2024!` | Super Administrador | ResiManager Sistema (99) | ✅ Configurado |
| `cmartinez` | `Carlos2024!` | Administrador General | Inmobiliaria ABC (1) | ✅ Configurado |
| `mrodriguez` | `Maria2024!` | Admin de Conjunto | Residencial Las Flores (1) | ✅ Configurado |
| `jperez` | `Juan2024!` | Propietario | Residencial Las Flores (1) - A-101 | ✅ Configurado |
| `agarcia` | `Ana2024!` | Residente | Residencial Las Flores (1) - A-102 | ✅ Configurado |
| `lgomez` | `Luis2024!` | Multi-Contexto | 2 Administradoras + 2 Conjuntos | ✅ Configurado |

---

## 🧪 Casos de Prueba Recomendados

### ✅ TEST 1: Login Simple (Un Contexto)
```
Usuario: cmartinez
Password: Carlos2024!
Resultado Esperado: 
  → Login exitoso
  → Auto-selección de contexto único
  → Redirige a Dashboard
  → Menú muestra opciones de "Administrador General"
```

### ✅ TEST 2: Multi-Contexto (Selector)
```
Usuario: lgomez
Password: Luis2024!
Resultado Esperado:
  → Login exitoso
  → Muestra ContextSelector con 4 opciones:
    1. Inmobiliaria ABC - Administrador General
    2. Administradora XYZ - Administrador General
    3. Torre Mayor - Administrador de Conjunto
    4. Plaza Norte - Administrador de Conjunto
  → Usuario selecciona uno
  → Redirige a Dashboard
  → Menú filtrado según perfil seleccionado
```

### ✅ TEST 3: Propietario
```
Usuario: jperez
Password: Juan2024!
Resultado Esperado:
  → Login exitoso
  → Auto-selección (1 contexto)
  → Dashboard de Propietario
  → Menú limitado a opciones de propietario
```

### ✅ TEST 4: Super Admin
```
Usuario: admin
Password: Admin2024!
Resultado Esperado:
  → Login exitoso
  → Auto-selección (contexto sistema)
  → Dashboard completo
  → Menú con TODAS las opciones del sistema
```

---

## 📊 Estructura de Contextos por Usuario

### admin
```
└─ ADMINISTRADORA: ResiManager - Administradora del Sistema (99)
   └─ Perfil: Super Administrador (1)
```

### cmartinez
```
└─ ADMINISTRADORA: Inmobiliaria ABC (1)
   └─ Perfil: Administrador General (2)
```

### mrodriguez
```
└─ CONJUNTO: Residencial Las Flores (1)
   └─ Perfil: Administrador de Conjunto (3)
```

### jperez
```
└─ CONJUNTO: Residencial Las Flores (1)
   └─ Perfil: Propietario (4)
   └─ Propiedad: A-101
```

### agarcia
```
└─ CONJUNTO: Residencial Las Flores (1)
   └─ Perfil: Residente (5)
   └─ Propiedad: A-102 (arrendataria)
```

### lgomez (CASO COMPLEJO)
```
├─ ADMINISTRADORA: Inmobiliaria ABC (1)
│  └─ Perfil: Administrador General (2)
│
├─ ADMINISTRADORA: Administradora XYZ (2)
│  └─ Perfil: Administrador General (2)
│
├─ CONJUNTO: Edificio Torre Mayor (2)
│  ├─ Perfil: Administrador de Conjunto (3)
│  ├─ Perfil: Propietario (4)
│  └─ Propiedad: PH-01 (Penthouse)
│
└─ CONJUNTO: Centro Comercial Plaza Norte (3)
   └─ Perfil: Administrador de Conjunto (3)
```

---

## 🔑 Endpoints a Probar

### 1. Login
```bash
POST http://localhost:8080/v1/login
Content-Type: application/json

{
  "username": "cmartinez",
  "password": "Q2FybG9zMjAyNCE="  # Base64("Carlos2024!")
}
```

### 2. Cambiar Contexto
```bash
POST http://localhost:8080/v1/contexto/cambiar
Authorization: Bearer <token-from-login>
Content-Type: application/json

{
  "tipo": "ADMINISTRADORA",
  "entidadId": 1,
  "perfilId": 2
}
```

### 3. Obtener Menú
```bash
GET http://localhost:8080/v1/menu/perfil
Authorization: Bearer <token-from-context>
X-Perfil-Id: 2
```

---

## 📝 Notas Importantes

1. **Contraseñas en Base64**: El endpoint de login espera la contraseña en Base64
2. **Tokens JWT**: Cada cambio de contexto genera un nuevo token con claims actualizados
3. **Header X-Perfil-Id**: Requerido para obtener el menú filtrado
4. **Auto-selección**: Si `contextosDisponibles.length === 1`, el frontend auto-selecciona
5. **Migración V2.0.6**: Toda la data de prueba está en esta migración
6. **Migración V2.0.7**: Asigna contexto al usuario admin

---

## ✨ Frontend - URLs de Prueba

```
Login: http://localhost:5000/
Dashboard: http://localhost:5000/dashboard
```

**Estado:** ✅ Listo para pruebas E2E completas
