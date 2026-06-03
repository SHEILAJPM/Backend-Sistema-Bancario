-- V13 Vincula el cobrador asignado a cada préstamo
ALTER TABLE prestamos ADD COLUMN IF NOT EXISTS cobrador_id BIGINT REFERENCES usuarios(id);
CREATE INDEX IF NOT EXISTS idx_prestamos_cobrador ON prestamos(cobrador_id);
