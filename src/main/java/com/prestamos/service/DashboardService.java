package com.prestamos.service;

import com.prestamos.dto.dashboard.AlertaCobro;
import com.prestamos.dto.dashboard.DashboardMetricasResponse;
import com.prestamos.dto.dashboard.FlujoCajaMesResponse;
import com.prestamos.dto.dashboard.TopClienteResponse;
import com.prestamos.entity.Cuota;
import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.repository.CuotaRepository;
import com.prestamos.repository.HistorialPagoRepository;
import com.prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final PrestamoRepository prestamoRepository;
    private final CuotaRepository cuotaRepository;
    private final HistorialPagoRepository historialPagoRepository;

    @Cacheable(value = "dashboard_metricas")
    public DashboardMetricasResponse obtenerMetricas() {
        LocalDate hoy = LocalDate.now();

        BigDecimal capitalEnLaCalle       = prestamoRepository.sumCapitalActivo();
        BigDecimal interesesPendientes    = prestamoRepository.sumInteresProyectadoPendiente();
        BigDecimal interesesLiquidos      = prestamoRepository.sumInteresLiquido();
        BigDecimal interesProyectadoTotal = prestamoRepository.sumInteresProyectadoTotal();
        BigDecimal totalRecuperado        = prestamoRepository.sumTotalRecuperado();

        // Rendimiento global = (totalRecuperado / capitalEnLaCalle) * 100
        // Evita división por cero si aún no hay préstamos
        BigDecimal rendimiento = BigDecimal.ZERO;
        if (capitalEnLaCalle.compareTo(BigDecimal.ZERO) > 0) {
            rendimiento = totalRecuperado
                .divide(capitalEnLaCalle, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        }

        long activos   = prestamoRepository.countByEstado(EstadoPrestamo.ACTIVO);
        long enMora    = prestamoRepository.countByEstado(EstadoPrestamo.EN_MORA);
        long pagados   = prestamoRepository.countByEstado(EstadoPrestamo.PAGADO);

        int cuotasHoy      = cuotaRepository.findCuotasVencenHoy(hoy).size();
        int cuotasProximas = cuotaRepository.findCuotasProximas(hoy.plusDays(1), hoy.plusDays(7)).size();

        return new DashboardMetricasResponse(
            capitalEnLaCalle,
            interesesPendientes,
            interesesLiquidos,
            interesProyectadoTotal,
            totalRecuperado,
            rendimiento,
            activos,
            enMora,
            pagados,
            cuotasHoy,
            cuotasProximas
        );
    }

    public List<AlertaCobro> obtenerAlertasHoy() {
        return cuotaRepository.findCuotasVencenHoy(LocalDate.now())
            .stream()
            .map(this::toAlerta)
            .toList();
    }

    public List<AlertaCobro> obtenerProximasSemana() {
        LocalDate hoy = LocalDate.now();
        return cuotaRepository.findCuotasProximas(hoy.plusDays(1), hoy.plusDays(7))
            .stream()
            .map(this::toAlerta)
            .toList();
    }

    public long contarAlertasHoy() {
        return cuotaRepository.findCuotasVencenHoy(LocalDate.now()).size();
    }

    public List<TopClienteResponse> getTopClientes(int limit) {
        return prestamoRepository.findTopClientesByCapital(PageRequest.of(0, limit))
            .stream()
            .map(row -> new TopClienteResponse(
                ((Number) row[0]).longValue(),
                row[1] + " " + row[2],
                ((Number) row[3]).intValue(),
                new java.math.BigDecimal(row[4].toString())
            ))
            .toList();
    }

    public List<FlujoCajaMesResponse> getFlujoCaja() {
        return historialPagoRepository.findFlujoPorMes()
            .stream()
            .map(row -> new FlujoCajaMesResponse(
                (String) row[0],
                row[1] == null ? BigDecimal.ZERO : new BigDecimal(row[1].toString())
            ))
            .toList();
    }

    private AlertaCobro toAlerta(Cuota c) {
        var cliente = c.getPrestamo().getCliente();
        return new AlertaCobro(
            c.getId(),
            c.getPrestamo().getId(),
            cliente.getId(),
            cliente.getNombre() + " " + cliente.getApellido(),
            cliente.getTelefono(),
            cliente.getDireccion(),
            c.getNumeroCuota(),
            c.getFechaVencimiento(),
            c.getMontoCuota(),
            c.getMontoPagado(),
            c.getMontoCuota().subtract(c.getMontoPagado()),
            c.getEstado()
        );
    }
}
