package com.prestamos.repository;

import com.prestamos.entity.Prestamo;
import com.prestamos.entity.enums.EstadoPrestamo;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    Page<Prestamo> findByClienteId(Long clienteId, Pageable pageable);

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    Page<Prestamo> findByEstado(EstadoPrestamo estado, Pageable pageable);

    // Métricas del dashboard: capital total activo en la calle
    @Query("SELECT COALESCE(SUM(p.capital), 0) FROM Prestamo p WHERE p.estado IN ('ACTIVO', 'EN_MORA')")
    BigDecimal sumCapitalActivo();

    // Interés proyectado total pendiente de cobrar
    @Query("""
        SELECT COALESCE(SUM(c.montoCuota - c.montoPagado), 0)
        FROM Cuota c
        WHERE c.estado IN ('PENDIENTE', 'VENCIDO')
        """)
    BigDecimal sumInteresProyectadoPendiente();

    // Total recuperado (pagos históricos)
    @Query("SELECT COALESCE(SUM(h.montoRecibido), 0) FROM HistorialPago h")
    BigDecimal sumTotalRecuperado();

    // Intereses líquidos = porción de interés ya cobrada en efectivo
    @Query("""
        SELECT COALESCE(SUM(c.montoPagado * p.interesProyectado / p.montoTotal), 0)
        FROM Cuota c JOIN c.prestamo p
        WHERE c.montoPagado > 0
        """)
    BigDecimal sumInteresLiquido();

    // Interés proyectado total de todos los préstamos activos
    @Query("SELECT COALESCE(SUM(p.interesProyectado), 0) FROM Prestamo p WHERE p.estado IN ('ACTIVO', 'EN_MORA')")
    BigDecimal sumInteresProyectadoTotal();

    // Conteo por estado
    long countByEstado(EstadoPrestamo estado);

    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.createdAt BETWEEN :desde AND :hasta")
    long countByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT COALESCE(SUM(p.capital), 0) FROM Prestamo p WHERE p.createdAt BETWEEN :desde AND :hasta")
    BigDecimal sumCapitalByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);

    // Top clientes por capital total prestado
    @Query("""
        SELECT p.cliente.id,
               p.cliente.nombre,
               p.cliente.apellido,
               COUNT(p.id),
               COALESCE(SUM(p.capital), 0)
        FROM Prestamo p
        GROUP BY p.cliente.id, p.cliente.nombre, p.cliente.apellido
        ORDER BY SUM(p.capital) DESC
        """)
    List<Object[]> findTopClientesByCapital(Pageable pageable);

    // Estadísticas por cliente
    @Query("""
        SELECT p.estado, COUNT(p.id), COALESCE(SUM(p.capital), 0)
        FROM Prestamo p
        WHERE p.cliente.id = :clienteId
        GROUP BY p.estado
        """)
    List<Object[]> findEstadisticasByCliente(Long clienteId);
}
