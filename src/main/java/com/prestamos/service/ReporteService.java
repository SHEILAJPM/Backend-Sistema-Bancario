package com.prestamos.service;

import com.prestamos.dto.dashboard.ResumenPeriodoResponse;
import com.prestamos.dto.pago.ResumenCobradorResponse;
import com.prestamos.dto.reporte.MoraReporteResponse;
import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.repository.CuotaRepository;
import com.prestamos.repository.HistorialPagoRepository;
import com.prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final HistorialPagoRepository historialPagoRepository;
    private final PrestamoRepository prestamoRepository;
    private final CuotaRepository cuotaRepository;

    public ResumenPeriodoResponse resumenPorPeriodo(LocalDate desde, LocalDate hasta) {
        log.info("Generando resumen periodo {} - {}", desde, hasta);
        LocalDateTime desdeTime = desde.atStartOfDay();
        LocalDateTime hastaTime = hasta.atTime(23, 59, 59);

        var montoCobrado      = historialPagoRepository.sumRecaudadoEnPeriodo(desdeTime, hastaTime);
        long pagosTotales     = historialPagoRepository.countByTimestampPagoBetween(desdeTime, hastaTime);
        long prestamosNuevos  = prestamoRepository.countByCreatedAtBetween(desdeTime, hastaTime);
        var capitalDesembolsado = prestamoRepository.sumCapitalByCreatedAtBetween(desdeTime, hastaTime);

        return new ResumenPeriodoResponse(
            desde, hasta, pagosTotales, montoCobrado, prestamosNuevos, capitalDesembolsado
        );
    }

    public MoraReporteResponse reporteMora() {
        long prestamosEnMora = prestamoRepository.countByEstado(EstadoPrestamo.EN_MORA);
        long cuotasVencidas  = cuotaRepository.countTotalVencidas();
        java.math.BigDecimal montoMoraTotal = cuotaRepository.sumSaldoVencido();
        return new MoraReporteResponse(prestamosEnMora, cuotasVencidas, montoMoraTotal);
    }

    public ResumenCobradorResponse resumenCobrador(String username) {
        java.util.List<Object[]> rows = historialPagoRepository.sumarioByRegistradoPor(username);
        if (rows.isEmpty()) {
            return new ResumenCobradorResponse(username, 0L, java.math.BigDecimal.ZERO);
        }
        Object[] row = rows.get(0);
        long pagos = row[0] == null ? 0L : ((Number) row[0]).longValue();
        java.math.BigDecimal monto = row[1] == null
            ? java.math.BigDecimal.ZERO
            : new java.math.BigDecimal(row[1].toString());
        return new ResumenCobradorResponse(username, pagos, monto);
    }
}