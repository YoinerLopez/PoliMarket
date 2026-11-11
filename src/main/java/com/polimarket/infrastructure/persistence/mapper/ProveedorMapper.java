package com.polimarket.infrastructure.persistence.mapper;

import com.polimarket.domain.model.Proveedor;
import com.polimarket.infrastructure.persistence.entity.ProveedorEntity;

/**
 * Mapper para convertir entre {@link Proveedor} (dominio) y {@link ProveedorEntity} (JPA).
 */
public final class ProveedorMapper {

    private ProveedorMapper() {
        // Clase utilitaria, no instanciable
    }

    public static Proveedor toDomain(ProveedorEntity entity) {
        return new Proveedor(entity.getCodigo(), entity.getNombre());
    }

}
