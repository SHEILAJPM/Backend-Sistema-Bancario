-- ============================================================
-- V2 — Usuario administrador inicial
-- password: Admin123! (BCrypt $2a$12$...)
-- ============================================================
INSERT INTO usuarios (username, password_hash, email, rol, activo)
VALUES (
    'admin',
    '$2a$12$9z3QGiYeNzivNX7R4GXlFOxh0gBt3QHGK9V1Z8A8jUpHHMC8K7HHC',
    'admin@prestamos.com',
    'ADMIN',
    true
)
ON CONFLICT (username) DO NOTHING;
