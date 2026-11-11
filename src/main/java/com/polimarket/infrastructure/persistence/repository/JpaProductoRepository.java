package com.polimarket.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polimarket.infrastructure.persistence.entity.ProductoEntity;

@Repository
public interface JpaProductoRepository extends JpaRepository<ProductoEntity, String> {
}
