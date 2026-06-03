package com.prestamos.dto.dashboard;

import com.prestamos.entity.enums.EstadoCuota;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlertaCobro(
    Long cuotaId,
    Long prestamoId,
    Long clienteId,
    String clienteNombre,
    String clienteTelefono,
    String clienteDireccion,
    Integer numeroCuota,
    LocalDate fechaVencimiento,
    BigDecimal montoCuota,
    BigDecimal montoPagado,
    BigDecimal saldoPendiente,
    EstadoCuota estado
) {}
