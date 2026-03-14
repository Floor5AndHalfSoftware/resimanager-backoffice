-- Insert Perfiles de acceso menu items for all contexts
-- This migration adds the menu items and assigns them to appropriate modules

-- Migration: V2.0.11__INSERT_PERFILES_MENU_ITEMS.sql
-- Description: Create menu items for Perfiles de acceso CRUD
-- Date: 2026-03-14

-- Insert menu item for Super Admin context (under Configuracion group, ID 2)
-- Uses module ID 1 (Configuracion Sistema)
INSERT INTO menu_item (
    mitid, mit_menuid, mit_nombre, mit_tipo, mit_item_padre, mit_orden, 
    mit_modid, mit_opcionid, mit_accionid, mit_controlador, mit_metodo, mit_sts,
    mit_usr_crea, mit_fch_hor_crea, mit_est_crea, mit_usr_mod, mit_fch_hor_mod, mit_est_mod
) VALUES (
    10, 1, 'Perfiles de acceso', 'O', 2, 2, 
    1, NULL, NULL, 'perfiles', '', 'A',
    1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
);

-- Insert menu item for Administradora context (under Usuarios group, ID 8)
-- Uses module ID 5 (Usuarios Administradora)
INSERT INTO menu_item (
    mitid, mit_menuid, mit_nombre, mit_tipo, mit_item_padre, mit_orden, 
    mit_modid, mit_opcionid, mit_accionid, mit_controlador, mit_metodo, mit_sts,
    mit_usr_crea, mit_fch_hor_crea, mit_est_crea, mit_usr_mod, mit_fch_hor_mod, mit_est_mod
) VALUES (
    11, 1, 'Perfiles de acceso', 'O', 8, 2, 
    5, NULL, NULL, 'perfiles', '', 'A',
    1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
);

-- Insert menu item for Conjunto context (under Usuarios group, ID 8)
-- Uses module ID 8 (Usuarios Conjunto)
INSERT INTO menu_item (
    mitid, mit_menuid, mit_nombre, mit_tipo, mit_item_padre, mit_orden, 
    mit_modid, mit_opcionid, mit_accionid, mit_controlador, mit_metodo, mit_sts,
    mit_usr_crea, mit_fch_hor_crea, mit_est_crea, mit_usr_mod, mit_fch_hor_mod, mit_est_mod
) VALUES (
    12, 1, 'Perfiles de acceso', 'O', 8, 3, 
    8, NULL, NULL, 'perfiles', '', 'A',
    1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
);

-- Update the menu_item sequence to continue from 13
SELECT setval('menu_item_mitid_seq', 12, true);

-- Verification query
DO $$ 
DECLARE 
    rec RECORD;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Inserted Perfiles de acceso menu items:';
    RAISE NOTICE '========================================';
    FOR rec IN 
        SELECT 
            mitid as id,
            mit_nombre as nombre,
            mit_modid as modulo_id,
            m.mod_nombre as modulo_nombre,
            mit_controlador as controlador,
            mit_metodo as metodo,
            mit_item_padre as padre_id,
            mit_orden as orden,
            mit_sts as estado
        FROM menu_item
        LEFT JOIN modulo m ON menu_item.mit_modid = m.modid
        WHERE mit_nombre = 'Perfiles de acceso'
        ORDER BY mitid
    LOOP
        RAISE NOTICE 'ID: %, Nombre: %, Modulo: % (ID: %), Padre: %, Orden: %, Controlador: %, Estado: %', 
            rec.id, rec.nombre, rec.modulo_nombre, rec.modulo_id, rec.padre_id, rec.orden, rec.controlador, rec.estado;
    END LOOP;
    RAISE NOTICE '========================================';
END $$;
