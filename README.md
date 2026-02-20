# ResiManager - Backoffice

Aplicación backend para la gestión de residencias y condominios.

## Tecnologías

- Java 21
- Spring Boot 3.4.1
- Maven
- Spring Security
- PostgreSQL / H2
- Flyway (Migraciones)
- Swagger/OpenAPI

## Configuración

### Variables de Entorno

El proyecto usa un único archivo `application.yml` que lee variables de entorno desde un archivo `.env`.

1. **Copia el archivo de ejemplo:**
   ```bash
   cp .env.example .env
   ```

2. **Configura tus variables de entorno en `.env`:**
   ```properties
   # Perfil de Spring (dev, local, test, prod)
   SPRING_PROFILES_ACTIVE=dev
   
   # Configuración de base de datos
   DB_HOST=localhost:5432
   DB_NAME=condominio
   DB_USERNAME=postgres
   DB_PASSWORD=tu_password
   DB_PARAMS=
   ```

3. **Variables disponibles:**
   - `SPRING_PROFILES_ACTIVE`: Perfil de Spring a usar
   - `SERVER_PORT`: Puerto del servidor (default: 8080)
   - `DB_HOST`: Host y puerto de la base de datos
   - `DB_NAME`: Nombre de la base de datos
   - `DB_USERNAME`: Usuario de la base de datos
   - `DB_PASSWORD`: Password de la base de datos
   - `DB_PARAMS`: Parámetros adicionales de conexión
   - `DB_DRIVER`: Driver JDBC (default: org.postgresql.Driver)
   - `JPA_DDL_AUTO`: Modo DDL de Hibernate (default: none)
   - `JPA_SHOW_SQL`: Mostrar SQL en consola (default: true)
   - `FLYWAY_ENABLED`: Activar migraciones Flyway (default: true)

Ver `.env.example` para más detalles y ejemplos de configuración.

### Base de Datos

El proyecto incluye migraciones Flyway que crean automáticamente el esquema completo:

**Bootstrap Admin User (creado automáticamente):**
- **Username:** `admin`
- **Email:** `admin@resimanager.com`
- **Password:** `Admin123!` (SHA-256 hash)
- **ID:** 1

> ⚠️ **IMPORTANTE:** Cambia el password del usuario admin inmediatamente después del primer login.

**Esquema creado:**
- 30+ tablas incluyendo: Persona, Administradora, Conjunto, Propiedad, Perfil, etc.
- Sistema de seguridad completo (Módulos, Opciones, Acciones, Perfiles)
- Sistema de invitaciones con UUID
- 5 perfiles predefinidos
- 10 módulos del sistema
- Menú base configurado

## Construcción y Ejecución

### Requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+ (o usar H2 para testing)

### Maven

Compilar el proyecto:
```bash
mvn clean package
```

Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

La aplicación cargará automáticamente las variables del archivo `.env`.

### Ejecutar tests:
```bash
mvn test
```

### Docker

Construir la imagen Docker:
```bash
docker build -t resimanager-backoffice .
```

Ejecutar el contenedor:
```bash
docker run -p 8080:8080 --env-file .env resimanager-backoffice
```

## Documentación API

### Swagger UI

La documentación interactiva de la API está disponible en:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

La especificación OpenAPI en formato JSON:
```
http://localhost:8080/v3/api-docs
```

## Migraciones de Base de Datos

El proyecto utiliza Flyway para las migraciones. Los scripts están en:
```
src/main/resources/migrations/
```

**Migraciones disponibles:**
- `V1.0.0.x` - Migraciones antiguas (se eliminan con V2.0.0)
- `V2.0.0` - DROP de tablas antiguas
- `V2.0.1` - Tablas core (Persona, Administradora, Conjunto, etc.)
- `V2.0.2` - Tablas de seguridad (Menu, Modulo, Opcion, Accion, etc.)
- `V2.0.3` - Tablas de relaciones (many-to-many)
- `V2.0.4` - Sistema de invitaciones
- `V2.0.5` - Datos base (admin user, perfiles, módulos, etc.)

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/resimanager/backoffice/
│   │       ├── config/          # Configuración de Spring
│   │       ├── controller/      # REST Controllers
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── exception/       # Manejo de excepciones
│   │       ├── persistance/     # Entidades y Repositorios
│   │       └── service/         # Lógica de negocio
│   └── resources/
│       ├── application.yml      # Configuración unificada
│       ├── banner.txt          # Banner de inicio
│       └── migrations/         # Migraciones Flyway
└── test/
    └── java/                   # Tests unitarios
```

## Contacto

Para más información sobre el proyecto ResiManager.
