# Sistema de Gestión de Préstamos — Backend

API REST construida con **Spring Boot 3.2.5** y **Java 21** para la gestión de préstamos, cobranzas y clientes.

## Tecnologías

- Java 21 (Temurin)
- Spring Boot 3.2.5 (Web, Security, Data JPA, Validation, Cache, Actuator)
- PostgreSQL 17 (Neon serverless)
- Flyway (migraciones versionadas)
- JWT (JJWT 0.12.5)
- Lombok
- Caffeine Cache

## Cómo ejecutar

```bash
# Requiere JDK 21
export JAVA_HOME=/path/to/jdk21
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Credenciales por defecto

| Usuario | Contraseña | Rol   |
|---------|-----------|-------|
| admin   | admin123  | ADMIN |

## Endpoints principales

| Método | Ruta                          | Descripción               |
|--------|-------------------------------|---------------------------|
| POST   | /api/auth/login               | Autenticación JWT         |
| GET    | /api/clientes                 | Listar clientes           |
| POST   | /api/clientes                 | Crear cliente             |
| GET    | /api/prestamos                | Listar préstamos          |
| POST   | /api/prestamos                | Crear préstamo            |
| POST   | /api/prestamos/pagar          | Registrar pago            |
| GET    | /api/dashboard/metricas       | KPIs del dashboard        |
| GET    | /api/usuarios                 | Gestión de usuarios (ADMIN)|
## Arquitectura

```
src/main/java/com/prestamos/
├── controller/   # REST endpoints
├── service/      # Business logic
├── repository/   # JPA data access
├── entity/       # JPA entities
├── dto/          # Request/Response records
├── security/     # JWT + Spring Security
├── config/       # Security & cache config
└── exception/    # Global error handling
```

## Variables de entorno

Configurar en `application.properties` o como variables de entorno:
- `SPRING_DATASOURCE_URL` — JDBC URL de PostgreSQL
- `JWT_SECRET` — Clave secreta para tokens JWT

## Roles y permisos

| Rol     | Permisos                                               |
|---------|--------------------------------------------------------|
| ADMIN   | Acceso completo a todos los endpoints                  |
| COBRADOR| Leer clientes/préstamos, registrar pagos, ver dashboard|

## Cómo correr con JDK 21 en Windows

```powershell
$env:JAVA_HOME = "C:\Users\sheil\scoop\apps\temurin21-jdk\current"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
mvn spring-boot:run
```
