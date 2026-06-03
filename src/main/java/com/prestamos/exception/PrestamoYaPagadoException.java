package com.prestamos.exception;
public class PrestamoYaPagadoException extends BusinessException {
    public PrestamoYaPagadoException(Long prestamoId) {
        super("El préstamo " + prestamoId + " ya se encuentra en estado PAGADO");
    }
}
