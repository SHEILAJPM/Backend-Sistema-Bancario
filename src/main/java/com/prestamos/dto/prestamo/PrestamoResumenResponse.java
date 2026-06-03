package com.prestamos.dto.prestamo;

import com.prestamos.entity.enums.EstadoPrestamo;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PrestamoResumenResponse(
    Long id,
    String clienteNombre,
    BigDecimal capital,
    int cuotasTotales,
    int cuotasPagadas,
    EstadoPrestamo estado,
    LocalDate fechaInicio
) {}
