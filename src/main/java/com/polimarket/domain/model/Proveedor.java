package com.polimarket.domain.model;

import java.util.Objects;

/**
 * Representa la empresa que suministró un producto a PoliMarket.
 *
 * <p>Permite identificar al proveedor que figura en el listado de adquisiciones
 * y al que se podría pedir reposición (RF-04, RF-05).</p>
 */
public class Proveedor {

    private final String codigo;
    private final String nombre;

    public Proveedor(String codigo, String nombre) {
        this.codigo = Objects.requireNonNull(codigo, "El código del proveedor no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre del proveedor no puede ser nulo");
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
        if (!(o instanceof Proveedor that)) return false;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

}
