-- ============================================================
-- V4 — Agrega columna updated_at a tablas principales
-- ============================================================
ALTER TABLE clientes    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT now();
ALTER TABLE prestamos   ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT now();
ALTER TABLE cuotas      ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT now();
ALTER TABLE usuarios    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT now();