package com.prestamos.repository;

import com.prestamos.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDni(String dni);

    @Query("""
        SELECT c FROM Cliente c
        WHERE c.activo = true
          AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :q, '%'))
            OR c.dni LIKE CONCAT('%', :q, '%'))
        """)
    Page<Cliente> buscar(String q, Pageable pageable);

    Page<Cliente> findByActivoTrue(Pageable pageable);

    boolean existsByDni(String dni);

    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.cliente.id = :clienteId AND p.estado = 'ACTIVO'")
    long countPrestamosActivosByCliente(Long clienteId);

    long countByActivoTrue();
}
