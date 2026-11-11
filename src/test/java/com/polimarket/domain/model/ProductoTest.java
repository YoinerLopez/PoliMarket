package com.polimarket.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Modelo de Dominio: Producto")
class ProductoTest {

    @Test
    @DisplayName("Debe crear producto correctamente y consultar su nombre")
    void debeCrearProducto() {
        Producto producto = new Producto("PROD-001", "Laptop HP");

        assertEquals("PROD-001", producto.getCodigo());
        assertEquals("Laptop HP", producto.getNombre());
        assertEquals("Laptop HP", producto.consultarNombre());
    }

    @Test
    @DisplayName("Debe lanzar excepción si código o nombre son nulos")
    void debeLanzarExcepcionConValoresNulos() {
        assertThrows(NullPointerException.class, () -> new Producto(null, "Laptop"));
        assertThrows(NullPointerException.class, () -> new Producto("PROD-001", null));
    }

    @Test
    @DisplayName("Debe validar equals y hashCode basados en código")
    void debeValidarEqualsYHashCode() {
        Producto p1 = new Producto("PROD-001", "Laptop");
        Producto p2 = new Producto("PROD-001", "Portátil");
        Producto p3 = new Producto("PROD-002", "Laptop");

        assertEquals(p1, p1);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "texto");
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
