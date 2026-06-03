package com.prestamos.dto.prestamo;

import com.prestamos.entity.enums.EstadoCuota;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaDetalleResponse(
    Long id,
    int numeroCuota,
    LocalDate fechaVencimiento,
    LocalDate fechaPago,
    BigDecimal montoCuota,
    BigDecimal montoPagado,
    BigDecimal saldoPendiente,
    BigDecimal moraRecargo,
    EstadoCuota estado
) {}