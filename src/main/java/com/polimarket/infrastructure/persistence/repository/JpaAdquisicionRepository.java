package com.polimarket.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polimarket.infrastructure.persistence.entity.RegistroAdquisicionEntity;

/**
 * Repositorio Spring Data JPA para registros de adquisición.
 */
@Repository
public interface JpaAdquisicionRepository extends JpaRepository<RegistroAdquisicionEntity, Long> {

    Optional<RegistroAdquisicionEntity> findByProductoCodigo(String codigoProducto);

}
