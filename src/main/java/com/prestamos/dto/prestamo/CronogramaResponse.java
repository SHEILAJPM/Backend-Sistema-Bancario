package com.prestamos.dto.prestamo;
import java.util.List;
public record CronogramaResponse(Long prestamoId, int totalCuotas, List<CuotaDetalleResponse> cuotas) {}
