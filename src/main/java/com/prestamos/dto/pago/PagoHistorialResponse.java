package com.prestamos.dto.pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoHistorialResponse(
    Long id,
    Long cuotaId,
    int numeroCuota,
    BigDecimal montoRecibido,
    LocalDateTime timestampPago,
    String registradoPor,
    String observacion
) {}