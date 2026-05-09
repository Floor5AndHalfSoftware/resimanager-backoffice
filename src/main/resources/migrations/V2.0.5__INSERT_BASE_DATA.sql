-- Initial seed data for ResiManager
-- Bootstrap admin user and base security configuration

-- Bootstrap Super Admin User (ID=1)
-- Password: Admin2024! (BCrypt hashed - change after first login)
INSERT INTO "Persona" (
    perid, per_doc_ident, per_nombre, per_apellido, per_tlf_cel, per_email, 
    per_usuario, per_clave, per_sts,
    per_usr_crea, per_fch_hor_crea, per_est_crea,
    per_usr_mod, per_fch_hor_mod, per_est_mod
) VALUES (
    1, 'ADMIN-001', 'Super', 'Admin', '+00000000000', 'admin@resimanager.com',
    'admin', '$2a$10$lnWHy/y0eizPyw.Ius/s5eB8JcYSjs9hXjvsnxVHuTTZviUr6f1HS', 'A',
    1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP',
    1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
);

-- Base Actions (MNJ, INS, MOD, VIS, EL)
INSERT INTO accion (accid, acc_nombre, acc_descrip, acc_sts, acc_usr_crea, acc_fch_hor_crea, acc_est_crea, acc_usr_mod, acc_fch_hor_mod, acc_est_mod) VALUES
(1, 'MNJ', 'Manejar - Acceso total al modulo/opcion', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 'INS', 'Insertar - Crear nuevos registros', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 'MOD', 'Modificar - Editar registros existentes', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(4, 'VIS', 'Visualizar - Solo lectura', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 'EL', 'Eliminar - Borrar registros', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Base Profiles
INSERT INTO "Perfil" (prfid, prf_nombre, prf_descrip, prf_sts, prf_usr_crea, prf_fch_hor_crea, prf_est_crea, prf_usr_mod, prf_fch_hor_mod, prf_est_mod) VALUES
(1, 'Super Administrador', 'Acceso total al sistema', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 'Administrador General', 'Administrador de administradora', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 'Administrador de Conjunto', 'Administrador de conjunto residencial', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(4, 'Propietario', 'Propietario de unidad residencial', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 'Residente', 'Residente sin propiedad', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Base Modules (organized by hierarchy level)
INSERT INTO modulo (modid, mod_nombre, mod_descrip, mod_nivel, mod_sts, mod_usr_crea, mod_fch_hor_crea, mod_est_crea, mod_usr_mod, mod_fch_hor_mod, mod_est_mod) VALUES
-- Level 0: Super Admin
(1, 'Configuracion Sistema', 'Configuracion general del sistema', 0, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 'Gestion Administradoras', 'Administracion de empresas administradoras', 0, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Level 1: Administradora
(3, 'Gestion Conjuntos', 'Administracion de conjuntos residenciales', 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(4, 'Gestion Contratos', 'Administracion de contratos', 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 'Usuarios Administradora', 'Gestion de usuarios de la administradora', 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Level 2: Conjunto
(6, 'Gestion Propiedades', 'Administracion de propiedades y unidades', 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(7, 'Gestion Propietarios', 'Administracion de propietarios', 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(8, 'Usuarios Conjunto', 'Gestion de usuarios del conjunto', 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(9, 'Invitaciones', 'Sistema de invitaciones a usuarios', 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Level 3: Propietario
(10, 'Mi Propiedad', 'Informacion de mi propiedad', 3, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Base Menu (Main Left Sidebar)
INSERT INTO "Menu" (menuid, menu_nombre, menu_descrip, menu_posicion, menu_sts, menu_usr_crea, menu_fch_hor_crea, menu_est_crea, menu_usr_mod, menu_fch_hor_mod, menu_est_mod) VALUES
(1, 'Menu Principal', 'Menu lateral izquierdo principal', 'I', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Base Menu Items (will be expanded later)
INSERT INTO menu_item (mitid, mit_menuid, mit_nombre, mit_tipo, mit_item_padre, mit_orden, mit_modid, mit_opcionid, mit_accionid, mit_controlador, mit_metodo, mit_sts, mit_usr_crea, mit_fch_hor_crea, mit_est_crea, mit_usr_mod, mit_fch_hor_mod, mit_est_mod) VALUES
-- Dashboard (no module required)
(1, 1, 'Dashboard', 'O', 0, 1, NULL, NULL, NULL, 'dashboard', 'index', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- System Configuration (Super Admin only)
(2, 1, 'Configuracion', 'A', 0, 2, NULL, NULL, NULL, NULL, NULL, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 1, 'Administradoras', 'O', 2, 1, 2, NULL, NULL, 'administradoras', 'list', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Conjunto Management
(4, 1, 'Conjuntos', 'A', 0, 3, NULL, NULL, NULL, NULL, NULL, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 1, 'Mis Conjuntos', 'O', 4, 1, 3, NULL, NULL, 'conjuntos', 'list', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(6, 1, 'Propiedades', 'O', 4, 2, 6, NULL, NULL, 'propiedades', 'list', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(7, 1, 'Propietarios', 'O', 4, 3, 7, NULL, NULL, 'propietarios', 'list', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- User Management
(8, 1, 'Usuarios', 'A', 0, 4, NULL, NULL, NULL, NULL, NULL, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(9, 1, 'Invitaciones', 'O', 8, 1, 9, NULL, NULL, 'invitaciones', 'list', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Grant Super Admin profile full access to all modules
INSERT INTO "ModPerfil" (mpid, mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 
    m.modid,
    1,  -- Super Administrador profile
    m.modid,
    'A',
    1,
    CURRENT_TIMESTAMP,
    'SYSTEM-BOOTSTRAP',
    1,
    CURRENT_TIMESTAMP,
    'SYSTEM-BOOTSTRAP'
FROM modulo m
WHERE m.mod_sts = 'A';

COMMENT ON TABLE "Persona" IS 'IMPORTANTE: Usuario bootstrap admin@resimanager.com con password Admin2024!';
