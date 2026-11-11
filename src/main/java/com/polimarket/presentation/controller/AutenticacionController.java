package com.polimarket.presentation.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polimarket.application.dto.LoginRequestDto;
import com.polimarket.application.dto.RespuestaDTO;
import com.polimarket.application.dto.SesionUsuarioDto;
import com.polimarket.application.service.ServicioAutenticacion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Inicio de sesión y validación de credenciales")
public class AutenticacionController {

    private final ServicioAutenticacion servicioAutenticacion;
    private final MessageSource messageSource;

    public AutenticacionController(ServicioAutenticacion servicioAutenticacion, MessageSource messageSource) {
        this.servicioAutenticacion = servicioAutenticacion;
        this.messageSource = messageSource;
    }

    @Operation(summary = "Iniciar sesión", description = "Valida el usuario, contraseña y estado de autorización.")
    @PostMapping("/login")
    public ResponseEntity<RespuestaDTO<SesionUsuarioDto>> login(@Valid @RequestBody LoginRequestDto request) {
        SesionUsuarioDto sesion = servicioAutenticacion.autenticar(request);
        String mensaje = messageSource.getMessage("auth.login.exitoso", new Object[]{sesion.nombreUsuario()}, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, sesion));
    }

}
