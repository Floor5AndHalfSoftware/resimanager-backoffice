-- Create Propietario table (Owner/Property assignment)
-- This replaces the old ProptPropiedad approach with a direct conjunt-persona-property relationship

CREATE TABLE IF NOT EXISTS "Propietario" (
    Ppt_ConjID INTEGER NOT NULL,
    Ppt_PerID INTEGER NOT NULL,
    PptID INTEGER NOT NULL,
    PptFchDesde DATE NOT NULL,
    PptFchHasta DATE,
    PptSts VARCHAR(1) NOT NULL DEFAULT 'A',
    PptFchHorCrea TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PptEstCrea VARCHAR(40) NOT NULL,
    PptFchHorMod TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PptEstMod VARCHAR(40) NOT NULL,
    CONSTRAINT pk_propietario PRIMARY KEY (Ppt_ConjID, Ppt_PerID),
    CONSTRAINT fk_propietario_conjunto FOREIGN KEY (Ppt_ConjID) REFERENCES "Conjunto"(conjid) ON DELETE RESTRICT,
    CONSTRAINT fk_propietario_persona FOREIGN KEY (Ppt_PerID) REFERENCES "Persona"(perid) ON DELETE RESTRICT,
    CONSTRAINT fk_propietario_propiedad FOREIGN KEY (PptID) REFERENCES "Propiedad"(ppid) ON DELETE RESTRICT
);

COMMENT ON TABLE "Propietario" IS 'Propietarios - asignacion de persona como propietaria de una propiedad en un conjunto';
COMMENT ON COLUMN "Propietario".Ppt_ConjID IS 'ID del conjunto';
COMMENT ON COLUMN "Propietario".Ppt_PerID IS 'ID de la persona (propietario)';
COMMENT ON COLUMN "Propietario".PptID IS 'ID de la propiedad';
COMMENT ON COLUMN "Propietario".PptFchDesde IS 'Fecha desde la cual es propietario';
COMMENT ON COLUMN "Propietario".PptFchHasta IS 'Fecha hasta la cual es propietario (null si actual)';
COMMENT ON COLUMN "Propietario".PptSts IS 'Estatus: A=Activo, I=Inactivo';
