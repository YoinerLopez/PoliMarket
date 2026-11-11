package com.polimarket.infrastructure.persistence.mapper;

import com.polimarket.domain.model.Existencia;
import com.polimarket.domain.model.Producto;
import com.polimarket.infrastructure.persistence.entity.ExistenciaEntity;

/**
 * Mapper para convertir entre {@link Existencia} (dominio) y {@link ExistenciaEntity} (JPA).
 */
public final class ExistenciaMapper {

    private ExistenciaMapper() {
        // Clase utilitaria, no instanciable
    }

    public static Existencia toDomain(ExistenciaEntity entity) {
        Producto producto = ProductoMapper.toDomain(entity.getProducto());
        return new Existencia(producto, entity.getCantidadDisponible());
    }

    public static void updateEntity(ExistenciaEntity entity, Existencia domain) {
        entity.setCantidadDisponible(domain.getCantidadDisponible());
    }

}
