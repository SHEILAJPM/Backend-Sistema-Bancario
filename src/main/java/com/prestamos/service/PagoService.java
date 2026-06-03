package com.prestamos.service;

import com.prestamos.dto.pago.PagoMasivoRequest;
import com.prestamos.dto.pago.PagoRequest;
import com.prestamos.entity.*;
import com.prestamos.entity.enums.EstadoCuota;
import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.exception.BusinessException;
import com.prestamos.exception.ResourceNotFoundException;
import com.prestamos.repository.CuotaRepository;
import com.prestamos.repository.HistorialPagoRepository;
import com.prestamos.repository.PrestamoRepository;
import com.prestamos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final CuotaRepository cuotaRepository;
    private final HistorialPagoRepository historialPagoRepository;
    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public void registrarPago(PagoRequest request) {
        Cuota cuota = cuotaRepository.findById(request.cuotaId())
            .orElseThrow(() -> new ResourceNotFoundException("Cuota no encontrada: " + request.cuotaId()));

        if (cuota.getEstado() == EstadoCuota.PAGADO) {
            throw new BusinessException("La cuota ya fue pagada completamente.");
        }

        BigDecimal saldoPendiente = cuota.getMontoCuota().subtract(cuota.getMontoPagado());
        if (request.montoRecibido().compareTo(saldoPendiente) > 0) {
            throw new BusinessException(
                "El monto recibido (" + request.montoRecibido() +
                ") supera el saldo pendiente (" + saldoPendiente + ")."
            );
        }

        // Obtener el usuario autenticado para auditoría
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cobrador = usuarioRepository.findByUsernameAndActivoTrue(username).orElse(null);

        // Registrar en historial
        historialPagoRepository.save(HistorialPago.builder()
            .cuota(cuota)
            .montoRecibido(request.montoRecibido())
            .registradoPor(cobrador)
            .observacion(request.observacion())
            .build());

        // Actualizar cuota
        BigDecimal nuevoPagado = cuota.getMontoPagado().add(request.montoRecibido());
        cuota.setMontoPagado(nuevoPagado);

        if (nuevoPagado.compareTo(cuota.getMontoCuota()) >= 0) {
            cuota.setEstado(EstadoCuota.PAGADO);
            cuota.setFechaPago(LocalDate.now());
        }
        cuotaRepository.save(cuota);

        log.info("Pago registrado: cuota={} monto={} cobrador={}", cuota.getId(), request.montoRecibido(), username);
        verificarPrestamoCompletado(cuota.getPrestamo());
    }

    @Transactional
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public void registrarPagoMasivo(PagoMasivoRequest request) {
        request.pagos().forEach(this::registrarPago);
    }

    private void verificarPrestamoCompletado(Prestamo prestamo) {
        boolean todasPagadas = prestamo.getCuotas().stream()
            .allMatch(c -> c.getEstado() == EstadoCuota.PAGADO);

        if (todasPagadas) {
            prestamo.setEstado(EstadoPrestamo.PAGADO);
            prestamoRepository.save(prestamo);
            log.info("Préstamo {} marcado como PAGADO - todas las cuotas completadas", prestamo.getId());
        }
    }
}
