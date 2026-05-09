# ResiManager - Database Migrations & User Credentials

## Migration Files Order

### Phase 1: Old System Cleanup
- `V1.0.0.0__CREATE_GENERAL_TABLES.sql` - Legacy tables (deprecated)
- `V1.0.0.1__CREATE_TABLE_USERS.sql` - Legacy user tables (deprecated)
- `V1.0.0.2__INSERT_DATA_IN_TABLE_USERS.sql` - Legacy data (deprecated)
- `V1.0.0.3__INSERT_DATA_IN_OWNERS_TABLE.sql` - Legacy data (deprecated)
- `V2.0.0__DROP_OLD_TABLES.sql` - Cleanup of legacy tables

### Phase 2: New Schema Structure
- `V2.0.1__CREATE_CORE_TABLES.sql` - Core business tables (Administradora, Conjunto, Persona, etc.)
- `V2.0.2__CREATE_SECURITY_TABLES.sql` - Security and permissions (Perfil, Modulo, Menu, Accion)
- `V2.0.3__CREATE_RELATIONSHIP_TABLES.sql` - Many-to-many relationships
- `V2.0.4__CREATE_INVITATION_TABLES.sql` - Invitation system tables

### Phase 3: Seed Data
- `V2.0.5__INSERT_BASE_DATA.sql` - Bootstrap data (admin user, base profiles, modules)
- `V2.0.6__INSERT_TEST_DATA.sql` - Test users and complete context structure

## User Credentials

All passwords use BCrypt hashing with strength 10.

### Production User
| Username | Password | Role | Email | Notes |
|----------|----------|------|-------|-------|
| `admin` | `Admin2024!` | Super Administrador | admin@resimanager.com | Bootstrap super admin |

### Test Users
| Username | Password | Role | Email | Context |
|----------|----------|------|-------|---------|
| `cmartinez` | `Carlos2024!` | Administrador General | carlos.martinez@inmobiliariaabc.com | Works at Inmobiliaria ABC |
| `mrodriguez` | `Maria2024!` | Administrador de Conjunto | maria.rodriguez@lasflores.com | Manages Residencial Las Flores |
| `jperez` | `Juan2024!` | Propietario | juan.perez@email.com | Owner in Las Flores |
| `agarcia` | `Ana2024!` | Residente | ana.garcia@email.com | Resident in Las Flores |
| `lgomez` | `Luis2024!` | Multi-role | luis.gomez@email.com | Admin General + Admin Conjunto + Propietario |

### Login Format

Passwords must be sent Base64 encoded in the API:

```json
{
  "username": "admin",
  "password": "QWRtaW4yMDI0IQ=="
}
```

Base64 encoding examples:
- `Admin2024!` → `QWRtaW4yMDI0IQ==`
- `Carlos2024!` → `Q2FybG9zMjAyNCE=`
- `Maria2024!` → `TWFyaWEyMDI0IQ==`
- `Juan2024!` → `SnVhbjIwMjQh`
- `Ana2024!` → `QW5hMjAyNCE=`
- `Luis2024!` → `THVpczIwMjQh`

## Migration Execution

To reset and rebuild the database:

1. **Drop all tables and schema history:**
   ```sql
   DROP SCHEMA public CASCADE;
   CREATE SCHEMA public;
   ```

2. **Restart application** - Flyway will automatically:
   - Execute all migrations in version order
   - Create schema history table
   - Mark all migrations as applied

## Security Notes

- All passwords are BCrypt hashed (strength 10)
- Password transmission uses Base64 encoding
- JWT tokens expire after 24 hours
- Failed login attempts are cached and limited

## Generated Date

Generated: 2026-02-20
