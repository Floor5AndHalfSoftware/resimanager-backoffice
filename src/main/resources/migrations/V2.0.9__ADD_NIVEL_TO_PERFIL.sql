-- Add nivel (hierarchical level) field to Perfil table
-- Level 0: Super Admin
-- Level 1: Administradora Admin
-- Level 2: Conjunto Admin
-- Level 3: Propietario/Residente

-- Check if column exists before adding (idempotent)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'Perfil' AND column_name = 'prf_nivel'
    ) THEN
        ALTER TABLE "Perfil" ADD COLUMN prf_nivel INTEGER NOT NULL DEFAULT 3;
    END IF;
END $$;

COMMENT ON COLUMN "Perfil".prf_nivel IS 'Nivel jerarquico del perfil: 0=Super Admin, 1=Admin General, 2=Admin Conjunto, 3=Propietario/Residente';

-- Update existing profiles with correct levels (idempotent)
UPDATE "Perfil" SET prf_nivel = 0 WHERE prfid = 1 AND prf_nivel != 0; -- Super Administrador
UPDATE "Perfil" SET prf_nivel = 1 WHERE prfid = 2 AND prf_nivel != 1; -- Administrador General
UPDATE "Perfil" SET prf_nivel = 2 WHERE prfid = 3 AND prf_nivel != 2; -- Administrador de Conjunto
UPDATE "Perfil" SET prf_nivel = 3 WHERE prfid = 4 AND prf_nivel != 3; -- Propietario
UPDATE "Perfil" SET prf_nivel = 3 WHERE prfid = 5 AND prf_nivel != 3; -- Residente

-- Create indexes for faster nivel-based queries (if not exist)
CREATE INDEX IF NOT EXISTS idx_perfil_nivel ON "Perfil"(prf_nivel);
CREATE INDEX IF NOT EXISTS idx_perfil_sts ON "Perfil"(prf_sts);
