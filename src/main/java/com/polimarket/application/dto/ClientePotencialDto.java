package com.polimarket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para información de un cliente potencial con datos de contacto.
 */
@Schema(description = "Información detallada de un cliente potencial")
public record ClientePotencialDto(
        @Schema(description = "Identificador del cliente", example = "1")
        Long id,

        @Schema(description = "Nombre o razón social del cliente", example = "Empresa ABC S.A.S.")
        String nombre,

        @Schema(description = "Correo electrónico de contacto", example = "ventas@empresaabc.com")
        String correo,

        @Schema(description = "Número telefónico de contacto", example = "+57 310 555 1234")
        String telefono,

        @Schema(description = "Ciudad de ubicación", example = "Bogotá D.C.")
        String ciudad
) {
}
