package com.polimarket.domain.repository;

import java.util.List;
import java.util.Optional;

import com.polimarket.domain.model.Vendedor;

/**
 * Puerto de persistencia para vendedores.
 *
 * <p>Define el contrato de acceso a datos de vendedores.
 * La implementación concreta reside en la capa de infraestructura (DIP).</p>
 */
public interface VendedorRepository {

    /**
     * Busca un vendedor por su nombre de usuario.
     *
     * @param nombreUsuario nombre de usuario único del vendedor
     * @return el vendedor encontrado, o vacío si no existe
     */
    Optional<Vendedor> buscarPorNombreUsuario(String nombreUsuario);

    /**
     * Persiste los cambios realizados en un vendedor (ej: autorización).
     *
     * @param vendedor vendedor a guardar
     */
    void guardar(Vendedor vendedor);

    /**
     * Lista todos los vendedores y usuarios registrados en el sistema.
     *
     * @return lista completa de vendedores
     */
    List<Vendedor> listarTodos();

}
