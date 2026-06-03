package com.prestamos.exception;
public class ClienteConPrestamosActivosException extends BusinessException {
    public ClienteConPrestamosActivosException(Long clienteId) {
        super("El cliente " + clienteId + " tiene préstamos activos y no puede eliminarse");
    }
}
