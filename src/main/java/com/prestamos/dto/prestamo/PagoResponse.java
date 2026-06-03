package com.prestamos.dto.prestamo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagoResponse(
    Long cuotaId,
    int numeroCuota,
    BigDecimal montoPagado,
    BigDecimal mora,
    LocalDate fechaPago,
    LocalDateTime registradoEn,
    String estado
) {}