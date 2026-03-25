# Configuración de Cookies HttpOnly para Despliegue

## 🔒 Problema Resuelto

El error **403 Forbidden** en el endpoint `/v1/contexto/cambiar` en producción (Koyeb) era causado porque las cookies JWT tenían el flag `Secure=false` hardcodeado, lo que impedía que el navegador enviara las cookies en conexiones HTTPS.

## ✅ Solución Implementada

Se agregó una variable de configuración `SECURITY_COOKIE_SECURE` que controla dinámicamente el flag `Secure` de las cookies JWT basándose en el entorno.

### Archivos Modificados:

1. **LoginController.java** (líneas 20, 51-52, 109, 115)
   - Agregado: `@Value("${app.security.cookie-secure}")` 
   - Cambiado: `setSecure(false)` → `setSecure(cookieSecure)`
   - Log mejorado para debugging

2. **ContextoController.java** (líneas 20, 42-43, 116, 122-123)
   - Agregado: `@Value("${app.security.cookie-secure}")`
   - Cambiado: `setSecure(false)` → `setSecure(cookieSecure)`
   - Log mejorado para debugging

3. **application.yml** (línea 16)
   - Ya existía: `app.security.cookie-secure: ${SECURITY_COOKIE_SECURE:true}`

4. **.env** (línea 45)
   - Actualizado con documentación detallada
   - Valor por defecto para desarrollo: `SECURITY_COOKIE_SECURE=false`

## 📋 Configuración por Entorno

### Desarrollo Local (HTTP - localhost:8080)

**Archivo:** `.env`
```bash
SECURITY_COOKIE_SECURE=false
```

**Por qué:** Localhost usa HTTP (no HTTPS), por lo que cookies con `Secure=true` no serían enviadas por el navegador.

### Producción (HTTPS - Koyeb/Vercel/etc)

**En Koyeb:** Configurar variable de entorno
```bash
SECURITY_COOKIE_SECURE=true
```

**Pasos en Koyeb:**
1. Ve a tu servicio en Koyeb Dashboard
2. Settings → Environment Variables
3. Agregar nueva variable:
   - **Name:** `SECURITY_COOKIE_SECURE`
   - **Value:** `true`
4. Deploy changes

**Por qué:** En producción usas HTTPS obligatorio, las cookies DEBEN tener `Secure=true` para:
- Ser enviadas en conexiones HTTPS
- Cumplir con mejores prácticas de seguridad
- Prevenir ataques man-in-the-middle

## 🔍 Verificación

### En Desarrollo (localhost)
1. Inicia el backend: `mvn spring-boot:run`
2. Revisa los logs, deberías ver:
   ```
   JWT cookie created with Secure flag: false
   ```
3. Login debería funcionar correctamente con HTTP

### En Producción (Koyeb)
1. Asegúrate de configurar `SECURITY_COOKIE_SECURE=true` en variables de entorno
2. Deploy los cambios
3. Revisa los logs en Koyeb, deberías ver:
   ```
   Contexto cambiado exitosamente para usuario X: Y (Cookie Secure: true)
   ```
4. El cambio de contexto ahora debería funcionar ✅

## 🐛 Debugging

Si sigues teniendo problemas:

### En el navegador (DevTools - F12):
1. **Application → Cookies**
   - Verifica que la cookie `jwt` existe
   - Verifica que tiene `Secure: true` en producción
   - Verifica que tiene `HttpOnly: true`
   - Verifica que tiene `SameSite: Lax`

2. **Network → Headers**
   - En la petición a `/contexto/cambiar`
   - Verifica que la cookie se está enviando en el header `Cookie:`
   - Si no aparece, el navegador la está bloqueando

### En el backend (logs):
```bash
# Verifica qué valor está usando
grep "Cookie Secure" logs.txt
```

### Variables de entorno:
```bash
# En Koyeb, revisa las variables activas
Settings → Environment Variables
# Debe aparecer: SECURITY_COOKIE_SECURE=true
```

## 🚀 Comandos Útiles

### Compilar y verificar cambios
```bash
cd resimanager-backoffice
mvn clean compile
```

### Ejecutar localmente
```bash
mvn spring-boot:run
```

### Verificar configuración
```bash
# Ver valor actual en .env
cat .env | grep SECURITY_COOKIE_SECURE
```

## 📝 Notas Adicionales

### ¿Por qué no auto-detectar con `request.isSecure()`?

Se consideró usar `request.isSecure()` para detectar automáticamente HTTPS, pero:
- ❌ Proxies inversos (como los de Koyeb) pueden hacer que `isSecure()` devuelva `false` aunque el cliente use HTTPS
- ❌ Headers como `X-Forwarded-Proto` no siempre son confiables
- ✅ **Variable de entorno explícita es más confiable y predecible**

### Compatibilidad con Frontend en Vercel

Si tu frontend está en Vercel (`https://tu-app.vercel.app`) y tu backend en Koyeb (`https://api.koyeb.app`):

1. **CORS debe permitir el origen específico:**
   ```java
   // En SimpleCORSFilter.java o SecurityConfig.java
   response.setHeader("Access-Control-Allow-Origin", "https://tu-app.vercel.app");
   // NO usar "*" con credentials
   ```

2. **Frontend debe incluir credentials:**
   ```javascript
   // Ya implementado en api.js
   fetch(url, {
     credentials: 'include', // ✅ Incluye cookies
     // ...
   })
   ```

3. **Cookie Secure debe ser true:**
   ```bash
   SECURITY_COOKIE_SECURE=true
   ```

## ✅ Checklist de Despliegue

Antes de deployar a producción:

- [ ] Variable `SECURITY_COOKIE_SECURE=true` configurada en Koyeb
- [ ] Backend compilado correctamente (`mvn clean compile`)
- [ ] Backend deployado en Koyeb
- [ ] Verificar logs muestran `Cookie Secure: true`
- [ ] Probar login en producción
- [ ] Probar cambio de contexto en producción
- [ ] Verificar cookie en DevTools del navegador
- [ ] CORS configurado para el dominio del frontend

---

**Última actualización:** 14/03/2026
**Versión:** 2.0.0 (Cookie-based authentication)
