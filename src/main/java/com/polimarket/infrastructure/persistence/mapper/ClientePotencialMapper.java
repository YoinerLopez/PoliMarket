package com.polimarket.infrastructure.persistence.mapper;

import com.polimarket.domain.model.ClientePotencial;
import com.polimarket.infrastructure.persistence.entity.ClientePotencialEntity;

/**
 * Mapper para convertir entre {@link ClientePotencial} (dominio) y {@link ClientePotencialEntity} (JPA).
 */
public final class ClientePotencialMapper {

    private ClientePotencialMapper() {
        // Clase utilitaria, no instanciable
    }

    public static ClientePotencial toDomain(ClientePotencialEntity entity) {
        return new ClientePotencial(
                entity.getId(),
                entity.getNombre(),
                entity.getCorreo(),
                entity.getTelefono(),
                entity.getCiudad()
        );
    }

}
