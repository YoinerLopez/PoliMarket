package com.polimarket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para un registro de adquisición (producto + proveedor).
 */
@Schema(description = "Registro de adquisición de un producto con su proveedor")
public record AdquisicionDto(
        @Schema(description = "Código del producto", example = "PROD-001")
        String codigoProducto,

        @Schema(description = "Nombre del producto", example = "Laptop HP ProBook")
        String nombreProducto,

        @Schema(description = "Código del proveedor", example = "PROV-001")
        String codigoProveedor,

        @Schema(description = "Nombre del proveedor", example = "TechDistribuciones S.A.")
        String nombreProveedor
) {
}
