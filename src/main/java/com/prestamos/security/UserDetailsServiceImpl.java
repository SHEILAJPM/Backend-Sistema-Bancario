package com.prestamos.security;

import com.prestamos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
            .orElseThrow(() -> {
                log.warn("Intento de login fallido para username: {}", username);
                return new UsernameNotFoundException("Usuario no encontrado o inactivo: " + username);
            });

        return new User(
            usuario.getUsername(),
            usuario.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
}
