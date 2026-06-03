package com.prestamos.dto.pago;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
public record PagoMasivoRequest(
    @NotEmpty List<PagoRequest> pagos
) {}
