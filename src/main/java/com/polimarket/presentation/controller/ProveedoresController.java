package com.polimarket.presentation.controller;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polimarket.application.dto.AdquisicionDto;
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.application.dto.RespuestaDTO;
import com.polimarket.application.service.ServicioProveedores;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para el módulo de Proveedores.
 *
 * <p>Expone los endpoints para listar adquisiciones (RF-04)
 * y buscar proveedor por producto (RF-05).</p>
 */
@RestController
@RequestMapping("/api/v1/proveedores")
@Tag(name = "Proveedores", description = "Consulta de adquisiciones e identificación de proveedores")
public class ProveedoresController {

    private final ServicioProveedores servicioProveedores;
    private final MessageSource messageSource;

    public ProveedoresController(ServicioProveedores servicioProveedores,
                                  MessageSource messageSource) {
        this.servicioProveedores = servicioProveedores;
        this.messageSource = messageSource;
    }

    /**
     * RF-04: Lista los productos adquiridos y sus proveedores.
     */
    @Operation(
            summary = "Listar adquisiciones",
            description = "Obtiene el listado de productos adquiridos por PoliMarket con el proveedor que suministró cada uno."
    )
    @GetMapping("/adquisiciones")
    public ResponseEntity<RespuestaDTO<List<AdquisicionDto>>> listarAdquisiciones() {
        List<AdquisicionDto> adquisiciones = servicioProveedores.listarAdquisiciones();
        String mensaje = getMessage("proveedores.adquisiciones.listado");
        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, adquisiciones));
    }

    /**
     * RF-05: Busca el proveedor asociado a un producto.
     */
    @Operation(
            summary = "Buscar proveedor por producto",
            description = "Busca el proveedor asociado a un producto específico en el registro de adquisiciones."
    )
    @GetMapping("/productos/{codigoProducto}")
    public ResponseEntity<RespuestaDTO<ProveedorDto>> buscarProveedorPorProducto(
            @Parameter(description = "Código del producto", example = "PROD-001")
            @PathVariable String codigoProducto) {

        ProveedorDto proveedor = servicioProveedores.buscarProveedorPorProducto(codigoProducto);
        String mensaje = getMessage("proveedores.reposicion.identificado",
                codigoProducto, proveedor.nombre());

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, proveedor));
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
