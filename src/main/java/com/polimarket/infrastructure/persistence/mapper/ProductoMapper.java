package com.polimarket.infrastructure.persistence.mapper;

import com.polimarket.domain.model.Producto;
import com.polimarket.infrastructure.persistence.entity.ProductoEntity;

/**
 * Mapper para convertir entre {@link Producto} (dominio) y {@link ProductoEntity} (JPA).
 */
public final class ProductoMapper {

    private ProductoMapper() {
        // Clase utilitaria, no instanciable
    }

    public static Producto toDomain(ProductoEntity entity) {
        return new Producto(entity.getCodigo(), entity.getNombre());
    }

}
