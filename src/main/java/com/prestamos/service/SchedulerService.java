package com.prestamos.service;

import com.prestamos.entity.Prestamo;
import com.prestamos.entity.enums.EstadoCuota;
import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.repository.CuotaRepository;
import com.prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final CuotaRepository cuotaRepository;
    private final PrestamoRepository prestamoRepository;

    /**
     * Se ejecuta todos los días a las 00:05 UTC.
     * 1. Marca cuotas PENDIENTE cuya fecha_vencimiento < hoy como VENCIDO.
     * 2. Marca préstamos con al menos una cuota VENCIDO como EN_MORA.
     * 3. Invalida la caché del dashboard.
     */
    @Scheduled(cron = "0 5 0 * * *", zone = "UTC")
    @Transactional
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public void actualizarEstadosVencidos() {
        LocalDate hoy = LocalDate.now();

        // ── Paso 1: bulk update de cuotas ────────────────────────────────────
        int cuotasActualizadas = cuotaRepository.marcarCuotasComoVencidas(hoy);
        log.info("[Scheduler] Cuotas marcadas como VENCIDO: {}", cuotasActualizadas);

        // ── Paso 2: préstamos activos con alguna cuota vencida → EN_MORA ─────
        List<Prestamo> activosPotencialmenteMora = prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO);

        long enMoraActualizados = activosPotencialmenteMora.stream()
            .filter(p -> p.getCuotas().stream()
                .anyMatch(c -> c.getEstado() == EstadoCuota.VENCIDO))
            .peek(p -> p.setEstado(EstadoPrestamo.EN_MORA))
            .count();

        if (enMoraActualizados > 0) {
            prestamoRepository.saveAll(activosPotencialmenteMora);
        }

        log.info("[Scheduler] Préstamos actualizados a EN_MORA: {}", enMoraActualizados);
    }

    @Scheduled(cron = "0 0 8 * * MON", zone = "UTC")
    @Transactional(readOnly = true)
    public void resumenSemanalLog() {
        long activos = prestamoRepository.countByEstado(com.prestamos.entity.enums.EstadoPrestamo.ACTIVO);
        long mora    = prestamoRepository.countByEstado(com.prestamos.entity.enums.EstadoPrestamo.EN_MORA);
        log.info("[Scheduler-Semanal] Préstamos activos={} en_mora={}", activos, mora);
    }
}
