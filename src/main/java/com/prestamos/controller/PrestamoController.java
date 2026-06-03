package com.prestamos.controller;

import com.prestamos.dto.pago.PagoRequest;
import com.prestamos.dto.prestamo.*;
import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.service.PagoService;
import com.prestamos.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<Page<PrestamoResponse>> listar(
            @RequestParam(required = false) EstadoPrestamo estado,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(prestamoService.listar(estado, pageable));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<PrestamoResponse>> listarPorCliente(
            @PathVariable Long clienteId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(prestamoService.listarPorCliente(clienteId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<PrestamoResponse> crear(@Valid @RequestBody PrestamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.crear(request));
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<SaldoResponse> saldo(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.getSaldo(id));
    }

    @PostMapping("/pagar")
    public ResponseEntity<Void> registrarPago(@Valid @RequestBody PagoRequest request) {
        pagoService.registrarPago(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody CancelacionRequest request) {
        prestamoService.cancelar(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reestructurar")
    public ResponseEntity<PrestamoResponse> reestructurar(
            @PathVariable Long id,
            @Valid @RequestBody ReestructuracionRequest request) {
        return ResponseEntity.ok(prestamoService.reestructurar(id, request));
    }

    @PostMapping("/{id}/abono-extra")
    public ResponseEntity<Void> abonoExtra(
            @PathVariable Long id,
            @Valid @RequestBody AbonoExtraRequest request) {
        prestamoService.abonoExtra(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<PrestamoEstadisticasResponse> estadisticas(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.getEstadisticas(id));
    }
}
