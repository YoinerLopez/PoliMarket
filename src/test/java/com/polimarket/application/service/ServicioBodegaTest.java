package com.polimarket.application.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.polimarket.application.dto.AdquisicionDto;
import com.polimarket.application.dto.ProductoDisponibleDto;
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.ReglaNegocioException;
import com.polimarket.domain.model.Existencia;
import com.polimarket.domain.model.Producto;
import com.polimarket.domain.model.Proveedor;
import com.polimarket.domain.model.RegistroAdquisicion;
import com.polimarket.domain.repository.AdquisicionRepository;
import com.polimarket.domain.repository.ExistenciaRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Servicio: Bodega (RF-03, RF-04, RF-05, RF-07)")
class ServicioBodegaTest {

    @Mock
    private ExistenciaRepository existenciaRepository;

    @Mock
    private AdquisicionRepository adquisicionRepository;

    @Mock
    private MessageSource messageSource;

    private ServicioBodega servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioBodega(existenciaRepository, adquisicionRepository, messageSource);
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Mensaje simulado");
    }

    @Test
    @DisplayName("Debe consultar existencias disponibles filtrando los productos con stock 0")
    void debeConsultarExistenciasDisponibles() {
        Producto p1 = new Producto("P1", "Prod 1");
        Producto p2 = new Producto("P2", "Prod 2");

        when(existenciaRepository.listar()).thenReturn(List.of(
                new Existencia(p1, 20),
                new Existencia(p2, 0)
        ));

        List<ProductoDisponibleDto> res = servicio.consultarExistenciasDisponibles();
        assertEquals(1, res.size());

        List<ProductoDisponibleDto> todas = servicio.consultarTodasLasExistencias();
        assertEquals(2, todas.size());
    }

    @Test
    @DisplayName("RF-04: Debe consultar lista de adquisiciones y sus proveedores")
    void debeConsultarProductosAdquiridos() {
        Producto p1 = new Producto("P1", "Laptop");
        Proveedor prov1 = new Proveedor("PR1", "TechDist");
        RegistroAdquisicion reg = new RegistroAdquisicion(1L, p1, prov1);

        when(adquisicionRepository.listar()).thenReturn(List.of(reg));

        List<AdquisicionDto> res = servicio.consultarProductosAdquiridos();

        assertEquals(1, res.size());
        assertEquals("P1", res.get(0).codigoProducto());
        assertEquals("Laptop", res.get(0).nombreProducto());
        assertEquals("PR1", res.get(0).codigoProveedor());
        assertEquals("TechDist", res.get(0).nombreProveedor());
    }

    @Test
    @DisplayName("RF-04: Debe consultar lista de adquisiciones paginada")
    void debeConsultarProductosAdquiridosPaginado() {
        Producto p1 = new Producto("P1", "Laptop");
        Proveedor prov1 = new Proveedor("PR1", "TechDist");
        RegistroAdquisicion reg = new RegistroAdquisicion(1L, p1, prov1);

        when(adquisicionRepository.listarPaginado(0, 15)).thenReturn(List.of(reg));
        when(adquisicionRepository.contarTotal()).thenReturn(1L);

        var pagina = servicio.consultarProductosAdquiridosPaginado(0, 15);

        assertNotNull(pagina);
        assertEquals(1, pagina.contenido().size());
        assertEquals(0, pagina.pagina());
        assertEquals(15, pagina.tamano());
        assertEquals(1L, pagina.totalElementos());
    }

    @Test
    @DisplayName("RF-05 Éxito: Identifica proveedor para reposición de un producto con existencia 0")
    void debeIdentificarProveedorReposicionExitosamente() {
        Producto p3 = new Producto("PROD-003", "Teclado Agotado");
        Existencia agotada = new Existencia(p3, 0);
        Proveedor prov3 = new Proveedor("PROV-003", "ElectroComponentes");
        RegistroAdquisicion reg = new RegistroAdquisicion(3L, p3, prov3);

        when(existenciaRepository.buscarPorCodigoProducto("PROD-003")).thenReturn(Optional.of(agotada));
        when(adquisicionRepository.buscarPorCodigoProducto("PROD-003")).thenReturn(Optional.of(reg));

        ProveedorDto provDto = servicio.identificarProveedorReposicion("PROD-003");

        assertNotNull(provDto);
        assertEquals("PROV-003", provDto.codigo());
        assertEquals("ElectroComponentes", provDto.nombre());
    }

    @Test
    @DisplayName("RF-05 Error: Falla si el producto no existe en existencias")
    void debeFallarReposicionSiProductoNoExiste() {
        when(existenciaRepository.buscarPorCodigoProducto("NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.identificarProveedorReposicion("NOEXISTE"));
    }

    @Test
    @DisplayName("RF-05 Error: Falla si el producto tiene stock > 0 (no está agotado)")
    void debeFallarReposicionSiProductoNoEstaAgotado() {
        Producto p1 = new Producto("PROD-001", "Laptop");
        Existencia conStock = new Existencia(p1, 15);

        when(existenciaRepository.buscarPorCodigoProducto("PROD-001")).thenReturn(Optional.of(conStock));

        assertThrows(ReglaNegocioException.class,
                () -> servicio.identificarProveedorReposicion("PROD-001"));
    }

    @Test
    @DisplayName("RF-05 Error: Falla si el producto agotado no tiene registro de adquisición")
    void debeFallarReposicionSiNoHayRegistroAdquisicion() {
        Producto p = new Producto("PROD-999", "Sin Prov");
        Existencia agotada = new Existencia(p, 0);

        when(existenciaRepository.buscarPorCodigoProducto("PROD-999")).thenReturn(Optional.of(agotada));
        when(adquisicionRepository.buscarPorCodigoProducto("PROD-999")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.identificarProveedorReposicion("PROD-999"));
    }

    @Test
    @DisplayName("RF-07 Éxito: Debe registrar salida de bodega descontando el stock")
    void debeRegistrarSalidaProductoExitosamente() {
        Producto p = new Producto("PROD-001", "Laptop");
        Existencia stock = new Existencia(p, 10);

        when(existenciaRepository.buscarPorCodigoProducto("PROD-001")).thenReturn(Optional.of(stock));

        servicio.registrarSalidaProducto("PROD-001", 3);

        assertEquals(7, stock.consultarCantidadDisponible());
        verify(existenciaRepository).guardar(stock);
    }

    @Test
    @DisplayName("Éxito: Debe recargar existencias al registrar entrada de bodega")
    void debeRecargarStockExitosamente() {
        Producto p = new Producto("PROD-003", "Teclado");
        Existencia stock = new Existencia(p, 0);

        when(existenciaRepository.buscarPorCodigoProducto("PROD-003")).thenReturn(Optional.of(stock));

        servicio.recargarStockProducto("PROD-003", 25);

        assertEquals(25, stock.consultarCantidadDisponible());
        verify(existenciaRepository).guardar(stock);
    }

    @Test
    @DisplayName("RF-07 Error: Falla si el producto para salida no existe")
    void debeFallarSalidaSiProductoNoExiste() {
        when(existenciaRepository.buscarPorCodigoProducto("PROD-404")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.registrarSalidaProducto("PROD-404", 2));
    }
}
