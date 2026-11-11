package com.polimarket.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polimarket.infrastructure.persistence.entity.ProveedorEntity;

@Repository
public interface JpaProveedorRepository extends JpaRepository<ProveedorEntity, String> {
}
