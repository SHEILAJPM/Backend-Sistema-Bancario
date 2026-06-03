package com.prestamos.controller;

import com.prestamos.dto.pago.PagoHistorialResponse;
import com.prestamos.dto.pago.PagoMasivoRequest;
import com.prestamos.entity.HistorialPago;
import com.prestamos.repository.HistorialPagoRepository;
import com.prestamos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final HistorialPagoRepository historialPagoRepository;
    private final PagoService pagoService;

    @PostMapping("/masivo")
    public ResponseEntity<Void> pagoMasivo(@Valid @RequestBody PagoMasivoRequest request) {
        pagoService.registrarPagoMasivo(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/prestamo/{prestamoId}")
    public ResponseEntity<List<PagoHistorialResponse>> historialPorPrestamo(@PathVariable Long prestamoId) {
        List<PagoHistorialResponse> historial = historialPagoRepository
            .findByPrestamoIdOrderByTimestampDesc(prestamoId)
            .stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(historial);
    }

    private PagoHistorialResponse toResponse(HistorialPago h) {
        return new PagoHistorialResponse(
            h.getId(),
            h.getCuota().getId(),
            h.getCuota().getNumeroCuota(),
            h.getMontoRecibido(),
            h.getTimestampPago(),
            h.getRegistradoPor() != null ? h.getRegistradoPor().getUsername() : "sistema",
            h.getObservacion()
        );
    }
}