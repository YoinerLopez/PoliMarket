package com.polimarket.presentation.console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.polimarket.application.dto.AdquisicionDto;
import com.polimarket.application.dto.LoginRequestDto;
import com.polimarket.application.dto.PaginaDTO;
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.application.dto.SesionUsuarioDto;
import com.polimarket.application.service.ServicioAutenticacion;
import com.polimarket.application.service.ServicioBodega;
import com.polimarket.domain.exception.RecursoNoEncontradoException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Cliente de Consola (Comp_ClienteConsola)")
class ClienteConsolaTest {

    @Mock
    private ServicioBodega servicioBodega;

    @Mock
    private ServicioAutenticacion servicioAutenticacion;

    private ClienteConsola clienteConsola;

    @BeforeEach
    void setUp() {
        clienteConsola = new ClienteConsola(servicioBodega, servicioAutenticacion);
    }

    private void configurarLoginExitoso() {
        when(servicioAutenticacion.autenticar(any(LoginRequestDto.class)))
                .thenReturn(new SesionUsuarioDto("carlos.gomez", true, "VENDEDOR"));
    }

    @Test
    @DisplayName("Debe salir de la terminal al seleccionar la opción 3 tras autenticarse")
    void debeSalirDeLaTerminal() {
        configurarLoginExitoso();
        String input = "carlos.gomez\npassword123\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("POLIMARKET - TERMINAL DE BODEGA"));
        assertTrue(salida.contains("Sesión iniciada con éxito"));
        assertTrue(salida.contains("Cerrando sesión y terminal"));
    }

    @Test
    @DisplayName("Debe cancelar inicio de sesión si el usuario escribe 'salir'")
    void debeCancelarLoginConSalir() {
        String input = "salir\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("Sesión cancelada"));
    }

    @Test
    @DisplayName("Debe reintentar y fallar si se superan los 3 intentos de login")
    void debeFallarDespuesDeTresIntentosFallidos() {
        when(servicioAutenticacion.autenticar(any(LoginRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Credenciales incorrectas"));

        String input = "user1\npass1\nuser2\npass2\nuser3\npass3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("Número máximo de intentos alcanzado"));
        assertTrue(salida.contains("Sesión cancelada o credenciales incorrectas"));
    }

    @Test
    @DisplayName("Debe manejar opción inválida en el menú")
    void debeManejarOpcionInvalida() {
        configurarLoginExitoso();
        String input = "carlos.gomez\npassword123\n9\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("Opción inválida"));
    }

    @Test
    @DisplayName("RF-04: Debe consultar adquisiciones paginadas con navegación Siguiente, Anterior y Volver")
    void debeConsultarAdquisicionesPaginadasConNavegacion() {
        configurarLoginExitoso();
        List<AdquisicionDto> items = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            items.add(new AdquisicionDto("P" + i, "Prod " + i, "PR1", "Proveedor 1"));
        }
        PaginaDTO<AdquisicionDto> paginaDto = PaginaDTO.de(items, 0, 495, 20);

        when(servicioBodega.consultarProductosAdquiridosPaginado(eq(0), anyInt())).thenReturn(paginaDto);

        // Secuencia de comandos en consola: 
        // carlos.gomez -> password123 -> 1 (Entrar a adquisiciones) -> S (Siguiente) -> A (Anterior) -> M (Menú) -> 3 (Salir)
        String input = "carlos.gomez\npassword123\n1\nS\nA\nM\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("HISTÓRICO DE ADQUISICIONES"));
        assertTrue(salida.contains("Pantalla 1 de 2"));
        assertTrue(salida.contains("Pantalla 2 de 2"));
    }

    @Test
    @DisplayName("RF-04: Maneja caso cuando no existen adquisiciones")
    void debeManejarAdquisicionesVacias() {
        configurarLoginExitoso();
        PaginaDTO<AdquisicionDto> vacio = PaginaDTO.de(List.of(), 0, 495, 0);
        when(servicioBodega.consultarProductosAdquiridosPaginado(eq(0), anyInt())).thenReturn(vacio);

        String input = "carlos.gomez\npassword123\n1\n\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("No se encontraron registros de adquisición"));
    }

    @Test
    @DisplayName("RF-05: Debe identificar proveedor para reposición exitosamente")
    void debeIdentificarProveedorReposicionExitoso() {
        configurarLoginExitoso();
        when(servicioBodega.identificarProveedorReposicion("PROD-003"))
                .thenReturn(new ProveedorDto("PROV-003", "ElectroComponentes S.A.S."));

        String input = "carlos.gomez\npassword123\n2\nPROD-003\n\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("PROVEEDOR IDENTIFICADO CON ÉXITO"));
        assertTrue(salida.contains("ElectroComponentes S.A.S."));
    }

    @Test
    @DisplayName("RF-05: Maneja error si el producto no existe o código vacío")
    void debeManejarErrorEnReposicion() {
        configurarLoginExitoso();
        when(servicioBodega.identificarProveedorReposicion("PROD-404"))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado"));

        String input = "carlos.gomez\npassword123\n2\n   \n2\nPROD-404\n\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        clienteConsola.ejecutar(in, out);

        String salida = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("El código no puede estar vacío"));
        assertTrue(salida.contains("Producto no encontrado"));
    }
}
