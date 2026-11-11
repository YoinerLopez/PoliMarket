package com.polimarket.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Petición de inicio de sesión")
public record LoginRequestDto(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Schema(description = "Nombre de usuario", example = "acastro")
        String nombreUsuario,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contraseña", example = "password123")
        String password
) {
}
