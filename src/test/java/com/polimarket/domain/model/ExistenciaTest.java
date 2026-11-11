package com.polimarket.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Modelo de Dominio: Existencia")
class ExistenciaTest {

    @Test
    @DisplayName("Debe crear existencia y verificar disponibilidad")
    void debeCrearExistenciaYVerificarDisponibilidad() {
        Producto producto = new Producto("PROD-001", "Monitor");
        Existencia conStock = new Existencia(producto, 10);
        Existencia sinStock = new Existencia(producto, 0);

        assertEquals(producto, conStock.getProducto());
        assertEquals(10, conStock.consultarCantidadDisponible());
        assertEquals(10, conStock.getCantidadDisponible());
        assertTrue(conStock.estaDisponible());

        assertEquals(0, sinStock.consultarCantidadDisponible());
        assertFalse(sinStock.estaDisponible());
    }

    @Test
    @DisplayName("Debe descontar e ingresar existencias correctamente")
    void debeDescontarExistencias() {
        Producto producto = new Producto("PROD-001", "Monitor");
        Existencia existencia = new Existencia(producto, 10);

        existencia.descontar(4);
        assertEquals(6, existencia.consultarCantidadDisponible());

        existencia.ingresarStock(10);
        assertEquals(16, existencia.consultarCantidadDisponible());

        assertThrows(IllegalArgumentException.class, () -> existencia.ingresarStock(0));
        assertThrows(IllegalArgumentException.class, () -> existencia.ingresarStock(-5));

        existencia.descontar(16);
        assertEquals(0, existencia.consultarCantidadDisponible());
        assertFalse(existencia.estaDisponible());
    }

    @Test
    @DisplayName("Debe fallar al crear existencia con cantidad negativa")
    void debeFallarConCantidadNegativa() {
        Producto producto = new Producto("PROD-001", "Monitor");
        assertThrows(IllegalArgumentException.class, () -> new Existencia(producto, -1));
        assertThrows(NullPointerException.class, () -> new Existencia(null, 5));
    }

    @Test
    @DisplayName("Debe fallar al descontar cantidad no positiva o que exceda el stock")
    void debeFallarDescuentoInvalido() {
        Producto producto = new Producto("PROD-001", "Monitor");
        Existencia existencia = new Existencia(producto, 5);

        assertThrows(IllegalArgumentException.class, () -> existencia.descontar(0));
        assertThrows(IllegalArgumentException.class, () -> existencia.descontar(-2));
        assertThrows(IllegalArgumentException.class, () -> existencia.descontar(6));
    }

    @Test
    @DisplayName("Debe validar equals y hashCode basados en producto")
    void debeValidarEqualsYHashCode() {
        Producto p1 = new Producto("PROD-001", "Monitor");
        Producto p2 = new Producto("PROD-002", "Teclado");

        Existencia e1 = new Existencia(p1, 5);
        Existencia e2 = new Existencia(p1, 20);
        Existencia e3 = new Existencia(p2, 5);

        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, null);
        assertNotEquals(e1, 123);
        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
