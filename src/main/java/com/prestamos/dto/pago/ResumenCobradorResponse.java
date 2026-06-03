package com.prestamos.dto.pago;
import java.math.BigDecimal;
public record ResumenCobradorResponse(String username, long pagosRegistrados, BigDecimal montoCobrado) {}
