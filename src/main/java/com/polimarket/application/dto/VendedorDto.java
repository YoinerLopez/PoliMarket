package com.polimarket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para información de un vendedor y sus permisos.
 */
@Schema(description = "Información de un vendedor")
public record VendedorDto(
        @Schema(description = "Nombre de usuario único", example = "jperez")
        String nombreUsuario,

        @Schema(description = "Indica si el vendedor está autorizado", example = "true")
        boolean autorizado,

        @Schema(description = "Rol del usuario", example = "VENDEDOR")
        String rol,

        @Schema(description = "Permisos asociados separados por coma", example = "VENTAS,BODEGA")
        String permisos
) {
    public VendedorDto(String nombreUsuario, boolean autorizado) {
        this(nombreUsuario, autorizado, "VENDEDOR", "VENTAS,BODEGA");
    }
}
