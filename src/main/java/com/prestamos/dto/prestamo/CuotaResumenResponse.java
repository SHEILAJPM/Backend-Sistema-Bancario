package com.prestamos.dto.prestamo;

import com.prestamos.entity.enums.EstadoCuota;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaResumenResponse(
    Long id,
    Integer numeroCuota,
    LocalDate fechaVencimiento,
    BigDecimal montoCuota,
    BigDecimal montoPagado,
    BigDecimal saldoPendiente,
    EstadoCuota estado,
    LocalDate fechaPago
) {}
