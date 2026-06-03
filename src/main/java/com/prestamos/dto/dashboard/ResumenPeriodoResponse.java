package com.prestamos.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumenPeriodoResponse(
    LocalDate desde,
    LocalDate hasta,
    long pagosTotales,
    BigDecimal montoCobrado,
    long prestamosNuevos,
    BigDecimal capitalDesembolsado
) {}