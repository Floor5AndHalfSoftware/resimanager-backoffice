# Consideraciones Técnicas

## Multimoneda

El sistema soporta operaciones en múltiples monedas.

### Tablas de Soporte

```sql
-- Monedas disponibles
INSERT INTO moneda (mon_codigo, mon_nombre, mon_simbolo) VALUES
('USD', 'Dólar Estadounidense', '$'),
('VES', 'Bolívar Venezolano', 'Bs.'),
('EUR', 'Euro', '€');

-- Tasas de cambio históricas
INSERT INTO tasa_cambio (tc_mon_origen, tc_mon_destino, tc_valor, tc_fch_vigencia)
VALUES 
(2, 1, 36.50, '2024-09-01'),  -- VES a USD
(2, 1, 37.20, '2024-09-15');  -- Actualización
```

### Uso en Operaciones

```javascript
// Convertir monto a moneda base
function convertirMoneda(monto, monedaOrigen, monedaDestino, fecha) {
  const tasa = await obtenerTasaCambio(monedaOrigen, monedaDestino, fecha);
  return monto * tasa;
}

// Obtener tasa vigente
async function obtenerTasaCambio(origen, destino, fecha = new Date()) {
  const response = await fetch(
    `/api/tasas-cambio?origen=${origen}&destino=${destino}&fecha=${fecha}`
  );
  return response.data.valor;
}
```

### Consideraciones

- Siempre usar la tasa vigente a la fecha de la operación
- Mantener histórico de tasas para reportes
- Redondear a 2 decimales para moneda, 4 para tasas

---

## Auditoría

### Campos de Auditoría por Tabla

```sql
-- Campos estándar en todas las tablas
fch_crea    TIMESTAMP    -- Fecha/hora de creación
fch_mod     TIMESTAMP    -- Fecha/hora de última modificación
usr_crea    INTEGER      -- ID del usuario que creó
usr_mod     INTEGER      -- ID del usuario que modificó
```

### Tabla de Log de Operaciones

```sql
CREATE TABLE log_operacion (
    log_id          SERIAL PRIMARY KEY,
    log_tabla       VARCHAR(50) NOT NULL,     -- Tabla afectada
    log_registro_id INTEGER NOT NULL,          -- ID del registro
    log_operacion   VARCHAR(1) NOT NULL,       -- I: Insert, U: Update, D: Delete
    log_datos_ant   JSONB,                     -- Datos anteriores
    log_datos_nuevo JSONB,                     -- Datos nuevos
    log_usr_id      INTEGER NOT NULL,          -- Usuario que realizó
    log_fch_hor     TIMESTAMP DEFAULT NOW(),
    log_ip          VARCHAR(45),               -- IP del cliente
    log_user_agent  TEXT                       -- Browser/Client info
);
```

### Implementación con Triggers

```sql
CREATE OR REPLACE FUNCTION fn_log_operacion()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO log_operacion (log_tabla, log_registro_id, log_operacion, 
                                   log_datos_nuevo, log_usr_id)
        VALUES (TG_TABLE_NAME, NEW.id, 'I', to_jsonb(NEW), 
                current_setting('app.current_user_id', true)::int);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO log_operacion (log_tabla, log_registro_id, log_operacion,
                                   log_datos_ant, log_datos_nuevo, log_usr_id)
        VALUES (TG_TABLE_NAME, NEW.id, 'U', to_jsonb(OLD), to_jsonb(NEW),
                current_setting('app.current_user_id', true)::int);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO log_operacion (log_tabla, log_registro_id, log_operacion,
                                   log_datos_ant, log_usr_id)
        VALUES (TG_TABLE_NAME, OLD.id, 'D', to_jsonb(OLD),
                current_setting('app.current_user_id', true)::int);
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Aplicar a tablas críticas
CREATE TRIGGER tr_log_persona AFTER INSERT OR UPDATE OR DELETE ON persona
FOR EACH ROW EXECUTE FUNCTION fn_log_operacion();
```

---

## Localización

### Tablas de Ubicación Geográfica

```sql
-- Países
INSERT INTO pais (pais_codigo, pais_nombre) VALUES
('VE', 'Venezuela'),
('CO', 'Colombia'),
('US', 'Estados Unidos');

-- Estados/Provincias
INSERT INTO estado (est_pais_id, est_nombre) VALUES
(1, 'Distrito Capital'),
(1, 'Miranda'),
(1, 'Carabobo');

-- Ciudades
INSERT INTO ciudad (ciud_est_id, ciud_nombre) VALUES
(1, 'Caracas'),
(2, 'Los Teques'),
(3, 'Valencia');
```

### Formatos Regionales

```javascript
// Configuración por país
const regionalConfig = {
  VE: {
    dateFormat: 'DD/MM/YYYY',
    currency: 'VES',
    currencyFormat: '#.###,##',
    phoneFormat: '+58 XXX XXX XXXX',
    documentoTipos: ['V', 'E', 'J', 'P']
  },
  US: {
    dateFormat: 'MM/DD/YYYY',
    currency: 'USD',
    currencyFormat: '#,###.##',
    phoneFormat: '+1 (XXX) XXX-XXXX',
    documentoTipos: ['SSN', 'EIN']
  }
};
```

