package com.prestamos.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "abonos_extra")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AbonoExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @CreationTimestamp
    @Column(name = "registrado_en", nullable = false, updatable = false)
    private LocalDateTime registradoEn;
}
