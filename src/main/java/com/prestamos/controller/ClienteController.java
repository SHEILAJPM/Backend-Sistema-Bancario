package com.prestamos.controller;

import com.prestamos.dto.cliente.ClienteEstadisticasResponse;
import com.prestamos.dto.cliente.ClienteRequest;
import com.prestamos.dto.cliente.ClienteResponse;
import com.prestamos.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listar(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "apellido") Pageable pageable) {
        return ResponseEntity.ok(clienteService.listar(q, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(clienteService.contarClientes());
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<ClienteEstadisticasResponse> estadisticas(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.getEstadisticas(id));
    }
}
