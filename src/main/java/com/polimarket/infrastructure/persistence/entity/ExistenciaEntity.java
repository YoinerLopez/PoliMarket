package com.polimarket.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que mapea la tabla de existencias en bodega.
 *
 * <p>Relación 1:1 con {@link ProductoEntity}.</p>
 */
@Entity
@Table(name = "existencias")
public class ExistenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_codigo", referencedColumnName = "codigo", nullable = false, unique = true)
    private ProductoEntity producto;

    @Column(name = "cantidad_disponible", nullable = false)
    private int cantidadDisponible;

    protected ExistenciaEntity() {
        // Constructor requerido por JPA
    }

    public ExistenciaEntity(ProductoEntity producto, int cantidadDisponible) {
        this.producto = producto;
        this.cantidadDisponible = cantidadDisponible;
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

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

}
