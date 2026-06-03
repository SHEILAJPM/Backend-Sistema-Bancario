-- ============================================================
-- V7 — Agrega campo observacion_interna a préstamos para
--       notas internas del cobrador o administrador
-- ============================================================
ALTER TABLE prestamos ADD COLUMN IF NOT EXISTS observacion_interna TEXT;