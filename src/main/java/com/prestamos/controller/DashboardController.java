package com.prestamos.controller;

import com.prestamos.dto.dashboard.AlertaCobro;
import com.prestamos.dto.dashboard.DashboardMetricasResponse;
import com.prestamos.dto.dashboard.FlujoCajaMesResponse;
import com.prestamos.dto.dashboard.TopClienteResponse;
import com.prestamos.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metricas")
    public ResponseEntity<DashboardMetricasResponse> metricas() {
        return ResponseEntity.ok(dashboardService.obtenerMetricas());
    }

    @GetMapping("/alertas/hoy")
    public ResponseEntity<List<AlertaCobro>> alertasHoy() {
        return ResponseEntity.ok(dashboardService.obtenerAlertasHoy());
    }

    @GetMapping("/alertas/proxima-semana")
    public ResponseEntity<List<AlertaCobro>> alertasProximaSemana() {
        return ResponseEntity.ok(dashboardService.obtenerProximasSemana());
    }

    @GetMapping("/alertas/count")
    public ResponseEntity<Long> contarAlertasHoy() {
        return ResponseEntity.ok(dashboardService.contarAlertasHoy());
    }

    @GetMapping("/flujo-caja")
    public ResponseEntity<List<FlujoCajaMesResponse>> flujoCaja() {
        return ResponseEntity.ok(dashboardService.getFlujoCaja());
    }

    @GetMapping("/top-clientes")
    public ResponseEntity<List<TopClienteResponse>> topClientes(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getTopClientes(limit));
    }

    @PostMapping("/cache/evict")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public ResponseEntity<Void> evictCache() {
        return ResponseEntity.noContent().build();
    }
}
