-- ============================================================
-- V10 — Agrega estado CANCELADO al tipo estado_prestamo
-- ============================================================
ALTER TABLE prestamos
  ALTER COLUMN estado TYPE VARCHAR(20);

UPDATE prestamos SET estado = estado;