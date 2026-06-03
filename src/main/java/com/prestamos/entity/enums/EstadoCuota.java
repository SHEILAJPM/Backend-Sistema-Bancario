package com.prestamos.entity.enums;

public enum EstadoCuota {
    PENDIENTE,
    PAGADO,
    VENCIDO;

    public boolean isPendienteOVencido() {
        return this == PENDIENTE || this == VENCIDO;
    }
}
