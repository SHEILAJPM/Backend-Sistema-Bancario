package com.prestamos.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_pagos", indexes = {
    @Index(name = "idx_histpago_cuota_id",  columnList = "cuota_id"),
    @Index(name = "idx_histpago_timestamp", columnList = "timestamp_pago")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuota_id", nullable = false)
    private Cuota cuota;

    @Column(name = "monto_recibido", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoRecibido;

    @CreationTimestamp
    @Column(name = "timestamp_pago", nullable = false, updatable = false)
    private LocalDateTime timestampPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    @Column(length = 500)
    private String observacion;
}
