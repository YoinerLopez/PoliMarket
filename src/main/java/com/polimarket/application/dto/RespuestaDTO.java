package com.polimarket.application.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO genérico estándar para todas las respuestas de la API PoliMarket.
 *
 * <p>Centraliza el estado de éxito, mensaje descriptivo, los datos devueltos
 * y una lista de errores detallados en caso de fallos.</p>
 *
 * @param <T> Tipo de datos contenidos en la respuesta
 */
@Schema(description = "Estructura estándar de respuesta de la API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaDTO<T>(
        @Schema(description = "Indica si la operación fue exitosa", example = "true")
        boolean exitoso,

        @Schema(description = "Mensaje descriptivo del resultado o acción realizada", example = "Operación realizada con éxito")
        String mensaje,

        @Schema(description = "Datos retornados por el servicio en caso de éxito")
        T datos,

        @Schema(description = "Lista detallada de errores (si ocurrieron)", example = "[]")
        List<String> errores,

        @Schema(description = "Marca de tiempo de la respuesta", example = "2026-11-10T00:25:00")
        LocalDateTime timestamp
) {

    /**
     * Respuesta exitosa con datos y mensaje.
     */
    public static <T> RespuestaDTO<T> exitoso(String mensaje, T datos) {
        return new RespuestaDTO<>(true, mensaje, datos, Collections.emptyList(), LocalDateTime.now());
    }

    /**
     * Respuesta exitosa sin datos adicionales.
     */
    public static <T> RespuestaDTO<T> exitoso(String mensaje) {
        return new RespuestaDTO<>(true, mensaje, null, Collections.emptyList(), LocalDateTime.now());
    }

    /**
     * Respuesta de error con un mensaje principal.
     */
    public static <T> RespuestaDTO<T> error(String mensaje) {
        return new RespuestaDTO<>(false, mensaje, null, List.of(mensaje), LocalDateTime.now());
    }

    /**
     * Respuesta de error con mensaje general y lista detallada de causas/errores.
     */
    public static <T> RespuestaDTO<T> error(String mensaje, List<String> errores) {
        return new RespuestaDTO<>(false, mensaje, null, errores != null ? errores : Collections.emptyList(), LocalDateTime.now());
    }

}
