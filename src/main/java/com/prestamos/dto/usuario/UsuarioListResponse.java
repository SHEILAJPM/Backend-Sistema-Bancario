package com.prestamos.dto.usuario;

import com.prestamos.entity.enums.Rol;
import java.util.List;

public record UsuarioListResponse(
    List<UsuarioResponse> usuarios,
    long totalActivos,
    long totalInactivos,
    long totalAdmins,
    long totalCobradores
) {}