---

## Sesiones y Seguridad

### Configuración de JWT

```yaml
# application.yml
jwt:
  secret: ${JWT_SECRET:default-secret-change-in-production}
  expiration: 86400000  # 24 horas en milisegundos
  refresh-expiration: 604800000  # 7 días
```

### Política de Contraseñas

```java
public class PasswordPolicy {
    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 64;
    public static final boolean REQUIRE_UPPERCASE = true;
    public static final boolean REQUIRE_LOWERCASE = true;
    public static final boolean REQUIRE_NUMBER = true;
    public static final boolean REQUIRE_SPECIAL = false;
}
```

### Bloqueo por Intentos Fallidos

```sql
-- Campos adicionales en persona
per_intentos_fallidos INTEGER DEFAULT 0,
per_bloqueado_hasta   TIMESTAMP,

-- Al exceder 5 intentos, bloquear por 30 minutos
UPDATE persona 
SET per_intentos_fallidos = per_intentos_fallidos + 1,
    per_bloqueado_hasta = CASE 
        WHEN per_intentos_fallidos >= 4 THEN NOW() + INTERVAL '30 minutes'
        ELSE per_bloqueado_hasta
    END
WHERE per_usuario = :usuario;
```

---

## Backup y Recuperación

### Estrategia de Backup

| Tipo | Frecuencia | Retención |
|------|------------|-----------|
| Full | Diario | 30 días |
| Incremental | Cada 6 horas | 7 días |
| Log shipping | Continuo | 24 horas |

### Scripts de Backup

```bash
#!/bin/bash
# backup-resimanager.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/resimanager"
DB_NAME="resimanager"

# Full backup
pg_dump -Fc $DB_NAME > $BACKUP_DIR/full_$DATE.dump

# Comprimir
gzip $BACKUP_DIR/full_$DATE.dump

# Subir a storage (S3, GCS, etc.)
aws s3 cp $BACKUP_DIR/full_$DATE.dump.gz s3://backups-resimanager/db/

# Limpiar backups antiguos (> 30 días)
find $BACKUP_DIR -name "*.gz" -mtime +30 -delete
```

### Restauración

```bash
# Restaurar desde backup
pg_restore -d resimanager_restore full_20240915_020000.dump.gz

# Switch a base restaurada (con downtime mínimo)
psql -c "ALTER DATABASE resimanager RENAME TO resimanager_old;"
psql -c "ALTER DATABASE resimanager_restore RENAME TO resimanager;"
```

---

## Monitoreo

### Métricas Clave

| Métrica | Alerta | Crítico |
|---------|--------|---------|
| CPU | > 70% | > 90% |
| Memoria | > 80% | > 95% |
| Disco | > 80% | > 95% |
| Conexiones DB | > 80 | > 100 |
| Tiempo respuesta API | > 1s | > 3s |
| Errores 5xx | > 10/min | > 50/min |

### Health Check Endpoints

```
GET /health
GET /health/db
GET /health/ready
GET /health/live
```

```json
{
  "status": "UP",
  "checks": [
    { "name": "database", "status": "UP", "latency": "5ms" },
    { "name": "redis", "status": "UP", "latency": "2ms" }
  ]
}
```

---

## Escalabilidad

### Estrategia de Caché

```java
// Redis para caché de sesión y datos frecuentes
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        Map<String, Duration> expirations = new HashMap<>();
        expirations.put("menu", Duration.ofMinutes(30));
        expirations.put("permisos", Duration.ofMinutes(15));
        expirations.put("usuario", Duration.ofHours(1));
        
        return RedisCacheManager.builder(factory)
            .withExpires(expirations)
            .build();
    }
}
```

### Connection Pooling

```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
```

---

## Mantenimiento

### Limpieza Programada

```sql
-- Ejecutar diariamente
-- Limpiar sesiones expiradas
DELETE FROM sesion WHERE ssn_expiracion < NOW();

-- Limpiar logs antiguos (> 90 días)
DELETE FROM log_operacion WHERE log_fch_hor < NOW() - INTERVAL '90 days';

-- Actualizar estadísticas
ANALYZE;
```

### Reindexación

```sql
-- Ejecutar semanalmente (bajo carga baja)
REINDEX TABLE persona;
REINDEX TABLE propiedad;
REINDEX TABLE log_operacion;
```

---

## Dependencias Críticas

### Frontend (package.json)

```json
{
  "dependencies": {
    "react": "^19.2.3",
    "react-dom": "^19.2.3",
    "react-router-dom": "^7.3.0",
    "@fortawesome/fontawesome-free": "^6.6.0"
  }
}
```

### Backend (pom.xml)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
</dependencies>
```
