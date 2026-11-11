package com.polimarket.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Modelo de Dominio: Vendedor")
class VendedorTest {

    @Test
    @DisplayName("Debe crear vendedor no autorizado por defecto y permitir autorizarlo")
    void debeCrearYAutorizarVendedor() {
        Vendedor vendedor = new Vendedor("jperez", false);

        assertNotNull(vendedor);
        assertEquals("jperez", vendedor.getNombreUsuario());
        assertFalse(vendedor.estaAutorizado());

        vendedor.autorizar();
        assertTrue(vendedor.estaAutorizado());

        vendedor.desautorizar();
        assertFalse(vendedor.estaAutorizado());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre de usuario es nulo")
    void debeLanzarExcepcionSiNombreEsNulo() {
        assertThrows(NullPointerException.class, () -> new Vendedor(null, false));
    }

    @Test
    @DisplayName("Debe validar equals y hashCode basados en nombre de usuario")
    void debeValidarEqualsYHashCode() {
        Vendedor v1 = new Vendedor("jperez", false);
        Vendedor v2 = new Vendedor("jperez", true);
        Vendedor v3 = new Vendedor("mgarcia", false);

        assertEquals(v1, v1);
        assertEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertNotEquals(v1, null);
        assertNotEquals(v1, "cadena");
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    @DisplayName("Debe validar roles y permisos correctamente")
    void debeValidarRolesYPermisos() {
        Vendedor rrhh = new Vendedor("admin.rrhh", "password123", true, "RECURSOS_HUMANOS", "GESTION_RRHH,CONSULTA_GENERAL");
        assertTrue(rrhh.esRecursosHumanos());
        assertTrue(rrhh.tienePermiso("GESTION_RRHH"));
        assertTrue(rrhh.tienePermiso("CONSULTA_GENERAL"));
        assertFalse(rrhh.tienePermiso("VENTAS"));
        assertFalse(rrhh.tienePermiso(null));
        assertEquals(2, rrhh.getListaPermisos().size());
        assertTrue(rrhh.validarPassword("password123"));
        assertFalse(rrhh.validarPassword("wrong"));
        assertTrue(rrhh.toString().contains("admin.rrhh"));

        Vendedor vendedor = new Vendedor("acastro", "password123", true);
        assertFalse(vendedor.esRecursosHumanos());
        assertTrue(vendedor.tienePermiso("VENTAS"));
        assertEquals("VENDEDOR", vendedor.getRol());
    }
}
