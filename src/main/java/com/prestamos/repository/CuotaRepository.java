package com.prestamos.repository;

import com.prestamos.entity.Cuota;
import com.prestamos.entity.enums.EstadoCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    List<Cuota> findByPrestamoId(Long prestamoId);

    // Cuotas que vencen hoy (alertas del dashboard)
    @Query("""
        SELECT c FROM Cuota c
        JOIN FETCH c.prestamo p
        JOIN FETCH p.cliente cl
        WHERE c.fechaVencimiento = :hoy
          AND c.estado = 'PENDIENTE'
        ORDER BY p.cliente.apellido ASC
        """)
    List<Cuota> findCuotasVencenHoy(LocalDate hoy);

    // Cuotas que vencen en los próximos N días
    @Query("""
        SELECT c FROM Cuota c
        JOIN FETCH c.prestamo p
        JOIN FETCH p.cliente cl
        WHERE c.fechaVencimiento BETWEEN :desde AND :hasta
          AND c.estado = 'PENDIENTE'
        ORDER BY c.fechaVencimiento ASC
        """)
    List<Cuota> findCuotasProximas(LocalDate desde, LocalDate hasta);

    // Para el job de vencimiento: cuotas PENDIENTE cuya fecha ya pasó
    @Query("""
        SELECT c FROM Cuota c
        WHERE c.estado = 'PENDIENTE'
          AND c.fechaVencimiento < :hoy
        """)
    List<Cuota> findVencidasSinActualizar(LocalDate hoy);

    // Bulk update de estado (nativo para eficiencia)
    @Modifying
    @Query(value = """
        UPDATE cuotas
        SET estado = 'VENCIDO'
        WHERE estado = 'PENDIENTE'
          AND fecha_vencimiento < :hoy
        """, nativeQuery = true)
    int marcarCuotasComoVencidas(LocalDate hoy);

    long countByPrestamoIdAndEstado(Long prestamoId, com.prestamos.entity.enums.EstadoCuota estado);

    @Query("SELECT COALESCE(SUM(c.montoPagado), 0) FROM Cuota c WHERE c.prestamo.id = :prestamoId")
    java.math.BigDecimal sumMontoPagadoByPrestamoId(Long prestamoId);

    @Query("SELECT COUNT(c) FROM Cuota c WHERE c.estado = 'VENCIDO'")
    long countTotalVencidas();

    @Query("SELECT COALESCE(SUM(c.montoCuota - c.montoPagado), 0) FROM Cuota c WHERE c.estado = 'VENCIDO'")
    java.math.BigDecimal sumSaldoVencido();
}

