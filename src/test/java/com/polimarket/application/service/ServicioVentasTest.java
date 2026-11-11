package com.polimarket.application.service;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.polimarket.application.dto.ClientePotencialDto;
import com.polimarket.application.dto.ProductoDisponibleDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.VendedorNoAutorizadoException;
import com.polimarket.domain.model.ClientePotencial;
import com.polimarket.domain.model.Existencia;
import com.polimarket.domain.model.Producto;
import com.polimarket.domain.repository.ClientePotencialRepository;
import com.polimarket.domain.repository.ExistenciaRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Servicio: Ventas (RF-02 y RF-03)")
class ServicioVentasTest {

    @Mock
    private ServicioRecursosHumanos servicioRRHH;

    @Mock
    private ClientePotencialRepository clienteRepository;

    @Mock
    private ExistenciaRepository existenciaRepository;

    @Mock
    private MessageSource messageSource;

    private ServicioVentas servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioVentas(servicioRRHH, clienteRepository, existenciaRepository, messageSource);
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Mensaje simulado");
    }

    @Test
    @DisplayName("RF-02 Éxito: Vendedor autorizado consulta cartera de clientes potenciales")
    void debeConsultarClientesPotencialesConVendedorAutorizado() {
        when(servicioRRHH.verificarAutorizacion("acastro")).thenReturn(true);
        when(clienteRepository.listar()).thenReturn(List.of(
                new ClientePotencial(1L, "Empresa ABC"),
                new ClientePotencial(2L, "Comercial del Valle")
        ));

        List<ClientePotencialDto> clientes = servicio.consultarClientesPotenciales("acastro");

        assertEquals(2, clientes.size());
        assertEquals("Empresa ABC", clientes.get(0).nombre());
        assertEquals("Comercial del Valle", clientes.get(1).nombre());
    }

    @Test
    @DisplayName("RF-02 Error: Vendedor pendiente no puede consultar clientes potenciales")
    void debeFallarConsultaClientesSiVendedorNoAutorizado() {
        when(servicioRRHH.verificarAutorizacion("jperez")).thenReturn(false);

        assertThrows(VendedorNoAutorizadoException.class, () -> servicio.consultarClientesPotenciales("jperez"));
        verify(clienteRepository, never()).listar();
    }

    @Test
    @DisplayName("RF-02 Error: Falla si el vendedor consultado no existe")
    void debeFallarConsultaClientesSiVendedorNoExiste() {
        when(servicioRRHH.verificarAutorizacion("fantasma"))
                .thenThrow(new RecursoNoEncontradoException("No existe"));

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.consultarClientesPotenciales("fantasma"));
    }

    @Test
    @DisplayName("RF-03 Éxito: Vendedor autorizado consulta productos disponibles (existencias > 0)")
    void debeConsultarProductosDisponiblesFiltrandoAgotados() {
        when(servicioRRHH.verificarAutorizacion("acastro")).thenReturn(true);

        Producto prod1 = new Producto("PROD-001", "Laptop HP");
        Producto prod2 = new Producto("PROD-002", "Monitor Dell");
        Producto prod3 = new Producto("PROD-003", "Teclado Agotado");

        when(existenciaRepository.listar()).thenReturn(List.of(
                new Existencia(prod1, 10),
                new Existencia(prod2, 5),
                new Existencia(prod3, 0)
        ));

        List<ProductoDisponibleDto> productos = servicio.consultarProductosDisponibles("acastro");

        assertEquals(2, productos.size());
        assertEquals("PROD-001", productos.get(0).codigoProducto());
        assertEquals(10, productos.get(0).cantidadDisponible());
        assertEquals("PROD-002", productos.get(1).codigoProducto());
        assertEquals(5, productos.get(1).cantidadDisponible());
    }

    @Test
    @DisplayName("RF-03 Error: Vendedor no autorizado no puede consultar productos")
    void debeFallarConsultaProductosSiVendedorNoAutorizado() {
        when(servicioRRHH.verificarAutorizacion("jperez")).thenReturn(false);

        assertThrows(VendedorNoAutorizadoException.class, () -> servicio.consultarProductosDisponibles("jperez"));
        verify(existenciaRepository, never()).listar();
    }
}
