package com.prestamos.controller;

import com.prestamos.dto.dashboard.ResumenPeriodoResponse;
import com.prestamos.dto.pago.ResumenCobradorResponse;
import com.prestamos.dto.reporte.MoraReporteResponse;
import com.prestamos.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/periodo")
    public ResponseEntity<ResumenPeriodoResponse> resumenPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.resumenPorPeriodo(desde, hasta));
    }

    @GetMapping("/mora")
    public ResponseEntity<MoraReporteResponse> reporteMora() {
        return ResponseEntity.ok(reporteService.reporteMora());
    }

    @GetMapping("/cobrador/{username}")
    public ResponseEntity<ResumenCobradorResponse> resumenCobrador(@PathVariable String username) {
        return ResponseEntity.ok(reporteService.resumenCobrador(username));
    }
}