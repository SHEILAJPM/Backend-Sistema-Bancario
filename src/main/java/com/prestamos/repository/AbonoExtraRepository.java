package com.prestamos.repository;

import com.prestamos.entity.AbonoExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbonoExtraRepository extends JpaRepository<AbonoExtra, Long> {
    List<AbonoExtra> findByPrestamoIdOrderByRegistradoEnDesc(Long prestamoId);
}
