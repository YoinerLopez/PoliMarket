package com.polimarket.application.service;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polimarket.application.dto.AdquisicionDto;
import com.polimarket.application.dto.ProductoDisponibleDto;
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.ReglaNegocioException;
import com.polimarket.domain.model.Existencia;
import com.polimarket.domain.model.Proveedor;
import com.polimarket.domain.model.RegistroAdquisicion;
import com.polimarket.domain.repository.AdquisicionRepository;
import com.polimarket.domain.repository.ExistenciaRepository;

/**
 * Servicio de Bodega.
 *
 * <p>Gestiona la consulta de existencias disponibles, productos adquiridos
 * e identificación de proveedores para reposición (RF-03, RF-04, RF-05).</p>
 */
@Service
@Transactional(readOnly = true)
public class ServicioBodega {

    private final ExistenciaRepository existenciaRepository;
    private final AdquisicionRepository adquisicionRepository;
    private final MessageSource messageSource;

    public ServicioBodega(ExistenciaRepository existenciaRepository,
                          AdquisicionRepository adquisicionRepository,
                          MessageSource messageSource) {
        this.existenciaRepository = existenciaRepository;
        this.adquisicionRepository = adquisicionRepository;
        this.messageSource = messageSource;
    }

    /**
     * Consulta todas las existencias disponibles en bodega (productos con stock > 0).
     *
     * @return lista de productos disponibles con su cantidad
     */
    public List<ProductoDisponibleDto> consultarExistenciasDisponibles() {
        return existenciaRepository.listar().stream()
                .filter(Existencia::estaDisponible)
                .map(e -> new ProductoDisponibleDto(
                        e.getProducto().getCodigo(),
                        e.getProducto().consultarNombre(),
                        e.consultarCantidadDisponible()))
                .toList();
    }

    /**
     * Consulta todas las existencias en bodega (incluyendo productos agotados con cantidad 0)
     * para el panel de gestión de bodega e inventario.
     *
     * @return lista completa de existencias
     */
    public List<ProductoDisponibleDto> consultarTodasLasExistencias() {
        return existenciaRepository.listar().stream()
                .map(e -> new ProductoDisponibleDto(
                        e.getProducto().getCodigo(),
                        e.getProducto().consultarNombre(),
                        e.consultarCantidadDisponible()))
                .toList();
    }

    /**
     * RF-04: Consulta los productos adquiridos por PoliMarket y sus proveedores.
     *
     * <p>Bodega consulta al sistema de Proveedores para obtener el listado.</p>
     *
     * @return lista de adquisiciones con información del proveedor
     */
    public List<AdquisicionDto> consultarProductosAdquiridos() {
        List<RegistroAdquisicion> adquisiciones = adquisicionRepository.listar();
        return adquisiciones.stream()
                .map(a -> new AdquisicionDto(
                        a.getProducto().getCodigo(),
                        a.getProducto().consultarNombre(),
                        a.getProveedor().getCodigo(),
                        a.getProveedor().getNombre()))
                .toList();
    }

    /**
     * RF-04 Paginado: Consulta productos adquiridos con paginación para consultas históricas masivas.
     *
     * @param pagina número de página (base 0)
     * @param tamano cantidad de elementos por página (ej. 495 o 15)
     * @return estructura paginada con DTOs de adquisición
     */
    public com.polimarket.application.dto.PaginaDTO<AdquisicionDto> consultarProductosAdquiridosPaginado(int pagina, int tamano) {
        List<RegistroAdquisicion> registros = adquisicionRepository.listarPaginado(pagina, tamano);
        long total = adquisicionRepository.contarTotal();

        List<AdquisicionDto> dtos = registros.stream()
                .map(a -> new AdquisicionDto(
                        a.getProducto().getCodigo(),
                        a.getProducto().consultarNombre(),
                        a.getProveedor().getCodigo(),
                        a.getProveedor().getNombre()))
                .toList();

        return com.polimarket.application.dto.PaginaDTO.de(dtos, pagina, tamano, total);
    }

    /**
     * RF-05: Identifica el proveedor al que se puede solicitar la reposición
     * de un producto agotado (existencias = 0).
     *
     * @param codigoProducto código del producto agotado
     * @return DTO del proveedor identificado para reposición
     * @throws RecursoNoEncontradoException si el producto o su registro de adquisición no existe
     * @throws ReglaNegocioException si el producto no está agotado
     */
    public ProveedorDto identificarProveedorReposicion(String codigoProducto) {
        Existencia existencia = existenciaRepository.buscarPorCodigoProducto(codigoProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("bodega.producto.no.encontrado", codigoProducto)));

        if (existencia.estaDisponible()) {
            throw new ReglaNegocioException(
                    getMessage("proveedores.reposicion.no.agotado",
                            existencia.getProducto().consultarNombre()));
        }

        RegistroAdquisicion adquisicion = adquisicionRepository
                .buscarPorCodigoProducto(codigoProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("proveedores.reposicion.sin.proveedor",
                                existencia.getProducto().consultarNombre())));

        Proveedor proveedor = adquisicion.consultarProveedor();
        return new ProveedorDto(proveedor.getCodigo(), proveedor.getNombre());
    }

    /**
     * RF-07 (Opcional): Registra la salida de productos de bodega.
     *
     * @param codigoProducto código del producto a retirar
     * @param cantidad cantidad de unidades a descontar
     * @throws RecursoNoEncontradoException si el producto no existe
     */
    @Transactional
    public void registrarSalidaProducto(String codigoProducto, int cantidad) {
        Existencia existencia = existenciaRepository.buscarPorCodigoProducto(codigoProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("bodega.producto.no.encontrado", codigoProducto)));

        existencia.descontar(cantidad);
        existenciaRepository.guardar(existencia);
    }

    /**
     * Registra la entrada / recarga de inventario para un producto (reposición de stock).
     *
     * @param codigoProducto código del producto a recargar
     * @param cantidad cantidad de unidades que ingresan
     * @throws RecursoNoEncontradoException si el producto no existe
     */
    @Transactional
    public void recargarStockProducto(String codigoProducto, int cantidad) {
        Existencia existencia = existenciaRepository.buscarPorCodigoProducto(codigoProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("bodega.producto.no.encontrado", codigoProducto)));

        existencia.ingresarStock(cantidad);
        existenciaRepository.guardar(existencia);
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
