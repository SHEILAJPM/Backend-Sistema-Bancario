package com.prestamos.dto.prestamo;

import java.math.BigDecimal;

public record PrestamoEstadisticasResponse(
    Long prestamoId,
    int totalCuotas,
    int cuotasPagadas,
    int cuotasPendientes,
    int cuotasVencidas,
    BigDecimal capitalOriginal,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente,
    double porcentajeAvance
) {}