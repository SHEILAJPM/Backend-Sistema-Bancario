-- V16 — Actualiza CHECK constraints para incluir CANCELADO en prestamos
ALTER TABLE prestamos DROP CONSTRAINT IF EXISTS prestamos_estado_check;
ALTER TABLE prestamos
    ADD CONSTRAINT prestamos_estado_check
    CHECK (estado IN ('ACTIVO', 'PAGADO', 'EN_MORA', 'CANCELADO'));
