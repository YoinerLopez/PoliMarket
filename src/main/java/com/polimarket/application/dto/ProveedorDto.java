package com.polimarket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para información de un proveedor.
 */
@Schema(description = "Información del proveedor identificado para reposición")
public record ProveedorDto(
        @Schema(description = "Código del proveedor", example = "PROV-001")
        String codigo,

        @Schema(description = "Nombre del proveedor", example = "TechDistribuciones S.A.")
        String nombre
) {
}
