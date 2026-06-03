package com.prestamos.dto.pago;
import java.math.BigDecimal;
import java.time.LocalDate;
public record CuotaPendienteResponse(Long cuotaId, int numero, LocalDate vencimiento, BigDecimal saldo) {}
