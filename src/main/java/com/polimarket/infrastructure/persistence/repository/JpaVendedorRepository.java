package com.polimarket.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polimarket.infrastructure.persistence.entity.VendedorEntity;

/**
 * Repositorio Spring Data JPA para vendedores.
 */
@Repository
public interface JpaVendedorRepository extends JpaRepository<VendedorEntity, String> {

    Optional<VendedorEntity> findByNombreUsuario(String nombreUsuario);

}
