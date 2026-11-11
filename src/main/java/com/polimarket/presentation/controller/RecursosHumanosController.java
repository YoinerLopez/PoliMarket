package com.polimarket.presentation.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polimarket.application.dto.RespuestaDTO;
import com.polimarket.application.dto.VendedorDto;
import com.polimarket.application.service.ServicioRecursosHumanos;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para el módulo de Recursos Humanos.
 *
 * <p>Expone los endpoints para la autorización y revocación de vendedores (RF-01).</p>
 */
@RestController
@RequestMapping("/api/v1/recursos-humanos")
@Tag(name = "Recursos Humanos", description = "Gestión de autorización y estado de vendedores")
public class RecursosHumanosController {

    private final ServicioRecursosHumanos servicioRRHH;
    private final MessageSource messageSource;

    public RecursosHumanosController(ServicioRecursosHumanos servicioRRHH,
                                      MessageSource messageSource) {
        this.servicioRRHH = servicioRRHH;
        this.messageSource = messageSource;
    }

    /**
     * Lista todos los vendedores y usuarios del sistema con sus roles y estados.
     */
    @Operation(
            summary = "Listar vendedores",
            description = "Retorna el listado completo de vendedores y usuarios registrados con sus roles y permisos."
    )
    @org.springframework.web.bind.annotation.GetMapping("/vendedores")
    public ResponseEntity<RespuestaDTO<java.util.List<VendedorDto>>> listarVendedores() {
        java.util.List<VendedorDto> lista = servicioRRHH.listarVendedores();
        String mensaje = getMessage("rrhh.vendedores.listado");
        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, lista));
    }

    /**
     * Autoriza a un vendedor registrado.
     */
    @Operation(
            summary = "Autorizar vendedor",
            description = "Autoriza a un vendedor registrado para interactuar con los sistemas de PoliMarket. "
                    + "Cambia su estado de pendiente a autorizado e invalida la caché en memoria."
    )
    @PatchMapping("/vendedores/{nombreUsuario}/autorizar")
    public ResponseEntity<RespuestaDTO<VendedorDto>> autorizarVendedor(
            @Parameter(description = "Nombre de usuario del vendedor", example = "jperez")
            @PathVariable String nombreUsuario) {

        VendedorDto vendedor = servicioRRHH.autorizarVendedor(nombreUsuario);
        String mensaje = getMessage("rrhh.vendedor.autorizado", nombreUsuario);

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, vendedor));
    }

    /**
     * Revoca la autorización a un vendedor.
     */
    @Operation(
            summary = "Revocar autorización de vendedor",
            description = "Revoca la autorización a un vendedor y purga su entrada en la caché en memoria, "
                    + "tumbando inmediatamente cualquier acceso a Ventas."
    )
    @PatchMapping("/vendedores/{nombreUsuario}/revocar")
    public ResponseEntity<RespuestaDTO<VendedorDto>> revocarAutorizacion(
            @Parameter(description = "Nombre de usuario del vendedor", example = "acastro")
            @PathVariable String nombreUsuario) {

        VendedorDto vendedor = servicioRRHH.revocarAutorizacion(nombreUsuario);
        String mensaje = getMessage("rrhh.vendedor.revocado", nombreUsuario);

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, vendedor));
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
