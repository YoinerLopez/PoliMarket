package com.polimarket.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.polimarket.domain.model.Vendedor;
import com.polimarket.domain.repository.VendedorRepository;
import com.polimarket.infrastructure.persistence.mapper.VendedorMapper;

/**
 * Adaptador que implementa el puerto {@link VendedorRepository} del dominio
 * utilizando Spring Data JPA.
 */
@Component
public class VendedorRepositoryAdapter implements VendedorRepository {

    private final JpaVendedorRepository jpaRepository;

    public VendedorRepositoryAdapter(JpaVendedorRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Vendedor> buscarPorNombreUsuario(String nombreUsuario) {
        return jpaRepository.findByNombreUsuario(nombreUsuario)
                .map(VendedorMapper::toDomain);
    }

    @Override
    public void guardar(Vendedor vendedor) {
        jpaRepository.save(VendedorMapper.toEntity(vendedor));
    }

    @Override
    public List<Vendedor> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(VendedorMapper::toDomain)
                .toList();
    }

}
