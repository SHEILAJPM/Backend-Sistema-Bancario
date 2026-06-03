-- V11 — Índice en email de clientes para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_clientes_email ON clientes(email) WHERE email IS NOT NULL;
