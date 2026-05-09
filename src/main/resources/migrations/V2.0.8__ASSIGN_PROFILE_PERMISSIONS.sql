-- Assign module permissions to profiles based on hierarchy levels
-- Super Admin (1) already has all permissions from V2.0.5
-- Only insert if not already present to avoid duplicates

-- ============================================================================
-- PERFIL 2: Administrador General (Administradora Level)
-- Access to Level 1 and Level 2 modules
-- ============================================================================

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 2, 3, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 2 AND mp_mod_id = 3);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 2, 4, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 2 AND mp_mod_id = 4);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 2, 5, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 2 AND mp_mod_id = 5);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 2, 6, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 2 AND mp_mod_id = 6);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 2, 7, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 2 AND mp_mod_id = 7);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 2, 8, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 2 AND mp_mod_id = 8);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 2, 9, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 2 AND mp_mod_id = 9);

-- ============================================================================
-- PERFIL 3: Administrador de Conjunto (Conjunto Level)
-- Access to Level 2 modules only
-- ============================================================================

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 3, 6, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 3 AND mp_mod_id = 6);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 3, 7, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 3 AND mp_mod_id = 7);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 3, 8, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 3 AND mp_mod_id = 8);

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 3, 9, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 3 AND mp_mod_id = 9);

-- ============================================================================
-- PERFIL 4: Propietario (Owner Level)
-- Access to Level 3 modules (own property only)
-- ============================================================================

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 4, 10, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 4 AND mp_mod_id = 10);

-- ============================================================================
-- PERFIL 5: Residente (Resident Level)
-- Access to Level 3 modules (read-only)
-- ============================================================================

INSERT INTO "ModPerfil" (mp_prf_id, mp_mod_id, mp_sts, mp_usr_crea, mp_fch_hor_crea, mp_est_crea, mp_usr_mod, mp_fch_hor_mod, mp_est_mod)
SELECT 5, 10, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'
WHERE NOT EXISTS (SELECT 1 FROM "ModPerfil" WHERE mp_prf_id = 5 AND mp_mod_id = 10);
