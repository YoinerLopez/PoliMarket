package com.polimarket.domain.repository;

import java.util.List;
import java.util.Optional;

import com.polimarket.domain.model.RegistroAdquisicion;

/**
 * Puerto de persistencia para registros de adquisiciones.
 *
 * <p>Define el contrato de acceso a datos de adquisiciones y proveedores.
 * La implementación concreta reside en la capa de infraestructura (DIP).</p>
 */
public interface AdquisicionRepository {

    /**
     * Lista todos los registros de productos adquiridos y sus proveedores.
     *
     * @return lista de registros de adquisición
     */
    List<RegistroAdquisicion> listar();

    /**
     * Lista los registros de adquisiciones de forma paginada para optimizar el rendimiento.
     *
     * @param pagina número de página (base 0)
     * @param tamano cantidad de registros por página
     * @return lista de registros de adquisición para la página solicitada
     */
    List<RegistroAdquisicion> listarPaginado(int pagina, int tamano);

    /**
     * Retorna la cantidad total de registros de adquisición en el sistema.
     *
     * @return total de registros
     */
    long contarTotal();

    /**
     * Busca el registro de adquisición de un producto por su código.
     *
     * @param codigoProducto código del producto a buscar
     * @return el registro encontrado, o vacío si no existe
     */
    Optional<RegistroAdquisicion> buscarPorCodigoProducto(String codigoProducto);

}
