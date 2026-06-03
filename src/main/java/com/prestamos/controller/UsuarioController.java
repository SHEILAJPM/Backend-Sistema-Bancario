package com.prestamos.controller;

import com.prestamos.dto.auth.ChangePasswordRequest;
import com.prestamos.dto.usuario.UsuarioRequest;
import com.prestamos.dto.usuario.UsuarioResponse;
import com.prestamos.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<UsuarioResponse> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.toggleActivo(id));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        usuarioService.cambiarPassword(id, request);
        return ResponseEntity.noContent().build();
    }
}