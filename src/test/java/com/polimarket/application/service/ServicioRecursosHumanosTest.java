package com.polimarket.application.service;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.polimarket.application.dto.VendedorDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.ReglaNegocioException;
import com.polimarket.domain.model.Vendedor;
import com.polimarket.domain.repository.VendedorRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Servicio: Recursos Humanos (RF-01)")
class ServicioRecursosHumanosTest {

    @Mock
    private VendedorRepository vendedorRepository;

    @Mock
    private MessageSource messageSource;

    private ServicioRecursosHumanos servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioRecursosHumanos(vendedorRepository, messageSource);
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Mensaje simulado");
    }

    @Test
    @DisplayName("RF-01 Éxito: Debe autorizar a un vendedor registrado que estaba pendiente")
    void debeAutorizarVendedorExitosamente() {
        Vendedor vendedor = new Vendedor("jperez", false);
        when(vendedorRepository.buscarPorNombreUsuario("jperez")).thenReturn(Optional.of(vendedor));

        VendedorDto resultado = servicio.autorizarVendedor("jperez");

        assertNotNull(resultado);
        assertEquals("jperez", resultado.nombreUsuario());
        assertTrue(resultado.autorizado());
        verify(vendedorRepository).guardar(vendedor);
    }

    @Test
    @DisplayName("RF-01 Error: Debe fallar si el vendedor no existe")
    void debeFallarSiVendedorNoExiste() {
        when(vendedorRepository.buscarPorNombreUsuario("noexiste")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.autorizarVendedor("noexiste"));
        verify(vendedorRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("RF-01 Error: Debe fallar si el vendedor ya se encuentra autorizado")
    void debeFallarSiVendedorYaEstaAutorizado() {
        Vendedor vendedor = new Vendedor("acastro", true);
        when(vendedorRepository.buscarPorNombreUsuario("acastro")).thenReturn(Optional.of(vendedor));

        assertThrows(ReglaNegocioException.class, () -> servicio.autorizarVendedor("acastro"));
        verify(vendedorRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Éxito: Debe revocar la autorización de un vendedor autorizado")
    void debeRevocarAutorizacionExitosamente() {
        Vendedor vendedor = new Vendedor("acastro", true);
        when(vendedorRepository.buscarPorNombreUsuario("acastro")).thenReturn(Optional.of(vendedor));

        VendedorDto resultado = servicio.revocarAutorizacion("acastro");

        assertNotNull(resultado);
        assertFalse(resultado.autorizado());
        verify(vendedorRepository).guardar(vendedor);
    }

    @Test
    @DisplayName("Error: Debe fallar al revocar un vendedor que ya estaba desautorizado/pendiente")
    void debeFallarAlRevocarVendedorYaPendiente() {
        Vendedor vendedor = new Vendedor("jperez", false);
        when(vendedorRepository.buscarPorNombreUsuario("jperez")).thenReturn(Optional.of(vendedor));

        assertThrows(ReglaNegocioException.class, () -> servicio.revocarAutorizacion("jperez"));
    }

    @Test
    @DisplayName("Debe verificar correctamente el estado de autorización de un vendedor")
    void debeVerificarAutorizacion() {
        Vendedor v1 = new Vendedor("jperez", false);
        Vendedor v2 = new Vendedor("acastro", true);

        when(vendedorRepository.buscarPorNombreUsuario("jperez")).thenReturn(Optional.of(v1));
        when(vendedorRepository.buscarPorNombreUsuario("acastro")).thenReturn(Optional.of(v2));
        when(vendedorRepository.buscarPorNombreUsuario("fantasma")).thenReturn(Optional.empty());

        assertFalse(servicio.verificarAutorizacion("jperez"));
        assertTrue(servicio.verificarAutorizacion("acastro"));
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.verificarAutorizacion("fantasma"));
    }

    @Test
    @DisplayName("Debe listar todos los vendedores y usuarios del sistema")
    void debeListarVendedores() {
        Vendedor v1 = new Vendedor("admin.rrhh", "password123", true, "RECURSOS_HUMANOS", "GESTION_RRHH,CONSULTA_GENERAL");
        Vendedor v2 = new Vendedor("jperez", false);
        when(vendedorRepository.listarTodos()).thenReturn(java.util.List.of(v1, v2));

        java.util.List<VendedorDto> lista = servicio.listarVendedores();
        assertNotNull(lista);
        assertEquals(2, lista.size());
        assertEquals("admin.rrhh", lista.get(0).nombreUsuario());
        assertEquals("RECURSOS_HUMANOS", lista.get(0).rol());
        assertEquals("jperez", lista.get(1).nombreUsuario());
    }
}
