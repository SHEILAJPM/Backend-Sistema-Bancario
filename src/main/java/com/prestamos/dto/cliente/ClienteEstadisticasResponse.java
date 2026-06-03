package com.prestamos.dto.cliente;

import java.math.BigDecimal;

public record ClienteEstadisticasResponse(
    Long clienteId,
    String nombreCompleto,
    int totalPrestamos,
    int prestamosActivos,
    int prestamosPagados,
    int prestamosEnMora,
    BigDecimal capitalTotal,
    BigDecimal saldoPendiente
) {}