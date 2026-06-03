package com.prestamos.dto.prestamo;

import com.prestamos.entity.enums.FrecuenciaPago;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ReestructuracionRequest(
    @NotNull @Positive BigDecimal nuevoCapital,
    @NotNull @Positive BigDecimal nuevaTasa,
    @NotNull @Min(1) Integer nuevasCuotas,
    @NotNull FrecuenciaPago nuevaFrecuencia,
    String motivo
) {}