-- Add "Gestión de Usuarios" menu item under "Usuarios" group
-- This provides access to the UsuariosPage component at /dashboard/usuarios
-- Navigation flow:
--   Menu -> Usuarios -> Gestión de Usuarios -> UsuariosPage (list all users)
--     -> Select user -> UsuarioPerfilesPage (view profiles by context)
--       -> Asignar Perfiles -> AsignarPerfilesPage (manage profile assignments)

-- Migration: V2.0.13__INSERT_GESTION_USUARIOS_MENU_ITEM.sql
-- Description: Add menu item for user management list page
-- Date: 2026-03-25

-- Insert menu item under "Usuarios" group (ID 8)
-- Uses module ID 5 (Usuarios Administradora) for user management context
INSERT INTO menu_item (
    mitid, 
    mit_menuid, 
    mit_nombre, 
    mit_tipo, 
    mit_item_padre, 
    mit_orden, 
    mit_modid, 
    mit_opcionid, 
    mit_accionid, 
    mit_controlador, 
    mit_metodo, 
    mit_sts, 
    mit_usr_crea, 
    mit_fch_hor_crea, 
    mit_est_crea, 
    mit_usr_mod, 
    mit_fch_hor_mod, 
    mit_est_mod
) VALUES (
    11,                      -- mitid: Next available ID (10 is taken by "Perfiles de acceso" under Configuracion)
    1,                       -- mit_menuid: Menu Principal
    'Gestión de Usuarios',   -- mit_nombre: Display name
    'O',                     -- mit_tipo: O = Option (vs A = Accordion/Group)
    8,                       -- mit_item_padre: Parent is "Usuarios" group (ID 8)
    2,                       -- mit_orden: Order 2 (after "Invitaciones" which is 1)
    5,                       -- mit_modid: Module "Usuarios Administradora"
    NULL,                    -- mit_opcionid: To be defined in future CRUD
    NULL,                    -- mit_accionid: To be defined in future CRUD
    'usuarios',              -- mit_controlador: Route segment
    'list',                  -- mit_metodo: Action identifier
    'A',                     -- mit_sts: Active status
    1,                       -- mit_usr_crea: Created by SYSTEM
    CURRENT_TIMESTAMP,       -- mit_fch_hor_crea
    'SYSTEM-MIGRATION',      -- mit_est_crea
    1,                       -- mit_usr_mod
    CURRENT_TIMESTAMP,       -- mit_fch_hor_mod
    'SYSTEM-MIGRATION'       -- mit_est_mod
);

-- Update sequence to reflect new max ID
SELECT setval('menu_item_mitid_seq', 11, true);

-- Verification: Show all menu items under "Usuarios" group
DO $$
DECLARE
    rec RECORD;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Menu items under "Usuarios" group (ID=8):';
    RAISE NOTICE '========================================';
    FOR rec IN
        SELECT mitid, mit_nombre, mit_tipo, mit_orden, mit_modid, mit_controlador, mit_metodo
        FROM menu_item
        WHERE mit_item_padre = 8
        ORDER BY mit_orden
    LOOP
        RAISE NOTICE 'ID: %, Nombre: "%" (Tipo: %), Orden: %, Módulo: %, Ruta: %/%',
            rec.mitid, 
            rec.mit_nombre, 
            rec.mit_tipo,
            rec.mit_orden, 
            rec.mit_modid,
            COALESCE(rec.mit_controlador, 'NULL'),
            COALESCE(rec.mit_metodo, 'NULL');
    END LOOP;
    
    RAISE NOTICE '';
    RAISE NOTICE 'Expected result:';
    RAISE NOTICE '  - ID 9: "Invitaciones" (Orden: 1)';
    RAISE NOTICE '  - ID 11: "Gestión de Usuarios" (Orden: 2)';
    RAISE NOTICE '========================================';
END $$;
