package com.polimarket.application.service;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.polimarket.application.dto.LoginRequestDto;
import com.polimarket.application.dto.SesionUsuarioDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.VendedorNoAutorizadoException;
import com.polimarket.domain.model.Vendedor;
import com.polimarket.domain.repository.VendedorRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ServicioAutenticacion")
class ServicioAutenticacionTest {

    @Mock
    private VendedorRepository vendedorRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ServicioAutenticacion servicioAutenticacion;

    @BeforeEach
    void setUp() {
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenAnswer(inv -> "Mensaje: " + inv.getArgument(0));
    }

    @Test
    @DisplayName("Autenticación exitosa con credenciales válidas y vendedor autorizado")
    void autenticar_exitoso() {
        Vendedor vendedor = new Vendedor("carlos.gomez", "password123", true);
        when(vendedorRepository.buscarPorNombreUsuario("carlos.gomez")).thenReturn(Optional.of(vendedor));

        LoginRequestDto request = new LoginRequestDto("carlos.gomez", "password123");
        SesionUsuarioDto sesion = servicioAutenticacion.autenticar(request);

        assertNotNull(sesion);
        assertEquals("carlos.gomez", sesion.nombreUsuario());
        assertTrue(sesion.autorizado());
        assertEquals("VENDEDOR", sesion.rol());
    }

    @Test
    @DisplayName("Error cuando el usuario no existe")
    void autenticar_usuarioInexistente() {
        when(vendedorRepository.buscarPorNombreUsuario("fantasma")).thenReturn(Optional.empty());

        LoginRequestDto request = new LoginRequestDto("fantasma", "password123");
        assertThrows(RecursoNoEncontradoException.class, () -> servicioAutenticacion.autenticar(request));
    }

    @Test
    @DisplayName("Error cuando la contraseña es incorrecta")
    void autenticar_passwordInvalida() {
        Vendedor vendedor = new Vendedor("carlos.gomez", "password123", true);
        when(vendedorRepository.buscarPorNombreUsuario("carlos.gomez")).thenReturn(Optional.of(vendedor));

        LoginRequestDto request = new LoginRequestDto("carlos.gomez", "wrongpass");
        assertThrows(IllegalArgumentException.class, () -> servicioAutenticacion.autenticar(request));
    }

    @Test
    @DisplayName("Error cuando el vendedor no está autorizado")
    void autenticar_vendedorNoAutorizado() {
        Vendedor vendedor = new Vendedor("maria.lopez", "password123", false);
        when(vendedorRepository.buscarPorNombreUsuario("maria.lopez")).thenReturn(Optional.of(vendedor));

        LoginRequestDto request = new LoginRequestDto("maria.lopez", "password123");
        assertThrows(VendedorNoAutorizadoException.class, () -> servicioAutenticacion.autenticar(request));
    }
}
