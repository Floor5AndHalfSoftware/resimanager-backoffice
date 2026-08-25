-- Fix duplicate "Perfiles de acceso" menu items under "Usuarios" group
-- Items 11 and 12 are redundant: they both point to /dashboard/perfiles (same as item 10
-- under "Configuracion"), and the user-perfiles assignment pages require a dynamic :id
-- parameter in the URL so they cannot be static menu links.
-- Access to user perfiles management is done via navigation:
--   Administradoras -> select one -> Usuarios -> select user -> Asignar Perfiles
--   Conjuntos       -> select one -> Usuarios -> select user -> Asignar Perfiles

-- Migration: V2.0.12__FIX_DUPLICATE_PERFILES_MENU_ITEMS.sql
-- Description: Remove duplicate "Perfiles de acceso" items from "Usuarios" group
-- Date: 2026-03-25

DELETE FROM menu_item WHERE mitid IN (11, 12);

-- Reset sequence to reflect current max id
SELECT setval('menu_item_mitid_seq', (SELECT MAX(mitid) FROM menu_item), true);

-- Verification
DO $$
DECLARE
    rec RECORD;
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Menu items under "Usuarios" group (padre=8):';
    RAISE NOTICE '========================================';
    FOR rec IN
        SELECT mitid, mit_nombre, mit_controlador, mit_metodo, mit_orden
        FROM menu_item
        WHERE mit_item_padre = 8
        ORDER BY mit_orden
    LOOP
        RAISE NOTICE 'ID: %, Nombre: %, Ruta: %/%', rec.mitid, rec.mit_nombre, rec.mit_controlador, rec.mit_metodo;
    END LOOP;
    RAISE NOTICE '========================================';
END $$;
