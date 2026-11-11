package com.polimarket.presentation.controller;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polimarket.application.dto.AdquisicionDto;
import com.polimarket.application.dto.ProductoDisponibleDto;
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.application.dto.RespuestaDTO;
import com.polimarket.application.service.ServicioBodega;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para el módulo de Bodega.
 *
 * <p>Expone los endpoints para consultar existencias, productos adquiridos,
 * identificar proveedores para reposición y registrar salidas
 * (RF-03, RF-04, RF-05, RF-07).</p>
 */
@RestController
@RequestMapping("/api/v1/bodega")
@Tag(name = "Bodega", description = "Gestión de existencias, adquisiciones y reposición de productos")
public class BodegaController {

    private final ServicioBodega servicioBodega;
    private final MessageSource messageSource;

    public BodegaController(ServicioBodega servicioBodega,
                             MessageSource messageSource) {
        this.servicioBodega = servicioBodega;
        this.messageSource = messageSource;
    }

    /**
     * Consulta existencias en bodega (opcionalmente incluye productos con stock 0).
     */
    @Operation(
            summary = "Consultar existencias disponibles o totales",
            description = "Obtiene las existencias en bodega. Con el parámetro incluirAgotados=true incluye también los productos en 0."
    )
    @GetMapping("/existencias")
    public ResponseEntity<RespuestaDTO<List<ProductoDisponibleDto>>> consultarExistencias(
            @RequestParam(defaultValue = "false") boolean incluirAgotados) {
        List<ProductoDisponibleDto> existencias = incluirAgotados
                ? servicioBodega.consultarTodasLasExistencias()
                : servicioBodega.consultarExistenciasDisponibles();
        String mensaje = getMessage("bodega.existencias.listado");
        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, existencias));
    }

    /**
     * RF-04: Consulta los productos adquiridos y sus proveedores (soporta paginación opcional).
     */
    @Operation(
            summary = "Consultar productos adquiridos (Paginado)",
            description = "Obtiene el listado histórico de adquisiciones de PoliMarket paginado. "
                    + "Por defecto devuelve página 0 con 15 elementos, o se puede consultar bloques mayores."
    )
    @GetMapping("/adquisiciones")
    public ResponseEntity<RespuestaDTO<com.polimarket.application.dto.PaginaDTO<AdquisicionDto>>> consultarAdquisiciones(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "15") int tamano) {

        com.polimarket.application.dto.PaginaDTO<AdquisicionDto> adquisiciones =
                servicioBodega.consultarProductosAdquiridosPaginado(pagina, tamano);
        String mensaje = getMessage("proveedores.adquisiciones.listado");
        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, adquisiciones));
    }

    /**
     * RF-05: Identifica el proveedor para reposición de un producto agotado.
     */
    @Operation(
            summary = "Identificar proveedor para reposición",
            description = "Identifica al proveedor al que se puede solicitar la reposición de un producto agotado "
                    + "(existencias = 0)."
    )
    @GetMapping("/productos/{codigoProducto}/proveedor-reposicion")
    public ResponseEntity<RespuestaDTO<ProveedorDto>> identificarProveedorReposicion(
            @Parameter(description = "Código del producto agotado", example = "PROD-003")
            @PathVariable String codigoProducto) {

        ProveedorDto proveedor = servicioBodega.identificarProveedorReposicion(codigoProducto);
        String mensaje = getMessage("proveedores.reposicion.identificado",
                codigoProducto, proveedor.nombre());

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje, proveedor));
    }

    /**
     * RF-07 (Opcional): Registra la salida de productos de bodega.
     */
    @Operation(
            summary = "Registrar salida de productos",
            description = "Registra la salida de productos de bodega, descontando las unidades del inventario."
    )
    @PostMapping("/productos/{codigoProducto}/salida")
    public ResponseEntity<RespuestaDTO<Void>> registrarSalida(
            @Parameter(description = "Código del producto", example = "PROD-001")
            @PathVariable String codigoProducto,
            @Parameter(description = "Cantidad de unidades a retirar", example = "5")
            @RequestParam int cantidad) {

        servicioBodega.registrarSalidaProducto(codigoProducto, cantidad);
        String mensaje = getMessage("bodega.salida.registrada", cantidad, codigoProducto);

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje));
    }

    /**
     * Registra la entrada / recarga de inventario para un producto.
     */
    @Operation(
            summary = "Registrar entrada de productos (Recarga de inventario)",
            description = "Registra la entrada de productos a bodega, incrementando las existencias en inventario."
    )
    @PostMapping("/productos/{codigoProducto}/entrada")
    public ResponseEntity<RespuestaDTO<Void>> registrarEntrada(
            @Parameter(description = "Código del producto", example = "PROD-003")
            @PathVariable String codigoProducto,
            @Parameter(description = "Cantidad de unidades que ingresan", example = "20")
            @RequestParam int cantidad) {

        servicioBodega.recargarStockProducto(codigoProducto, cantidad);
        String mensaje = getMessage("bodega.entrada.registrada", cantidad, codigoProducto);

        return ResponseEntity.ok(RespuestaDTO.exitoso(mensaje));
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
