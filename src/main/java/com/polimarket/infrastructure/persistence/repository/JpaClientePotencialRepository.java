package com.polimarket.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polimarket.infrastructure.persistence.entity.ClientePotencialEntity;

/**
 * Repositorio Spring Data JPA para clientes potenciales.
 */
@Repository
public interface JpaClientePotencialRepository extends JpaRepository<ClientePotencialEntity, Long> {

}
