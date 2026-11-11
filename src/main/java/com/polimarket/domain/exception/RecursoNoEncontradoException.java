package com.polimarket.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

}
