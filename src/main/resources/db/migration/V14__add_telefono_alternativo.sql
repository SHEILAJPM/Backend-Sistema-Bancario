-- V14 Agrega teléfono alternativo al cliente
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS telefono_alt VARCHAR(20);
