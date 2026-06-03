package com.prestamos.dto.dashboard;

import java.math.BigDecimal;

public record TopClienteResponse(
    Long clienteId,
    String nombreCompleto,
    int totalPrestamos,
    BigDecimal capitalTotal
) {}
