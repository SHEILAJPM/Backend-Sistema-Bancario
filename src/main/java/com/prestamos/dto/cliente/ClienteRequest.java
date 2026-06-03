package com.prestamos.dto.cliente;

import jakarta.validation.constraints.*;

public record ClienteRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Size(max = 100) String apellido,
    @NotBlank @Size(max = 20)  String dni,
    @Size(max = 20)            String telefono,
    @Size(max = 250)           String direccion,
    @Email @Size(max = 120)    String email
) {}
