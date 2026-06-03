package com.prestamos.repository;

import com.prestamos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameAndActivoTrue(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Usuario> findByRol(com.prestamos.entity.enums.Rol rol);
    long countByActivoTrue();
}
