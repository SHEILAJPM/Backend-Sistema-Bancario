package com.prestamos.dto.reporte;

import java.math.BigDecimal;

public record ResumenMensualResponse(
    int anio,
    int mes,
    long prestamosDesembolsados,
    BigDecimal montoDesembolsado,
    long pagosCobrados,
    BigDecimal montoCobrado,
    long cuotasVencidas,
    BigDecimal montoMoraAcumulada
) {}