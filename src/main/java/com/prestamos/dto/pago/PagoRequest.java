package com.prestamos.dto.pago;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PagoRequest(
    @NotNull Long cuotaId,

    @NotNull @DecimalMin("0.01")
    BigDecimal montoRecibido,

    @Size(max = 500)
    String observacion
) {}
