package com.prestamos.entity;

import com.prestamos.entity.enums.EstadoCuota;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cuotas", indexes = {
    @Index(name = "idx_cuota_vencimiento", columnList = "fecha_vencimiento"),
    @Index(name = "idx_cuota_estado",      columnList = "estado"),
    @Index(name = "idx_cuota_prestamo_id", columnList = "prestamo_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "monto_cuota", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoCuota;

    @Column(name = "monto_pagado", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuota estado;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "mora_recargo", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal moraRecargo = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cuota", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<HistorialPago> pagos = new ArrayList<>();
}
