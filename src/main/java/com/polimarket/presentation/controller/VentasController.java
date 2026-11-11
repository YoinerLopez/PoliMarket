package com.polimarket.presentation.controller;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polimarket.application.dto.ClientePotencialDto;
import com.polimarket.application.dto.ProductoDisponibleDto;
import com.polimarket.application.dto.RespuestaDTO;
import com.polimarket.application.service.ServicioVentas;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para el módulo de Ventas.
 *
 * <p>Expone los endpoints para consultar clientes potenciales (RF-02)
 * y productos disponibles (RF-03).</p>
 */
@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Consulta de clientes potenciales y productos disponibles")
public class VentasController {

    private final ServicioVentas servicioVentas;
    private final MessageSource messageSource;

    public VentasController(ServicioVentas servicioVentas,
                             MessageSource messageSource) {
        this.servicioVentas = servicioVentas;
        this.messageSource = messageSource;
    }

    /**
     * RF-02: Consulta los posibles clientes.
     */
    @Operation(
            summary = "Consultar clientes potenciales",
            description = "Obtiene el listado de posibles clientes que el vendedor autorizado puede contactar."
    )
    @GetMapping("/clientes-potenciales")
    public ResponseEntity<RespuestaDTO<List<ClientePotencialDto>>> consultarClientesPotenciales(
            @Parameter(description = "Nombre de usuario del vendedor autorizado", example = "jperez")
            @RequestParam String nombreUsuario) {

        List<ClientePotencialDto> clientes = servicioVentas.consultarClientesPotenciales(nombreUsuario);
        String mensaje = getMessage("ventas.clientes.listado");

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, clientes));
    }

    /**
     * RF-03: Consulta los productos disponibles.
     */
    @Operation(
            summary = "Consultar productos disponibles",
            description = "Obtiene los productos con existencias mayores a cero que el vendedor puede ofrecer. "
                    + "Ventas consulta las existencias en Bodega."
    )
    @GetMapping("/productos-disponibles")
    public ResponseEntity<RespuestaDTO<List<ProductoDisponibleDto>>> consultarProductosDisponibles(
            @Parameter(description = "Nombre de usuario del vendedor autorizado", example = "jperez")
            @RequestParam String nombreUsuario) {

        List<ProductoDisponibleDto> productos = servicioVentas.consultarProductosDisponibles(nombreUsuario);
        String mensaje = getMessage("ventas.productos.listado");

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, productos));
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
