package com.prestamos.dto.dashboard;
import java.math.BigDecimal;
public record KpiCardResponse(String titulo, BigDecimal valor, String unidad, double variacion) {}
