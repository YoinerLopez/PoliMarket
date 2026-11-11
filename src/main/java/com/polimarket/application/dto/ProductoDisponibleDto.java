package com.polimarket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para un producto con su disponibilidad en bodega.
 */
@Schema(description = "Producto disponible con cantidad en stock")
public record ProductoDisponibleDto(
        @Schema(description = "Código del producto", example = "PROD-001")
        String codigoProducto,

        @Schema(description = "Nombre del producto", example = "Laptop HP ProBook")
        String nombreProducto,

        @Schema(description = "Cantidad disponible en bodega", example = "25")
        int cantidadDisponible
) {
}
