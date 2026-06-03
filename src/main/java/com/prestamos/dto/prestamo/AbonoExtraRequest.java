package com.prestamos.dto.prestamo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
public record AbonoExtraRequest(
    @NotNull Long prestamoId,
    @NotNull @Positive BigDecimal monto,
    String observacion
) {}
