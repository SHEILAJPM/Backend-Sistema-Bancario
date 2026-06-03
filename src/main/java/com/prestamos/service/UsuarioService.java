package com.prestamos.service;

import com.prestamos.dto.auth.ChangePasswordRequest;
import com.prestamos.dto.usuario.UsuarioRequest;
import com.prestamos.dto.usuario.UsuarioResponse;
import com.prestamos.entity.Usuario;
import com.prestamos.entity.enums.Rol;
import com.prestamos.exception.BusinessException;
import com.prestamos.exception.ConflictException;
import com.prestamos.exception.ResourceNotFoundException;
import com.prestamos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ConflictException("El username '" + request.username() + "' ya está en uso");
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ConflictException("El email '" + request.email() + "' ya está registrado");
        }
        Usuario usuario = Usuario.builder()
            .username(request.username())
            .passwordHash(passwordEncoder.encode(request.password()))
            .email(request.email())
            .rol(request.rol())
            .activo(true)
            .build();
        Usuario saved = usuarioRepository.save(usuario);
        log.info("Usuario creado: {} con rol {}", saved.getUsername(), saved.getRol());
        return toResponse(saved);
    }

    @Transactional
    public UsuarioResponse toggleActivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        if (usuario.getRol() == Rol.ADMIN) {
            throw new BusinessException("No se puede desactivar una cuenta de administrador");
        }
        usuario.setActivo(!usuario.isActivo());
        log.info("Usuario {} -> activo={}", usuario.getUsername(), usuario.isActivo());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void cambiarPassword(Long id, ChangePasswordRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPasswordHash())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.passwordNueva()));
        usuarioRepository.save(usuario);
        log.info("Password actualizado para usuario: {}", usuario.getUsername());
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
            u.getId(), u.getUsername(), u.getEmail(),
            u.getRol(), u.isActivo(), u.getCreatedAt()
        );
    }
}