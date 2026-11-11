package com.polimarket.domain.model;

import java.util.Objects;

/**
 * Representa la cantidad de unidades que Bodega tiene de un producto.
 *
 * <p>Cada producto tiene un único registro de existencias, incluso cuando
 * tiene cero unidades. Bodega usa la cantidad para informar si el producto
 * está disponible o agotado (RF-03, RF-05).</p>
 */
public class Existencia {

    private final Producto producto;
    private int cantidadDisponible;

    public Existencia(Producto producto, int cantidadDisponible) {
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser nulo");
        if (cantidadDisponible < 0) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser negativa");
        }
        this.cantidadDisponible = cantidadDisponible;
    }

    /**
     * Consulta la cantidad de unidades disponibles.
     *
     * @return cantidad disponible (>= 0)
     */
    public int consultarCantidadDisponible() {
        return this.cantidadDisponible;
    }

    /**
     * Verifica si el producto tiene existencias disponibles.
     *
     * @return {@code true} si la cantidad es mayor a cero
     */
    public boolean estaDisponible() {
        return this.cantidadDisponible > 0;
    }

    /**
     * Descuenta unidades del inventario al registrar una salida.
     *
     * @param cantidad unidades a descontar
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     *         o si supera las unidades disponibles
     */
    public void descontar(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero");
        }
        if (cantidad > this.cantidadDisponible) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponibles: " + this.cantidadDisponible + ", solicitadas: " + cantidad);
        }
        this.cantidadDisponible -= cantidad;
    }

    /**
     * Ingresa / recarga unidades al inventario (reposición o adquisición).
     *
     * @param cantidad unidades a ingresar
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     */
    public void ingresarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a ingresar debe ser mayor a cero");
        }
        this.cantidadDisponible += cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Existencia that)) return false;
        return Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto);
    }

}
