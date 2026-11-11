package com.polimarket.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO genérico para respuestas paginadas de la API.
 *
 * @param <T> tipo de los elementos contenidos en la página
 */
@Schema(description = "Estructura de datos paginada")
public record PaginaDTO<T>(
        @Schema(description = "Elementos de la página actual")
        List<T> contenido,

        @Schema(description = "Número de página actual (base 0)", example = "0")
        int pagina,

        @Schema(description = "Tamaño de página", example = "15")
        int tamano,

        @Schema(description = "Total de elementos registrados", example = "500")
        long totalElementos,

        @Schema(description = "Total de páginas disponibles", example = "34")
        int totalPaginas,

        @Schema(description = "Indica si es la última página", example = "false")
        boolean esUltima
) {

    public static <T> PaginaDTO<T> de(List<T> contenido, int pagina, int tamano, long totalElementos) {
        int totalPaginas = tamano > 0 ? (int) Math.ceil((double) totalElementos / tamano) : 1;
        boolean esUltima = (pagina + 1) >= totalPaginas;
        return new PaginaDTO<>(contenido, pagina, tamano, totalElementos, totalPaginas, esUltima);
    }

}
