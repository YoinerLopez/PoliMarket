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
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.model.Producto;
import com.polimarket.domain.model.Proveedor;
import com.polimarket.domain.model.RegistroAdquisicion;
import com.polimarket.domain.repository.AdquisicionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Servicio: Proveedores (RF-04 y RF-05)")
class ServicioProveedoresTest {

    @Mock
    private AdquisicionRepository adquisicionRepository;

    @Mock
    private MessageSource messageSource;

    private ServicioProveedores servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioProveedores(adquisicionRepository, messageSource);
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Mensaje simulado");
    }

    @Test
    @DisplayName("Debe listar todas las adquisiciones")
    void debeListarAdquisiciones() {
        Producto prod = new Producto("P1", "Laptop");
        Proveedor prov = new Proveedor("PR1", "TechDist");
        RegistroAdquisicion reg = new RegistroAdquisicion(1L, prod, prov);

        when(adquisicionRepository.listar()).thenReturn(List.of(reg));

        List<AdquisicionDto> res = servicio.listarAdquisiciones();

        assertEquals(1, res.size());
        assertEquals("P1", res.get(0).codigoProducto());
        assertEquals("PR1", res.get(0).codigoProveedor());
    }

    @Test
    @DisplayName("Debe buscar proveedor por producto exitosamente")
    void debeBuscarProveedorPorProductoExitoso() {
        Producto prod = new Producto("P1", "Laptop");
        Proveedor prov = new Proveedor("PR1", "TechDist");
        RegistroAdquisicion reg = new RegistroAdquisicion(1L, prod, prov);

        when(adquisicionRepository.buscarPorCodigoProducto("P1")).thenReturn(Optional.of(reg));

        ProveedorDto res = servicio.buscarProveedorPorProducto("P1");

        assertNotNull(res);
        assertEquals("PR1", res.codigo());
        assertEquals("TechDist", res.nombre());
    }

    @Test
    @DisplayName("Debe fallar al buscar proveedor si no existe adquisición para el producto")
    void debeFallarBuscarProveedorInexistente() {
        when(adquisicionRepository.buscarPorCodigoProducto("P99")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.buscarProveedorPorProducto("P99"));
    }
}
