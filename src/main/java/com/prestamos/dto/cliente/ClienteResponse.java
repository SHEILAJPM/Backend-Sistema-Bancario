package com.prestamos.dto.cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
    Long id,
    String nombre,
    String apellido,
    String nombreCompleto,
    String dni,
    String telefono,
    String direccion,
    String email,
    boolean activo,
    LocalDateTime createdAt,
    int totalPrestamos
) {}
