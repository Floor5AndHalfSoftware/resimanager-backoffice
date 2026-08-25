-- Update menu_item records to point to the new Perfiles CRUD frontend routes
-- This migration updates the controller and method fields for "Perfiles de acceso" menu items
-- to correctly route to /dashboard/perfiles instead of /dashboard/ccontrolador/index

-- Migration: V2.0.10__UPDATE_MENU_PERFILES_ROUTES.sql
-- Description: Configure menu items to access the Perfiles CRUD pages
-- Date: 2026-03-14
-- NOTE: This migration was created before discovering that menu items 7, 13, 20 did not exist
--       It attempts to update non-existent records, so it will not make any changes
--       The actual menu items are created in V2.0.11__INSERT_PERFILES_MENU_ITEMS.sql

-- Update Super Admin context - Perfiles de acceso (ID 7) - RECORD DOES NOT EXIST
UPDATE menu_item
SET 
    mit_controlador = 'perfiles',
    mit_metodo = '',
    mit_fch_hor_mod = CURRENT_TIMESTAMP
WHERE mitid = 7
  AND mit_nombre = 'Perfiles de acceso';

-- Update Administradora context - Perfiles de acceso (ID 13) - RECORD DOES NOT EXIST
UPDATE menu_item
SET 
    mit_controlador = 'perfiles',
    mit_metodo = '',
    mit_fch_hor_mod = CURRENT_TIMESTAMP
WHERE mitid = 13
  AND mit_nombre = 'Perfiles de acceso';

-- Update Conjunto context - Perfiles de acceso (ID 20) - RECORD DOES NOT EXIST
UPDATE menu_item
SET 
    mit_controlador = 'perfiles',
    mit_metodo = '',
    mit_fch_hor_mod = CURRENT_TIMESTAMP
WHERE mitid = 20
  AND mit_nombre = 'Perfiles de acceso';

-- Verification query (will show no results as records don't exist)
DO $$ 
DECLARE 
    rec RECORD;
    record_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO record_count 
    FROM menu_item 
    WHERE mit_nombre = 'Perfiles de acceso';
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Perfiles de acceso menu items found: %', record_count;
    RAISE NOTICE '========================================';
    
    IF record_count = 0 THEN
        RAISE NOTICE 'No records were updated. Menu items 7, 13, 20 do not exist in database.';
        RAISE NOTICE 'These items will be created in migration V2.0.11__INSERT_PERFILES_MENU_ITEMS.sql';
    END IF;
END $$;
