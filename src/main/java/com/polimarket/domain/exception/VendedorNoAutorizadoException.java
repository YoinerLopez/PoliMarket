package com.polimarket.domain.exception;

/**
 * Excepción lanzada cuando un vendedor no está autorizado para realizar una operación.
 */
public class VendedorNoAutorizadoException extends RuntimeException {

    public VendedorNoAutorizadoException(String mensaje) {
        super(mensaje);
    }

}
