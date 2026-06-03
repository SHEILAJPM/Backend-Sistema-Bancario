-- ============================================================
-- V1 — Esquema inicial del Sistema de Préstamos y Cobranzas
-- ============================================================

-- ── Usuarios (administradores y cobradores) ──────────────────
CREATE TABLE usuarios (
    id            BIGSERIAL    PRIMARY KEY,
    username      VARCHAR(80)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    rol           VARCHAR(20)  NOT NULL CHECK (rol IN ('ADMIN','COBRADOR')),
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuario_username ON usuarios(username);
CREATE INDEX idx_usuario_email    ON usuarios(email);

-- ── Clientes (prestatarios) ───────────────────────────────────
CREATE TABLE clientes (
    id         BIGSERIAL    PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    dni        VARCHAR(20)  NOT NULL UNIQUE,
    telefono   VARCHAR(20),
    direccion  VARCHAR(250),
    email      VARCHAR(120),
    activo     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cliente_dni ON clientes(dni);

-- ── Préstamos ─────────────────────────────────────────────────
CREATE TABLE prestamos (
    id                  BIGSERIAL      PRIMARY KEY,
    cliente_id          BIGINT         NOT NULL REFERENCES clientes(id),
    capital             NUMERIC(14,2)  NOT NULL,
    tasa_interes        NUMERIC(6,4)   NOT NULL,
    interes_proyectado  NUMERIC(14,2)  NOT NULL,
    monto_total         NUMERIC(14,2)  NOT NULL,
    numero_cuotas       INTEGER        NOT NULL,
    frecuencia          VARCHAR(20)    NOT NULL CHECK (frecuencia IN ('DIARIO','SEMANAL','QUINCENAL','MENSUAL')),
    fecha_inicio        DATE           NOT NULL,
    estado              VARCHAR(20)    NOT NULL CHECK (estado IN ('ACTIVO','PAGADO','EN_MORA')),
    observacion         VARCHAR(500),
    created_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_prestamo_estado       ON prestamos(estado);
CREATE INDEX idx_prestamo_cliente_id   ON prestamos(cliente_id);
CREATE INDEX idx_prestamo_fecha_inicio ON prestamos(fecha_inicio);

-- ── Cuotas ────────────────────────────────────────────────────
CREATE TABLE cuotas (
    id               BIGSERIAL     PRIMARY KEY,
    prestamo_id      BIGINT        NOT NULL REFERENCES prestamos(id),
    numero_cuota     INTEGER       NOT NULL,
    fecha_vencimiento DATE         NOT NULL,
    monto_cuota      NUMERIC(14,2) NOT NULL,
    monto_pagado     NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    estado           VARCHAR(20)   NOT NULL CHECK (estado IN ('PENDIENTE','PAGADO','VENCIDO')),
    fecha_pago       DATE,
    CONSTRAINT uq_cuota_prestamo_numero UNIQUE (prestamo_id, numero_cuota)
);

CREATE INDEX idx_cuota_vencimiento ON cuotas(fecha_vencimiento);
CREATE INDEX idx_cuota_estado      ON cuotas(estado);
CREATE INDEX idx_cuota_prestamo_id ON cuotas(prestamo_id);

-- ── Historial de Pagos (auditoría de caja) ────────────────────
CREATE TABLE historial_pagos (
    id              BIGSERIAL     PRIMARY KEY,
    cuota_id        BIGINT        NOT NULL REFERENCES cuotas(id),
    monto_recibido  NUMERIC(14,2) NOT NULL,
    timestamp_pago  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    registrado_por  BIGINT        REFERENCES usuarios(id),
    observacion     VARCHAR(500)
);

CREATE INDEX idx_histpago_cuota_id  ON historial_pagos(cuota_id);
CREATE INDEX idx_histpago_timestamp ON historial_pagos(timestamp_pago);
