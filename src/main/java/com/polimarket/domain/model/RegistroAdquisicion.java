package com.polimarket.domain.model;

import java.util.Objects;

/**
 * Registro que vincula un producto adquirido con el proveedor que lo suministró.
 *
 * <p>Permite consultar los productos adquiridos y buscar el proveedor de un
 * producto específico. Los registros están precargados y cada producto tiene
 * un solo proveedor asociado (RF-04, RF-05).</p>
 */
public class RegistroAdquisicion {

    private final Long id;
    private final Producto producto;
    private final Proveedor proveedor;

    public RegistroAdquisicion(Long id, Producto producto, Proveedor proveedor) {
        this.id = id;
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser nulo");
        this.proveedor = Objects.requireNonNull(proveedor, "El proveedor no puede ser nulo");
    }

    /**
     * Consulta el proveedor asociado a este registro de adquisición.
     *
     * @return proveedor que suministró el producto
     */
    public Proveedor consultarProveedor() {
        return this.proveedor;
    }

    public Long getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistroAdquisicion that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
