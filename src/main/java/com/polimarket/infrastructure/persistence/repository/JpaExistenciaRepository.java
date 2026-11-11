package com.polimarket.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polimarket.infrastructure.persistence.entity.ExistenciaEntity;

/**
 * Repositorio Spring Data JPA para existencias.
 */
@Repository
public interface JpaExistenciaRepository extends JpaRepository<ExistenciaEntity, Long> {

    Optional<ExistenciaEntity> findByProductoCodigo(String codigoProducto);

}
