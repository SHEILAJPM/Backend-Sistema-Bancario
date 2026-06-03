-- ============================================================
-- V8 — Agrega campo notas a historial_pagos para observaciones
--       adicionales del cobrador al momento del pago
-- ============================================================
ALTER TABLE historial_pagos ADD COLUMN IF NOT EXISTS notas TEXT;