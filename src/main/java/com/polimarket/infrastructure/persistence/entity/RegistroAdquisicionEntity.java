package com.polimarket.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que mapea la tabla de registros de adquisición.
 *
 * <p>Relación 1:1 con {@link ProductoEntity} y N:1 con {@link ProveedorEntity}.</p>
 */
@Entity
@Table(name = "registros_adquisicion")
public class RegistroAdquisicionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_codigo", referencedColumnName = "codigo", nullable = false, unique = true)
    private ProductoEntity producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_codigo", referencedColumnName = "codigo", nullable = false)
    private ProveedorEntity proveedor;

    protected RegistroAdquisicionEntity() {
        // Constructor requerido por JPA
    }

    public RegistroAdquisicionEntity(ProductoEntity producto, ProveedorEntity proveedor) {
        this.producto = producto;
        this.proveedor = proveedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }

    public ProveedorEntity getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorEntity proveedor) {
        this.proveedor = proveedor;
    }

}
