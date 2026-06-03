package com.prestamos.entity.enums;

public enum FrecuenciaPago {
    DIARIO(1),
    SEMANAL(7),
    QUINCENAL(15),
    MENSUAL(30);

    private final int diasIntervalo;

    FrecuenciaPago(int diasIntervalo) {
        this.diasIntervalo = diasIntervalo;
    }

    public int getDiasIntervalo() {
        return diasIntervalo;
    }
}
