package com.polimarket.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Modelo de Dominio: ClientePotencial")
class ClientePotencialTest {

    @Test
    @DisplayName("Debe crear cliente potencial correctamente")
    void debeCrearClientePotencial() {
        ClientePotencial cliente = new ClientePotencial(1L, "Empresa ABC", "ventas@abc.com", "+57 310 111", "Bogota");

        assertEquals(1L, cliente.getId());
        assertEquals("Empresa ABC", cliente.getNombre());
        assertEquals("Empresa ABC", cliente.consultarNombre());
        assertEquals("ventas@abc.com", cliente.getCorreo());
        assertEquals("+57 310 111", cliente.getTelefono());
        assertEquals("Bogota", cliente.getCiudad());

        ClientePotencial clienteSimple = new ClientePotencial(2L, "Simple");
        assertNotNull(clienteSimple.getCorreo());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre es nulo")
    void debeLanzarExcepcionSiNombreEsNulo() {
        assertThrows(NullPointerException.class, () -> new ClientePotencial(1L, null));
    }

    @Test
    @DisplayName("Debe validar equals y hashCode basados en id")
    void debeValidarEqualsYHashCode() {
        ClientePotencial c1 = new ClientePotencial(1L, "Empresa A");
        ClientePotencial c2 = new ClientePotencial(1L, "Empresa B");
        ClientePotencial c3 = new ClientePotencial(2L, "Empresa A");

        assertEquals(c1, c1);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, new Object());
        assertEquals(c1.hashCode(), c2.hashCode());
    }
}
