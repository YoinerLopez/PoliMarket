package com.polimarket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de la sesión autenticada con roles y permisos")
public record SesionUsuarioDto(
        @Schema(description = "Nombre de usuario", example = "acastro")
        String nombreUsuario,

        @Schema(description = "Indica si el usuario tiene autorización activa", example = "true")
        boolean autorizado,

        @Schema(description = "Rol o tipo de usuario", example = "VENDEDOR")
        String rol,

        @Schema(description = "Permisos asociados separados por coma", example = "VENTAS,BODEGA")
        String permisos
) {
    public SesionUsuarioDto(String nombreUsuario, boolean autorizado, String rol) {
        this(nombreUsuario, autorizado, rol, "RECURSOS_HUMANOS".equalsIgnoreCase(rol) ? "GESTION_RRHH,CONSULTA_GENERAL" : "VENTAS,BODEGA");
    }
}
