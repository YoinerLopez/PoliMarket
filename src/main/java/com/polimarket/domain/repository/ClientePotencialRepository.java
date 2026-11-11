package com.polimarket.domain.repository;

import java.util.List;

import com.polimarket.domain.model.ClientePotencial;

/**
 * Puerto de persistencia para clientes potenciales.
 *
 * <p>Define el contrato de acceso a datos de clientes potenciales.
 * La implementación concreta reside en la capa de infraestructura (DIP).</p>
 */
public interface ClientePotencialRepository {

    /**
     * Lista todos los clientes potenciales registrados.
     *
     * @return lista de clientes potenciales
     */
    List<ClientePotencial> listar();

}
