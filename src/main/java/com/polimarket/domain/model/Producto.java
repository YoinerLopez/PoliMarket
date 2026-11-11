package com.polimarket.domain.model;

import java.util.Objects;

/**
 * Representa un artículo identificado en el catálogo de PoliMarket.
 *
 * <p>El código identifica al producto en el catálogo. Cada producto conserva
 * su existencia incluso con cero unidades (RF-03, RF-04, RF-05).</p>
 */
public class Producto {

    private final String codigo;
    private final String nombre;

    public Producto(String codigo, String nombre) {
        this.codigo = Objects.requireNonNull(codigo, "El código del producto no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre del producto no puede ser nulo");
    }

    /**
     * Consulta el nombre del producto.
     *
     * @return nombre del producto
     */
    public String consultarNombre() {
        return this.nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto that)) return false;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

}
