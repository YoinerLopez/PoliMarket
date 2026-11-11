package com.polimarket.presentation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.polimarket.application.dto.RespuestaDTO;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.ReglaNegocioException;
import com.polimarket.domain.exception.StockInsuficienteException;
import com.polimarket.domain.exception.VendedorNoAutorizadoException;

/**
 * Manejador global de excepciones para la API REST.
 *
 * <p>Centraliza el manejo de errores, garantizando respuestas consistentes
 * y mensajes internacionalizados con el formato RespuestaDTO.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RespuestaDTO.error(ex.getMessage()));
    }

    @ExceptionHandler(VendedorNoAutorizadoException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleVendedorNoAutorizado(VendedorNoAutorizadoException ex) {
        log.warn("Vendedor no autorizado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaDTO.error(ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleReglaNegocio(ReglaNegocioException ex) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RespuestaDTO.error(ex.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleStockInsuficiente(StockInsuficienteException ex) {
        log.warn("Stock insuficiente: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(RespuestaDTO.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespuestaDTO.error(ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        String mensaje = getMessage("error.parametro.requerido", ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespuestaDTO.error(mensaje));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RespuestaDTO<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String mensaje = getMessage("error.metodo.no.soportado", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(RespuestaDTO.error(mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaDTO<Void>> handleGeneral(Exception ex) {
        log.error("Error inesperado: ", ex);
        String mensaje = getMessage("error.general");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaDTO.error(mensaje));
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
