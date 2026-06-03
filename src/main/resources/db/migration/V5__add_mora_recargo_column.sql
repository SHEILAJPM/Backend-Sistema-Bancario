-- ============================================================
-- V5 — Agrega columna mora_recargo a cuotas para almacenar
--       el recargo por pago tardío calculado por el scheduler
-- ============================================================
ALTER TABLE cuotas ADD COLUMN IF NOT EXISTS mora_recargo NUMERIC(12,2) DEFAULT 0.00 NOT NULL;