package com.prestamos.entity;

import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.entity.enums.FrecuenciaPago;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prestamos", indexes = {
    @Index(name = "idx_prestamo_estado",      columnList = "estado"),
    @Index(name = "idx_prestamo_cliente_id",  columnList = "cliente_id"),
    @Index(name = "idx_prestamo_fecha_inicio",columnList = "fecha_inicio")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal capital;

    @Column(name = "tasa_interes", nullable = false, precision = 6, scale = 4)
    private BigDecimal tasaInteres;

    @Column(name = "interes_proyectado", nullable = false, precision = 14, scale = 2)
    private BigDecimal interesProyectado;

    @Column(name = "monto_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "numero_cuotas", nullable = false)
    private Integer numeroCuotas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FrecuenciaPago frecuencia;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado;

    @Column(length = 500)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cobrador_id")
    private Usuario cobrador;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "prestamo", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroCuota ASC")
    @Builder.Default
    private List<Cuota> cuotas = new ArrayList<>();
}
