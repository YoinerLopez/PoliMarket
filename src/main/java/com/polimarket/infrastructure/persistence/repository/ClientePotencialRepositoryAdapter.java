package com.polimarket.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.polimarket.domain.model.ClientePotencial;
import com.polimarket.domain.repository.ClientePotencialRepository;
import com.polimarket.infrastructure.persistence.mapper.ClientePotencialMapper;

/**
 * Adaptador que implementa el puerto {@link ClientePotencialRepository} del dominio
 * utilizando Spring Data JPA.
 */
@Component
public class ClientePotencialRepositoryAdapter implements ClientePotencialRepository {

    private final JpaClientePotencialRepository jpaRepository;

    public ClientePotencialRepositoryAdapter(JpaClientePotencialRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ClientePotencial> listar() {
        return jpaRepository.findAll().stream()
                .map(ClientePotencialMapper::toDomain)
                .toList();
    }

}
