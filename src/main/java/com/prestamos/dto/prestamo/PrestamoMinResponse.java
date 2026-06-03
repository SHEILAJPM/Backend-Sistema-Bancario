package com.prestamos.dto.prestamo;
import com.prestamos.entity.enums.EstadoPrestamo;
import java.math.BigDecimal;
public record PrestamoMinResponse(Long id, BigDecimal capital, int cuotas, EstadoPrestamo estado) {}
