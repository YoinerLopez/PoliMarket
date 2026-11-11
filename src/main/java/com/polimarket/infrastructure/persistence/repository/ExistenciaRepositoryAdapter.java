package com.polimarket.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.polimarket.domain.model.Existencia;
import com.polimarket.domain.repository.ExistenciaRepository;
import com.polimarket.infrastructure.persistence.entity.ExistenciaEntity;
import com.polimarket.infrastructure.persistence.mapper.ExistenciaMapper;

/**
 * Adaptador que implementa el puerto {@link ExistenciaRepository} del dominio
 * utilizando Spring Data JPA.
 */
@Component
public class ExistenciaRepositoryAdapter implements ExistenciaRepository {

    private final JpaExistenciaRepository jpaRepository;

    public ExistenciaRepositoryAdapter(JpaExistenciaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Existencia> listar() {
        return jpaRepository.findAll().stream()
                .map(ExistenciaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Existencia> buscarPorCodigoProducto(String codigoProducto) {
        return jpaRepository.findByProductoCodigo(codigoProducto)
                .map(ExistenciaMapper::toDomain);
    }

    @Override
    public void guardar(Existencia existencia) {
        ExistenciaEntity entity = jpaRepository
                .findByProductoCodigo(existencia.getProducto().getCodigo())
                .orElseThrow();
        ExistenciaMapper.updateEntity(entity, existencia);
        jpaRepository.save(entity);
    }

}
