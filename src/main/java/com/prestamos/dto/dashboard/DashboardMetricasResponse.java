package com.prestamos.dto.dashboard;

import java.math.BigDecimal;

public record DashboardMetricasResponse(
    BigDecimal capitalEnLaCalle,
    BigDecimal interesesPendientes,
    BigDecimal interesesLiquidos,
    BigDecimal interesProyectadoTotal,
    BigDecimal totalRecuperado,
    BigDecimal rendimientoGlobalPct,
    long prestamosActivos,
    long prestamosEnMora,
    long prestamosPagados,
    int cuotasVencenHoy,
    int cuotasProximas7Dias
) {}
