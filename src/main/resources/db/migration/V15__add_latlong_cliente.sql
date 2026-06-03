-- V15 Coordenadas GPS opcionales del cliente para cobradores móviles
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS latitud  DOUBLE PRECISION;
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS longitud DOUBLE PRECISION;
