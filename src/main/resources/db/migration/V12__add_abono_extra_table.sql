-- V12 Tabla para abonos extraordinarios al capital
CREATE TABLE IF NOT EXISTS abonos_extra (
    id            BIGSERIAL PRIMARY KEY,
    prestamo_id   BIGINT NOT NULL REFERENCES prestamos(id),
    monto         NUMERIC(12,2) NOT NULL,
    observacion   TEXT,
    registrado_en TIMESTAMPTZ DEFAULT now()
);
