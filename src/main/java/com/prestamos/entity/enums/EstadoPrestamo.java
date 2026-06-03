package com.prestamos.entity.enums;

public enum EstadoPrestamo {
    ACTIVO,
    PAGADO,
    EN_MORA,
    CANCELADO;

    public boolean isTerminal() {
        return this == PAGADO || this == CANCELADO;
    }
}
