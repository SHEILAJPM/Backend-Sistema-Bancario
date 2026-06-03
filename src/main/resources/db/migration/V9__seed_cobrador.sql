-- ============================================================
-- V9 — Agrega usuario cobrador de ejemplo para pruebas
--       usuario: cobrador  |  contraseña: cobrador123
-- ============================================================
INSERT INTO usuarios (username, password_hash, email, rol, activo)
VALUES (
    'cobrador',
    '$2a$12$LtrfcVhCzzeb3MJErSr5cuQ8tIAMQqYgsT5h3v4Ea2ts6gi4GVnk2',
    'cobrador@prestamos.com',
    'COBRADOR',
    true
)
ON CONFLICT (username) DO NOTHING;