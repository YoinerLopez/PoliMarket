package com.polimarket.domain.exception;

/**
 * Excepción lanzada cuando no hay suficiente stock para completar una operación.
 */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

}
