package com.prestamos.dto.usuario;

import com.prestamos.entity.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 80, message = "El username debe tener entre 3 y 80 caracteres")
    String username,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    String email,

    @NotNull(message = "El rol es obligatorio")
    Rol rol
) {}