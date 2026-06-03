package com.prestamos.dto.reporte;
import java.math.BigDecimal;
public record MoraReporteResponse(long prestamosEnMora, long cuotasVencidas, BigDecimal montoMoraTotal) {}
