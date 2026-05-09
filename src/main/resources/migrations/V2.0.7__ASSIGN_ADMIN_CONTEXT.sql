-- Assign admin user to system administradora context
-- This migration fixes the issue where admin user has no available contexts

-- ============================================================================
-- SYSTEM ADMINISTRADORA (for Super Admin)
-- ============================================================================

INSERT INTO "Administradora" (admid, adm_doc_ident, adm_nombre, adm_telefono, adm_email, adm_pers_contacto, adm_sts, adm_usr_crea, adm_fch_hor_crea, adm_est_crea, adm_usr_mod, adm_fch_hor_mod, adm_est_mod) VALUES
(99, 'J-00000000-0', 'ResiManager - Administración del Sistema', '+00000000000', 'system@resimanager.com', 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- ASSIGN ADMIN TO SYSTEM ADMINISTRADORA
-- ============================================================================

INSERT INTO "PersAdministradora" (paid, pa_per_id, pa_adm_id, pa_sts, pa_usr_crea, pa_fch_hor_crea, pa_est_crea, pa_usr_mod, pa_fch_hor_mod, pa_est_mod) VALUES
(99, 1, 99, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- ASSIGN SUPER ADMINISTRADOR PROFILE TO SYSTEM ADMINISTRADORA
-- ============================================================================

INSERT INTO "PerfAdministradora" (pfaid, pfa_prf_id, pfa_adm_id, pfa_sts, pfa_usr_crea, pfa_fch_hor_crea, pfa_est_crea, pfa_usr_mod, pfa_fch_hor_mod, pfa_est_mod) VALUES
(99, 1, 99, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- ASSIGN ADMIN WITH SUPER ADMINISTRADOR PROFILE
-- ============================================================================

INSERT INTO "PerfPersAdministradora" (ppaid, ppa_adm_id, ppa_per_id, ppa_prf_id, ppa_sts, ppa_usr_crea, ppa_fch_hor_crea, ppa_est_crea, ppa_usr_mod, ppa_fch_hor_mod, ppa_est_mod) VALUES
(99, 99, 1, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- SUMMARY
-- ============================================================================

COMMENT ON TABLE "Administradora" IS 
'Admin user context: User admin (ID:1) now has context in System Administradora (ID:99) with Super Administrador profile (ID:1)';
