package com.polimarket.application.service;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polimarket.application.dto.ClientePotencialDto;
import com.polimarket.application.dto.ProductoDisponibleDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.VendedorNoAutorizadoException;
import com.polimarket.domain.model.ClientePotencial;
import com.polimarket.domain.model.Existencia;
import com.polimarket.domain.repository.ClientePotencialRepository;
import com.polimarket.domain.repository.ExistenciaRepository;

/**
 * Servicio de Ventas.
 *
 * <p>Gestiona la consulta de clientes potenciales (RF-02) y
 * productos disponibles (RF-03).
 * Coordina con {@link ServicioRecursosHumanos} para verificar la autorización
 * del vendedor a través de la caché en memoria.</p>
 */
@Service
@Transactional(readOnly = true)
public class ServicioVentas {

    private final ServicioRecursosHumanos servicioRRHH;
    private final ClientePotencialRepository clienteRepository;
    private final ExistenciaRepository existenciaRepository;
    private final MessageSource messageSource;

    public ServicioVentas(ServicioRecursosHumanos servicioRRHH,
                          ClientePotencialRepository clienteRepository,
                          ExistenciaRepository existenciaRepository,
                          MessageSource messageSource) {
        this.servicioRRHH = servicioRRHH;
        this.clienteRepository = clienteRepository;
        this.existenciaRepository = existenciaRepository;
        this.messageSource = messageSource;
    }

    /**
     * RF-02: Consulta los posibles clientes que un vendedor autorizado puede contactar.
     *
     * @param nombreUsuario nombre de usuario del vendedor que realiza la consulta
     * @return lista de clientes potenciales
     * @throws RecursoNoEncontradoException si el vendedor no existe
     * @throws VendedorNoAutorizadoException si el vendedor no está autorizado
     */
    public List<ClientePotencialDto> consultarClientesPotenciales(String nombreUsuario) {
        validarVendedorAutorizado(nombreUsuario);

        List<ClientePotencial> clientes = clienteRepository.listar();
        return clientes.stream()
                .map(c -> new ClientePotencialDto(c.getId(), c.consultarNombre(), c.getCorreo(), c.getTelefono(), c.getCiudad()))
                .toList();
    }

    /**
     * RF-03: Consulta los productos disponibles (existencias > 0) que un vendedor puede ofrecer.
     *
     * <p>Ventas consulta las existencias en Bodega y muestra solo los productos
     * con cantidad mayor a cero.</p>
     *
     * @param nombreUsuario nombre de usuario del vendedor que realiza la consulta
     * @return lista de productos disponibles con su cantidad
     * @throws RecursoNoEncontradoException si el vendedor no existe
     * @throws VendedorNoAutorizadoException si el vendedor no está autorizado
     */
    public List<ProductoDisponibleDto> consultarProductosDisponibles(String nombreUsuario) {
        validarVendedorAutorizado(nombreUsuario);

        List<Existencia> existencias = existenciaRepository.listar();
        return existencias.stream()
                .filter(Existencia::estaDisponible)
                .map(e -> new ProductoDisponibleDto(
                        e.getProducto().getCodigo(),
                        e.getProducto().consultarNombre(),
                        e.consultarCantidadDisponible()))
                .toList();
    }

    /**
     * Valida que el vendedor esté autorizado delegando en ServicioRecursosHumanos (con soporte de caché).
     */
    private void validarVendedorAutorizado(String nombreUsuario) {
        boolean estaAutorizado = servicioRRHH.verificarAutorizacion(nombreUsuario);
        if (!estaAutorizado) {
            throw new VendedorNoAutorizadoException(
                    getMessage("ventas.vendedor.no.autorizado", nombreUsuario));
        }
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
