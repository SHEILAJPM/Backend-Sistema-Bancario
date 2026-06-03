package com.prestamos.dto.prestamo;

import jakarta.validation.constraints.NotBlank;

public record CancelacionRequest(
    @NotBlank(message = "El motivo de cancelación es obligatorio")
    String motivo
) {}