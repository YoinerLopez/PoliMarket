package com.polimarket.domain.repository;

import java.util.List;
import java.util.Optional;

import com.polimarket.domain.model.Existencia;

/**
 * Puerto de persistencia para existencias de productos en bodega.
 *
 * <p>Define el contrato de acceso a datos de existencias.
 * La implementación concreta reside en la capa de infraestructura (DIP).</p>
 */
public interface ExistenciaRepository {

    /**
     * Lista todas las existencias registradas en bodega.
     *
     * @return lista de existencias (incluye productos con cantidad cero)
     */
    List<Existencia> listar();

    /**
     * Busca la existencia de un producto por su código.
     *
     * @param codigoProducto código del producto a buscar
     * @return la existencia encontrada, o vacío si no existe
     */
    Optional<Existencia> buscarPorCodigoProducto(String codigoProducto);

    /**
     * Persiste los cambios en una existencia (ej: descuento de unidades).
     *
     * @param existencia existencia a guardar
     */
    void guardar(Existencia existencia);

}
