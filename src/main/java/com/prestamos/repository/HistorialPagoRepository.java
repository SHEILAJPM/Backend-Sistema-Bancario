package com.prestamos.repository;

import com.prestamos.entity.HistorialPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialPagoRepository extends JpaRepository<HistorialPago, Long> {

    List<HistorialPago> findByCuotaIdOrderByTimestampPagoDesc(Long cuotaId);

    @Query("SELECT h FROM HistorialPago h WHERE h.cuota.prestamo.id = :prestamoId ORDER BY h.timestampPago DESC")
    List<HistorialPago> findByPrestamoIdOrderByTimestampDesc(Long prestamoId);

    @Query("""
        SELECT COALESCE(SUM(h.montoRecibido), 0)
        FROM HistorialPago h
        WHERE h.timestampPago BETWEEN :desde AND :hasta
        """)
    BigDecimal sumRecaudadoEnPeriodo(LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT COUNT(h) FROM HistorialPago h WHERE h.timestampPago BETWEEN :desde AND :hasta")
    long countByTimestampPagoBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query("""
        SELECT COUNT(h), COALESCE(SUM(h.montoRecibido), 0)
        FROM HistorialPago h
        JOIN h.registradoPor u
        WHERE u.username = :username
        """)
    List<Object[]> sumarioByRegistradoPor(String username);

    @Query(value = """
        SELECT
            TO_CHAR(DATE_TRUNC('month', timestamp_pago), 'Mon YYYY') AS mes,
            COALESCE(SUM(monto_recibido), 0.00)                      AS total
        FROM historial_pagos
        WHERE timestamp_pago >= CURRENT_TIMESTAMP - INTERVAL '6 months'
        GROUP BY DATE_TRUNC('month', timestamp_pago)
        ORDER BY DATE_TRUNC('month', timestamp_pago)
        """, nativeQuery = true)
    List<Object[]> findFlujoPorMes();
}