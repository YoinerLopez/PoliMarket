package com.polimarket.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.polimarket.domain.model.RegistroAdquisicion;
import com.polimarket.domain.repository.AdquisicionRepository;
import com.polimarket.infrastructure.persistence.mapper.RegistroAdquisicionMapper;

/**
 * Adaptador que implementa el puerto {@link AdquisicionRepository} del dominio
 * utilizando Spring Data JPA.
 */
@Component
public class AdquisicionRepositoryAdapter implements AdquisicionRepository {

    private final JpaAdquisicionRepository jpaRepository;

    public AdquisicionRepositoryAdapter(JpaAdquisicionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<RegistroAdquisicion> listar() {
        return jpaRepository.findAll().stream()
                .map(RegistroAdquisicionMapper::toDomain)
                .toList();
    }

    @Override
    public List<RegistroAdquisicion> listarPaginado(int pagina, int tamano) {
        org.springframework.data.domain.PageRequest pageRequest =
                org.springframework.data.domain.PageRequest.of(pagina, tamano);
        return jpaRepository.findAll(pageRequest).getContent().stream()
                .map(RegistroAdquisicionMapper::toDomain)
                .toList();
    }

    @Override
    public long contarTotal() {
        return jpaRepository.count();
    }

    @Override
    public Optional<RegistroAdquisicion> buscarPorCodigoProducto(String codigoProducto) {
        return jpaRepository.findByProductoCodigo(codigoProducto)
                .map(RegistroAdquisicionMapper::toDomain);
    }

}
