package com.polimarket.domain.exception;

/**
 * Excepción lanzada cuando una regla de negocio es violada.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

}
