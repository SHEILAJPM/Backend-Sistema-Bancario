package com.prestamos.dto.prestamo;

import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.entity.enums.FrecuenciaPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PrestamoResponse(
    Long id,
    Long clienteId,
    String clienteNombre,
    BigDecimal capital,
    BigDecimal tasaInteres,
    BigDecimal interesProyectado,
    BigDecimal montoTotal,
    Integer numeroCuotas,
    FrecuenciaPago frecuencia,
    LocalDate fechaInicio,
    EstadoPrestamo estado,
    String observacion,
    LocalDateTime createdAt,
    List<CuotaResumenResponse> cuotas
) {}
