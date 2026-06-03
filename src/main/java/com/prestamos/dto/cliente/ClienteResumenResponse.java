package com.prestamos.dto.cliente;

public record ClienteResumenResponse(
    Long id,
    String nombreCompleto,
    String dni,
    String telefono,
    int prestamosActivos
) {}