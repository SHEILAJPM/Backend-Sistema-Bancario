package com.prestamos.dto.usuario;

import com.prestamos.entity.enums.Rol;
import java.time.LocalDateTime;

public record UsuarioResponse(
    Long id,
    String username,
    String email,
    Rol rol,
    boolean activo,
    LocalDateTime createdAt
) {}