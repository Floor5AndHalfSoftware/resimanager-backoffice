-- Test data for ResiManager
-- Creates users for each role with complete context structure

-- ============================================================================
-- TEST USERS PASSWORDS (BCrypt hashed)
-- ============================================================================
-- cmartinez: Carlos2024!
-- mrodriguez: Maria2024!
-- jperez: Juan2024!
-- agarcia: Ana2024!
-- lgomez: Luis2024!

-- User 2: Administrator of "Inmobiliaria ABC" (Administrador General)
INSERT INTO "Persona" (perid, per_doc_ident, per_nombre, per_apellido, per_tlf_cel, per_email, per_usuario, per_clave, per_sts, per_usr_crea, per_fch_hor_crea, per_est_crea, per_usr_mod, per_fch_hor_mod, per_est_mod) VALUES
(2, 'V-12345678', 'Carlos', 'Martinez', '+58412-1234567', 'carlos.martinez@inmobiliariaabc.com', 'cmartinez', '$2a$10$rit0jmnq84feqlUAEHlo5.wuqeoaMITybhW737KUtxHZ0349yi7pG', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- User 3: Administrator of "Residencial Las Flores" (Administrador de Conjunto)
INSERT INTO "Persona" (perid, per_doc_ident, per_nombre, per_apellido, per_tlf_cel, per_email, per_usuario, per_clave, per_sts, per_usr_crea, per_fch_hor_crea, per_est_crea, per_usr_mod, per_fch_hor_mod, per_est_mod) VALUES
(3, 'V-23456789', 'Maria', 'Rodriguez', '+58424-2345678', 'maria.rodriguez@lasflores.com', 'mrodriguez', '$2a$10$Lt5yQz4PKN.k49gjY.VBG.QCVHmpF7W10/Y.wH.tQPizGlktMrA1W', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- User 4: Owner in "Residencial Las Flores" (Propietario)
INSERT INTO "Persona" (perid, per_doc_ident, per_nombre, per_apellido, per_tlf_cel, per_email, per_usuario, per_clave, per_sts, per_usr_crea, per_fch_hor_crea, per_est_crea, per_usr_mod, per_fch_hor_mod, per_est_mod) VALUES
(4, 'V-34567890', 'Juan', 'Perez', '+58414-3456789', 'juan.perez@email.com', 'jperez', '$2a$10$OwNDuDSL1vlpY3quqzPdk.ynlwanuN6qItEZflvAUQVHNpIxEx5Cq', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- User 5: Resident in "Residencial Las Flores" (Residente)
INSERT INTO "Persona" (perid, per_doc_ident, per_nombre, per_apellido, per_tlf_cel, per_email, per_usuario, per_clave, per_sts, per_usr_crea, per_fch_hor_crea, per_est_crea, per_usr_mod, per_fch_hor_mod, per_est_mod) VALUES
(5, 'V-45678901', 'Ana', 'Garcia', '+58426-4567890', 'ana.garcia@email.com', 'agarcia', '$2a$10$oSYZw7l8nVYfp1j2pvsh1.SHb7jSNB/ynz9RFguMMrI1n0DH0P0ga', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- User 6: Multi-role user (Admin General + Admin Conjunto + Propietario)
INSERT INTO "Persona" (perid, per_doc_ident, per_nombre, per_apellido, per_tlf_cel, per_email, per_usuario, per_clave, per_sts, per_usr_crea, per_fch_hor_crea, per_est_crea, per_usr_mod, per_fch_hor_mod, per_est_mod) VALUES
(6, 'V-56789012', 'Luis', 'Gomez', '+58412-5678901', 'luis.gomez@email.com', 'lgomez', '$2a$10$JdFruP1sFDHB/dCEZe9zv.c06YoIEftv9TKyi1yUv0U229v2mIAx2', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- TEST ADMINISTRADORAS (Property Management Companies)
-- ============================================================================

INSERT INTO "Administradora" (admid, adm_doc_ident, adm_nombre, adm_telefono, adm_email, adm_pers_contacto, adm_sts, adm_usr_crea, adm_fch_hor_crea, adm_est_crea, adm_usr_mod, adm_fch_hor_mod, adm_est_mod) VALUES
(1, 'J-12345678-9', 'Inmobiliaria ABC', '+58212-1234567', 'contacto@inmobiliariaabc.com', 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 'J-98765432-1', 'Administradora XYZ', '+58212-9876543', 'info@adminxyz.com', 6, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- TEST CONJUNTOS (Residential/Commercial Complexes)
-- ============================================================================
-- conj_origen: 'A' = Managed by Administradora, 'C' = Independent Conjunto

INSERT INTO "Conjunto" (conjid, conj_doc_ident, conj_nombre, conj_telefono, conj_email, conj_pers_contacto, conj_origen, conj_sts, conj_usr_crea, conj_fch_hor_crea, conj_est_crea, conj_usr_mod, conj_fch_hor_mod, conj_est_mod) VALUES
(1, 'J-11111111-1', 'Residencial Las Flores', '+58212-1111111', 'admin@lasflores.com', 3, 'A', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 'J-22222222-2', 'Edificio Torre Mayor', '+58212-2222222', 'admin@torremayor.com', 6, 'A', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 'J-33333333-3', 'Centro Comercial Plaza Norte', '+58212-3333333', 'admin@plazanorte.com', 6, 'A', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- RELATIONSHIPS: Persona <-> Administradora
-- ============================================================================

-- Carlos Martinez (ID:2) works at Inmobiliaria ABC (ID:1)
INSERT INTO "PersAdministradora" (paid, pa_per_id, pa_adm_id, pa_sts, pa_usr_crea, pa_fch_hor_crea, pa_est_crea, pa_usr_mod, pa_fch_hor_mod, pa_est_mod) VALUES
(1, 2, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Luis Gomez (ID:6) works at both administradoras
INSERT INTO "PersAdministradora" (paid, pa_per_id, pa_adm_id, pa_sts, pa_usr_crea, pa_fch_hor_crea, pa_est_crea, pa_usr_mod, pa_fch_hor_mod, pa_est_mod) VALUES
(2, 6, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 6, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- RELATIONSHIPS: Persona <-> Conjunto
-- ============================================================================

-- Maria Rodriguez (ID:3) manages Residencial Las Flores (ID:1)
INSERT INTO "PersConjunto" (pcid, pc_per_id, pc_conj_id, pc_sts, pc_usr_crea, pc_fch_hor_crea, pc_est_crea, pc_usr_mod, pc_fch_hor_mod, pc_est_mod) VALUES
(1, 3, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Juan Perez (ID:4) lives in Residencial Las Flores (ID:1)
INSERT INTO "PersConjunto" (pcid, pc_per_id, pc_conj_id, pc_sts, pc_usr_crea, pc_fch_hor_crea, pc_est_crea, pc_usr_mod, pc_fch_hor_mod, pc_est_mod) VALUES
(2, 4, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Ana Garcia (ID:5) lives in Residencial Las Flores (ID:1)
INSERT INTO "PersConjunto" (pcid, pc_per_id, pc_conj_id, pc_sts, pc_usr_crea, pc_fch_hor_crea, pc_est_crea, pc_usr_mod, pc_fch_hor_mod, pc_est_mod) VALUES
(3, 5, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- Luis Gomez (ID:6) manages multiple conjuntos
INSERT INTO "PersConjunto" (pcid, pc_per_id, pc_conj_id, pc_sts, pc_usr_crea, pc_fch_hor_crea, pc_est_crea, pc_usr_mod, pc_fch_hor_mod, pc_est_mod) VALUES
(4, 6, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 6, 3, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- CONTRACTS (Must be created before ConjAdministradora)
-- ============================================================================

INSERT INTO "Contrato" (cttid, ctt_adm_id, ctt_nombre, ctt_duracion, ct_documento, ctt_auto_renovar, ctt_resindir_antes_de, ctt_sts, ctt_usr_crea, ctt_fch_hor_crea, ctt_est_crea, ctt_usr_mod, ctt_fch_hor_mod, ctt_est_mod) VALUES
-- Contracts for administration services (2 years duration, auto-renew, 30 days notice)
(1, 1, 'Contrato Administración Las Flores', 24, 'CONT-001-2024', 'S', 30, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 2, 'Contrato Administración Torre Mayor', 24, 'CONT-002-2024', 'S', 30, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 2, 'Contrato Administración Plaza Norte', 24, 'CONT-003-2024', 'S', 30, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- RELATIONSHIPS: Conjuntos managed by Administradoras
-- ============================================================================

INSERT INTO "ConjAdministradora" (caid, ca_conj_id, ca_adm_id, ca_ctt_id, ca_fch_hor_desde, ca_fch_hor_hasta, ca_auto_renovar, ca_sts, ca_usr_crea, ca_fch_hor_crea, ca_est_crea, ca_usr_mod, ca_fch_hor_mod, ca_est_mod) VALUES
-- Inmobiliaria ABC manages Las Flores (contract started 6 months ago, ends in 18 months)
(1, 1, 1, 1, CURRENT_TIMESTAMP - INTERVAL '6 months', CURRENT_TIMESTAMP + INTERVAL '18 months', 'S', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
-- Administradora XYZ manages Torre Mayor (contract started 3 months ago, ends in 21 months)
(2, 2, 2, 2, CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP + INTERVAL '21 months', 'S', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
-- Administradora XYZ manages Plaza Norte (contract started 1 month ago, ends in 23 months)
(3, 3, 2, 3, CURRENT_TIMESTAMP - INTERVAL '1 month', CURRENT_TIMESTAMP + INTERVAL '23 months', 'S', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- PROFILES assigned to Administradoras
-- ============================================================================

INSERT INTO "PerfAdministradora" (pfaid, pfa_prf_id, pfa_adm_id, pfa_sts, pfa_usr_crea, pfa_fch_hor_crea, pfa_est_crea, pfa_usr_mod, pfa_fch_hor_mod, pfa_est_mod) VALUES
-- Inmobiliaria ABC offers "Administrador General" profile
(1, 2, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
-- Administradora XYZ offers "Administrador General" profile
(2, 2, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- PROFILES assigned to Conjuntos
-- ============================================================================

INSERT INTO "PerfConjunto" (pfcid, pfc_prf_id, pfc_conj_id, pfc_sts, pfc_usr_crea, pfc_fch_hor_crea, pfc_est_crea, pfc_usr_mod, pfc_fch_hor_mod, pfc_est_mod) VALUES
-- Las Flores offers: Admin Conjunto, Propietario, Residente
(1, 3, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 4, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 5, 1, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Torre Mayor offers: Admin Conjunto, Propietario
(4, 3, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 4, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Plaza Norte offers: Admin Conjunto
(6, 3, 3, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- USER PROFILE ASSIGNMENTS in Administradoras
-- ============================================================================

INSERT INTO "PerfPersAdministradora" (ppaid, ppa_adm_id, ppa_per_id, ppa_prf_id, ppa_sts, ppa_usr_crea, ppa_fch_hor_crea, ppa_est_crea, ppa_usr_mod, ppa_fch_hor_mod, ppa_est_mod) VALUES
-- Carlos Martinez has "Administrador General" role in Inmobiliaria ABC
(1, 1, 2, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Luis Gomez has "Administrador General" role in both administradoras
(2, 1, 6, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 2, 6, 2, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- USER PROFILE ASSIGNMENTS in Conjuntos
-- ============================================================================

INSERT INTO "PerfPersConjunto" (ppcid, ppc_conj_id, ppc_per_id, ppc_prf_id, ppc_sts, ppc_usr_crea, ppc_fch_hor_crea, ppc_est_crea, ppc_usr_mod, ppc_fch_hor_mod, ppc_est_mod) VALUES
-- Maria Rodriguez is "Administrador de Conjunto" in Las Flores
(1, 1, 3, 3, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Juan Perez is "Propietario" in Las Flores
(2, 1, 4, 4, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Ana Garcia is "Residente" in Las Flores
(3, 1, 5, 5, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Luis Gomez is "Administrador de Conjunto" in Torre Mayor and "Propietario" in Torre Mayor
(4, 2, 6, 3, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 2, 6, 4, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Luis Gomez is "Administrador de Conjunto" in Plaza Norte
(6, 3, 6, 3, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- PROPERTY CLASSES (for conjuntos)
-- ============================================================================

INSERT INTO "ClaseDePropiedad" (cdpid, cdp_conj_id, cdp_nombre, cdp_descrip, cdp_cantidad, cdp_tipo_bien, cdp_und_med, cdp_tipo_ppd, cdp_sts, cdp_usr_crea, cdp_fch_hor_crea, cdp_est_crea, cdp_usr_mod, cdp_fch_hor_mod, cdp_est_mod) VALUES
-- Las Flores property types
(1, 1, 'Apartamento', 'Apartamento residencial', 1, 'I', 'UND', 'PH', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 1, 'Casa', 'Casa unifamiliar', 1, 'I', 'UND', 'PH', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 1, 'Local Comercial', 'Local para comercio', 1, 'I', 'UND', 'PH', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Torre Mayor property types
(4, 2, 'Apartamento', 'Apartamento residencial', 1, 'I', 'UND', 'PH', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 2, 'Penthouse', 'Apartamento tipo penthouse', 1, 'I', 'UND', 'PH', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Plaza Norte property types
(6, 3, 'Local Comercial', 'Local para comercio', 1, 'I', 'UND', 'PH', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(7, 3, 'Oficina', 'Oficina administrativa', 1, 'I', 'UND', 'PH', 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- PROPERTIES
-- ============================================================================

INSERT INTO "Propiedad" (ppid, pp_conj_id, pp_cdp_id, pp_numero, pp_cantidad, pp_coef_participacion, pp_sts, pp_usr_crea, pp_fch_hor_crea, pp_est_crea, pp_usr_mod, pp_fch_hor_mod, pp_est_mod) VALUES
-- Las Flores properties
(1, 1, 1, 'A-101', 1, 0.0285, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(2, 1, 1, 'A-102', 1, 0.0285, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(3, 1, 2, 'C-01', 1, 0.04, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Torre Mayor properties
(4, 2, 4, '501', 1, 0.0316, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(5, 2, 5, 'PH-01', 1, 0.0833, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Plaza Norte properties
(6, 3, 6, 'L-101', 1, 0.0225, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),
(7, 3, 7, 'OF-201', 1, 0.03, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- PROPERTY OWNERSHIP
-- ============================================================================

INSERT INTO "ProptPropiedad" (prptid, prpt_pp_id, prpt_per_id, prpt_porc_participacion, prpt_sts, prpt_usr_crea, prpt_fch_hor_crea, prpt_est_crea, prpt_usr_mod, prpt_fch_hor_mod, prpt_est_mod) VALUES
-- Juan Perez owns A-101 in Las Flores (100%)
(1, 1, 4, 1.0000, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Luis Gomez owns PH-01 in Torre Mayor (100%)
(2, 5, 6, 1.0000, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP'),

-- Ana Garcia rents A-102 in Las Flores (tenant - represented as 0% ownership)
(3, 2, 5, 0.0000, 'A', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP', 1, CURRENT_TIMESTAMP, 'SYSTEM-BOOTSTRAP');

-- ============================================================================
-- SUMMARY COMMENT
-- ============================================================================

COMMENT ON TABLE "Persona" IS 
'TEST USERS CREDENTIALS (BCrypt hashed):
1. admin / Admin2024! - Super Administrador (all access) - Defined in V2.0.5
2. cmartinez / Carlos2024! - Administrador General at Inmobiliaria ABC
3. mrodriguez / Maria2024! - Administrador de Conjunto at Las Flores
4. jperez / Juan2024! - Propietario at Las Flores (owns A-101)
5. agarcia / Ana2024! - Residente at Las Flores (rents A-102)
6. lgomez / Luis2024! - Multi-role: Admin General (2 administradoras) + Admin Conjunto (2 conjuntos) + Propietario (Torre Mayor)';
