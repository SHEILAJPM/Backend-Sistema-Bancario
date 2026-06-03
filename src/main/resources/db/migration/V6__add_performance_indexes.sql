-- ============================================================
-- V6 — Índices adicionales para mejorar performance de queries
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_cuotas_estado          ON cuotas(estado);
CREATE INDEX IF NOT EXISTS idx_cuotas_fecha_vencimiento ON cuotas(fecha_vencimiento);
CREATE INDEX IF NOT EXISTS idx_prestamos_estado        ON prestamos(estado);
CREATE INDEX IF NOT EXISTS idx_prestamos_cliente_id    ON prestamos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_historial_timestamp     ON historial_pagos(timestamp_pago DESC);