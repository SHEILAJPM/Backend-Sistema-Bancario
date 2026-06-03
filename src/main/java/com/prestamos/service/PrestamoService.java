package com.prestamos.service;

import com.prestamos.dto.prestamo.*;
import com.prestamos.entity.*;
import com.prestamos.entity.enums.EstadoCuota;
import com.prestamos.entity.enums.EstadoPrestamo;
import lombok.extern.slf4j.Slf4j;
import com.prestamos.exception.BusinessException;
import com.prestamos.exception.ResourceNotFoundException;
import com.prestamos.repository.AbonoExtraRepository;
import com.prestamos.repository.ClienteRepository;
import com.prestamos.repository.CuotaRepository;
import com.prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final ClienteRepository clienteRepository;
    private final CuotaRepository cuotaRepository;
    private final AbonoExtraRepository abonoExtraRepository;

    public Page<PrestamoResponse> listar(EstadoPrestamo estado, Pageable pageable) {
        if (estado != null) {
            return prestamoRepository.findByEstado(estado, pageable).map(this::toResponse);
        }
        return prestamoRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<PrestamoResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        return prestamoRepository.findByClienteId(clienteId, pageable).map(this::toResponse);
    }

    public PrestamoResponse obtener(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public PrestamoResponse crear(PrestamoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.clienteId()));

        // ─── Cálculo financiero ───────────────────────────────────────────────
        // Interés total = capital * tasa * numCuotas
        BigDecimal interesProyectado = request.capital()
            .multiply(request.tasaInteres())
            .multiply(BigDecimal.valueOf(request.numeroCuotas()))
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal montoTotal = request.capital().add(interesProyectado);

        // Cuota fija = montoTotal / numeroCuotas
        BigDecimal montoCuota = montoTotal
            .divide(BigDecimal.valueOf(request.numeroCuotas()), 2, RoundingMode.HALF_UP);

        Prestamo prestamo = Prestamo.builder()
            .cliente(cliente)
            .capital(request.capital())
            .tasaInteres(request.tasaInteres())
            .interesProyectado(interesProyectado)
            .montoTotal(montoTotal)
            .numeroCuotas(request.numeroCuotas())
            .frecuencia(request.frecuencia())
            .fechaInicio(request.fechaInicio())
            .estado(EstadoPrestamo.ACTIVO)
            .observacion(request.observacion())
            .build();

        // ─── Generación del cronograma de cuotas ─────────────────────────────
        List<Cuota> cuotas = generarCronograma(prestamo, montoCuota, request);
        prestamo.getCuotas().addAll(cuotas);

        Prestamo saved = prestamoRepository.save(prestamo);
        log.info("Préstamo creado: id={} cliente={} capital={} cuotas={}",
            saved.getId(), cliente.getId(), request.capital(), request.numeroCuotas());
        return toResponse(saved);
    }

    public SaldoResponse getSaldo(Long id) {
        Prestamo p = findOrThrow(id);
        int cuotasPendientes = 0;
        BigDecimal saldoTotal = BigDecimal.ZERO;
        for (Cuota c : p.getCuotas()) {
            if (c.getEstado() != EstadoCuota.PAGADO) {
                cuotasPendientes++;
                saldoTotal = saldoTotal.add(c.getMontoCuota().subtract(c.getMontoPagado()));
            }
        }
        BigDecimal saldoCapital = BigDecimal.ZERO;
        BigDecimal saldoInteres = BigDecimal.ZERO;
        if (p.getMontoTotal().compareTo(BigDecimal.ZERO) > 0) {
            saldoCapital = saldoTotal.multiply(p.getCapital())
                .divide(p.getMontoTotal(), 2, RoundingMode.HALF_UP);
            saldoInteres = saldoTotal.subtract(saldoCapital);
        }
        return new SaldoResponse(id, saldoCapital, saldoInteres, saldoTotal, cuotasPendientes);
    }

    @Transactional
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public void cancelar(Long id, CancelacionRequest request) {
        Prestamo p = findOrThrow(id);
        if (p.getEstado().isTerminal()) {
            throw new BusinessException("El préstamo ya está en estado " + p.getEstado());
        }
        p.setEstado(EstadoPrestamo.CANCELADO);
        String nota = "Cancelado: " + request.motivo();
        p.setObservacion(p.getObservacion() != null ? p.getObservacion() + " | " + nota : nota);
        prestamoRepository.save(p);
        log.info("Préstamo {} cancelado. Motivo: {}", id, request.motivo());
    }

    @Transactional
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public PrestamoResponse reestructurar(Long id, ReestructuracionRequest request) {
        Prestamo p = findOrThrow(id);
        if (p.getEstado().isTerminal()) {
            throw new BusinessException("No se puede reestructurar un préstamo en estado " + p.getEstado());
        }

        // Eliminar cuotas no pagadas — deleteAllInBatch garantiza que el DELETE
        // se ejecuta antes del INSERT de las nuevas cuotas (evita conflicto de UK)
        List<Cuota> pendientes = p.getCuotas().stream()
            .filter(c -> c.getEstado() != EstadoCuota.PAGADO)
            .toList();
        p.getCuotas().removeAll(pendientes);
        cuotaRepository.deleteAllInBatch(pendientes);

        // Recalcular términos financieros
        BigDecimal interesProyectado = request.nuevoCapital()
            .multiply(request.nuevaTasa())
            .multiply(BigDecimal.valueOf(request.nuevasCuotas()))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoTotal = request.nuevoCapital().add(interesProyectado);
        BigDecimal montoCuota = montoTotal
            .divide(BigDecimal.valueOf(request.nuevasCuotas()), 2, RoundingMode.HALF_UP);

        p.setCapital(request.nuevoCapital());
        p.setTasaInteres(request.nuevaTasa());
        p.setInteresProyectado(interesProyectado);
        p.setMontoTotal(montoTotal);
        p.setNumeroCuotas(request.nuevasCuotas());
        p.setFrecuencia(request.nuevaFrecuencia());
        p.setFechaInicio(LocalDate.now());
        p.setEstado(EstadoPrestamo.ACTIVO);
        if (request.motivo() != null) {
            String nota = "Reestructurado: " + request.motivo();
            p.setObservacion(p.getObservacion() != null ? p.getObservacion() + " | " + nota : nota);
        }

        // Generar nuevo cronograma
        int diasIntervalo = request.nuevaFrecuencia().getDiasIntervalo();
        BigDecimal totalAcumulado = BigDecimal.ZERO;
        for (int i = 1; i <= request.nuevasCuotas(); i++) {
            LocalDate fecha = LocalDate.now().plusDays((long) diasIntervalo * i);
            BigDecimal monto = montoCuota;
            if (i == request.nuevasCuotas()) {
                monto = montoTotal.subtract(totalAcumulado).max(BigDecimal.ZERO);
            }
            totalAcumulado = totalAcumulado.add(monto);
            p.getCuotas().add(Cuota.builder()
                .prestamo(p)
                .numeroCuota(i)
                .fechaVencimiento(fecha)
                .montoCuota(monto)
                .montoPagado(BigDecimal.ZERO)
                .estado(EstadoCuota.PENDIENTE)
                .build());
        }

        Prestamo saved = prestamoRepository.save(p);
        log.info("Préstamo {} reestructurado: capital={} cuotas={}", id, request.nuevoCapital(), request.nuevasCuotas());
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "dashboard_metricas", allEntries = true)
    public void abonoExtra(Long id, AbonoExtraRequest request) {
        Prestamo p = findOrThrow(id);
        if (p.getEstado().isTerminal()) {
            throw new BusinessException("No se puede aplicar un abono a un préstamo en estado " + p.getEstado());
        }

        BigDecimal montoRestante = request.monto();
        List<Cuota> pendientes = p.getCuotas().stream()
            .filter(c -> c.getEstado() != EstadoCuota.PAGADO)
            .sorted(java.util.Comparator.comparing(Cuota::getNumeroCuota))
            .toList();

        if (pendientes.isEmpty()) {
            throw new BusinessException("El préstamo no tiene cuotas pendientes");
        }
        BigDecimal saldoTotal = pendientes.stream()
            .map(c -> c.getMontoCuota().subtract(c.getMontoPagado()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (montoRestante.compareTo(saldoTotal) > 0) {
            throw new BusinessException("El abono (" + montoRestante + ") supera el saldo total pendiente (" + saldoTotal + ")");
        }

        for (Cuota cuota : pendientes) {
            if (montoRestante.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal saldo = cuota.getMontoCuota().subtract(cuota.getMontoPagado());
            BigDecimal aplicar = montoRestante.min(saldo);
            cuota.setMontoPagado(cuota.getMontoPagado().add(aplicar));
            montoRestante = montoRestante.subtract(aplicar);
            if (cuota.getMontoPagado().compareTo(cuota.getMontoCuota()) >= 0) {
                cuota.setEstado(EstadoCuota.PAGADO);
                cuota.setFechaPago(LocalDate.now());
            }
            cuotaRepository.save(cuota);
        }

        abonoExtraRepository.save(AbonoExtra.builder()
            .prestamo(p)
            .monto(request.monto())
            .observacion(request.observacion())
            .build());

        boolean todasPagadas = p.getCuotas().stream().allMatch(c -> c.getEstado() == EstadoCuota.PAGADO);
        if (todasPagadas) {
            p.setEstado(EstadoPrestamo.PAGADO);
            prestamoRepository.save(p);
        }
        log.info("Abono extra de {} aplicado al préstamo {}", request.monto(), id);
    }

    public PrestamoEstadisticasResponse getEstadisticas(Long id) {
        Prestamo p = findOrThrow(id);
        List<Cuota> cuotas = p.getCuotas();
        int total    = cuotas.size();
        int pagadas  = (int) cuotas.stream().filter(c -> c.getEstado() == EstadoCuota.PAGADO).count();
        int vencidas = (int) cuotas.stream().filter(c -> c.getEstado() == EstadoCuota.VENCIDO).count();
        int pendientes = total - pagadas - vencidas;

        BigDecimal totalPagado = cuotas.stream()
            .map(Cuota::getMontoPagado)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoPendiente = cuotas.stream()
            .filter(c -> c.getEstado() != EstadoCuota.PAGADO)
            .map(c -> c.getMontoCuota().subtract(c.getMontoPagado()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        double pct = total == 0 ? 0.0
            : Math.round((pagadas * 100.0 / total) * 100.0) / 100.0;

        return new PrestamoEstadisticasResponse(
            id, total, pagadas, pendientes, vencidas,
            p.getCapital(), totalPagado, saldoPendiente, pct);
    }

    /**
     * Genera el cronograma completo de cuotas para un préstamo.
     * La última cuota absorbe el redondeo acumulado para cuadrar centavos.
     */
    private List<Cuota> generarCronograma(Prestamo prestamo, BigDecimal montoCuota,
                                           PrestamoRequest req) {
        List<Cuota> cuotas = new ArrayList<>();
        int diasIntervalo = req.frecuencia().getDiasIntervalo();
        LocalDate fechaActual = req.fechaInicio();

        BigDecimal totalAcumulado = BigDecimal.ZERO;

        for (int i = 1; i <= req.numeroCuotas(); i++) {
            fechaActual = req.fechaInicio().plusDays((long) diasIntervalo * i);

            BigDecimal monto = montoCuota;

            // La última cuota ajusta el redondeo acumulado
            if (i == req.numeroCuotas()) {
                BigDecimal montoFinal = prestamo.getMontoTotal().subtract(totalAcumulado);
                monto = montoFinal.max(BigDecimal.ZERO);
            }

            totalAcumulado = totalAcumulado.add(monto);

            cuotas.add(Cuota.builder()
                .prestamo(prestamo)
                .numeroCuota(i)
                .fechaVencimiento(fechaActual)
                .montoCuota(monto)
                .montoPagado(BigDecimal.ZERO)
                .estado(EstadoCuota.PENDIENTE)
                .build());
        }
        return cuotas;
    }

    private Prestamo findOrThrow(Long id) {
        return prestamoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado: " + id));
    }

    private PrestamoResponse toResponse(Prestamo p) {
        List<CuotaResumenResponse> cuotasDto = p.getCuotas().stream()
            .map(c -> new CuotaResumenResponse(
                c.getId(),
                c.getNumeroCuota(),
                c.getFechaVencimiento(),
                c.getMontoCuota(),
                c.getMontoPagado(),
                c.getMontoCuota().subtract(c.getMontoPagado()),
                c.getEstado(),
                c.getFechaPago()
            ))
            .toList();

        return new PrestamoResponse(
            p.getId(),
            p.getCliente().getId(),
            p.getCliente().getNombre() + " " + p.getCliente().getApellido(),
            p.getCapital(),
            p.getTasaInteres(),
            p.getInteresProyectado(),
            p.getMontoTotal(),
            p.getNumeroCuotas(),
            p.getFrecuencia(),
            p.getFechaInicio(),
            p.getEstado(),
            p.getObservacion(),
            p.getCreatedAt(),
            cuotasDto
        );
    }
}
