package com.prestamos.service;

import com.prestamos.dto.cliente.ClienteEstadisticasResponse;
import com.prestamos.dto.cliente.ClienteRequest;
import com.prestamos.dto.cliente.ClienteResponse;
import com.prestamos.entity.Cliente;
import com.prestamos.entity.enums.EstadoPrestamo;
import com.prestamos.exception.ConflictException;
import com.prestamos.exception.ResourceNotFoundException;
import com.prestamos.repository.ClienteRepository;
import com.prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PrestamoRepository prestamoRepository;

    public Page<ClienteResponse> listar(String q, Pageable pageable) {
        Page<Cliente> page = (q == null || q.isBlank())
            ? clienteRepository.findByActivoTrue(pageable)
            : clienteRepository.buscar(q.trim(), pageable);
        return page.map(this::toResponse);
    }

    public ClienteResponse obtener(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByDni(request.dni())) {
            throw new ConflictException("Ya existe un cliente con DNI: " + request.dni());
        }
        Cliente cliente = Cliente.builder()
            .nombre(request.nombre())
            .apellido(request.apellido())
            .dni(request.dni())
            .telefono(request.telefono())
            .direccion(request.direccion())
            .email(request.email())
            .build();
        Cliente saved = clienteRepository.save(cliente);
        log.info("Cliente creado: id={} dni={}", saved.getId(), saved.getDni());
        return toResponse(saved);
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = findOrThrow(id);
        if (!cliente.getDni().equals(request.dni()) && clienteRepository.existsByDni(request.dni())) {
            throw new ConflictException("Ya existe otro cliente con DNI: " + request.dni());
        }
        cliente.setNombre(request.nombre());
        cliente.setApellido(request.apellido());
        cliente.setDni(request.dni());
        cliente.setTelefono(request.telefono());
        cliente.setDireccion(request.direccion());
        cliente.setEmail(request.email());
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = findOrThrow(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
        log.info("Cliente desactivado: id={}", id);
    }

    public long contarClientes() {
        return clienteRepository.countByActivoTrue();
    }

    public ClienteEstadisticasResponse getEstadisticas(Long id) {
        Cliente cliente = findOrThrow(id);
        java.math.BigDecimal capitalTotal = java.math.BigDecimal.ZERO;
        java.math.BigDecimal saldoPendiente = java.math.BigDecimal.ZERO;
        int activos = 0, pagados = 0, enMora = 0;

        for (com.prestamos.entity.Prestamo p : cliente.getPrestamos()) {
            capitalTotal = capitalTotal.add(p.getCapital());
            if (p.getEstado() == EstadoPrestamo.ACTIVO)    activos++;
            else if (p.getEstado() == EstadoPrestamo.PAGADO)  pagados++;
            else if (p.getEstado() == EstadoPrestamo.EN_MORA) enMora++;
            saldoPendiente = saldoPendiente.add(
                p.getCuotas().stream()
                    .filter(c -> c.getEstado() != com.prestamos.entity.enums.EstadoCuota.PAGADO)
                    .map(c -> c.getMontoCuota().subtract(c.getMontoPagado()))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );
        }
        return new ClienteEstadisticasResponse(
            cliente.getId(),
            cliente.getNombre() + " " + cliente.getApellido(),
            cliente.getPrestamos().size(),
            activos, pagados, enMora,
            capitalTotal, saldoPendiente
        );
    }

    private Cliente findOrThrow(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
    }

    private ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
            c.getId(),
            c.getNombre(),
            c.getApellido(),
            c.getNombre() + " " + c.getApellido(),
            c.getDni(),
            c.getTelefono(),
            c.getDireccion(),
            c.getEmail(),
            c.isActivo(),
            c.getCreatedAt(),
            c.getPrestamos().size()
        );
    }
}
