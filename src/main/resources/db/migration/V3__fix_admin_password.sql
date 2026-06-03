-- ============================================================
-- V3 — Corrige el hash del admin con BCrypt real
-- usuario: admin  |  contraseña: admin123
-- ============================================================
UPDATE usuarios
SET password_hash = '$2a$12$LtrfcVhCzzeb3MJErSr5cuQ8tIAMQqYgsT5h3v4Ea2ts6gi4GVnk2'
WHERE username = 'admin';
