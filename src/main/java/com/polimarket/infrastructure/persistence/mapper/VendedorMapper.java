package com.polimarket.infrastructure.persistence.mapper;

import com.polimarket.domain.model.Vendedor;
import com.polimarket.infrastructure.persistence.entity.VendedorEntity;

/**
 * Mapper para convertir entre {@link Vendedor} (dominio) y {@link VendedorEntity} (JPA).
 */
public final class VendedorMapper {

    private VendedorMapper() {
        // Clase utilitaria, no instanciable
    }

    public static Vendedor toDomain(VendedorEntity entity) {
        if (entity == null) return null;
        return new Vendedor(
                entity.getNombreUsuario(),
                entity.getPassword(),
                entity.isAutorizado(),
                entity.getRol(),
                entity.getPermisos()
        );
    }

    public static VendedorEntity toEntity(Vendedor domain) {
        if (domain == null) return null;
        return new VendedorEntity(
                domain.getNombreUsuario(),
                domain.getPassword(),
                domain.estaAutorizado(),
                domain.getRol(),
                domain.getPermisos()
        );
    }

}
