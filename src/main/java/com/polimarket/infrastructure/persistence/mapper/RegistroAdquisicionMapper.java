package com.polimarket.infrastructure.persistence.mapper;

import com.polimarket.domain.model.Producto;
import com.polimarket.domain.model.Proveedor;
import com.polimarket.domain.model.RegistroAdquisicion;
import com.polimarket.infrastructure.persistence.entity.RegistroAdquisicionEntity;

/**
 * Mapper para convertir entre {@link RegistroAdquisicion} (dominio) y {@link RegistroAdquisicionEntity} (JPA).
 */
public final class RegistroAdquisicionMapper {

    private RegistroAdquisicionMapper() {
        // Clase utilitaria, no instanciable
    }

    public static RegistroAdquisicion toDomain(RegistroAdquisicionEntity entity) {
        Producto producto = ProductoMapper.toDomain(entity.getProducto());
        Proveedor proveedor = ProveedorMapper.toDomain(entity.getProveedor());
        return new RegistroAdquisicion(entity.getId(), producto, proveedor);
    }

}
