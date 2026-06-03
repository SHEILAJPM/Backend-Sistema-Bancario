package com.prestamos.dto.prestamo;
import java.math.BigDecimal;
public record SaldoResponse(
    Long prestamoId,
    BigDecimal saldoCapital,
    BigDecimal saldoInteres,
    BigDecimal saldoTotal,
    int cuotasPendientes
) {}
