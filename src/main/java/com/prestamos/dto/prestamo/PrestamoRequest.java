package com.prestamos.dto.prestamo;

import com.prestamos.entity.enums.FrecuenciaPago;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PrestamoRequest(
    @NotNull Long clienteId,

    @NotNull @DecimalMin("100.00")
    BigDecimal capital,

    @NotNull @DecimalMin("0.001") @DecimalMax("1.000")
    BigDecimal tasaInteres,

    @NotNull @Min(1) @Max(360)
    Integer numeroCuotas,

    @NotNull
    FrecuenciaPago frecuencia,

    @NotNull
    LocalDate fechaInicio,

    @Size(max = 500)
    String observacion
) {}
