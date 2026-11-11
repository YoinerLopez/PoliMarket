package com.polimarket.application.service;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polimarket.application.dto.AdquisicionDto;
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.model.Proveedor;
import com.polimarket.domain.model.RegistroAdquisicion;
import com.polimarket.domain.repository.AdquisicionRepository;

/**
 * Servicio de Proveedores.
 *
 * <p>Gestiona la consulta de productos adquiridos y la búsqueda de proveedores
 * por producto (RF-04, RF-05).</p>
 */
@Service
@Transactional(readOnly = true)
public class ServicioProveedores {

    private final AdquisicionRepository adquisicionRepository;
    private final MessageSource messageSource;

    public ServicioProveedores(AdquisicionRepository adquisicionRepository,
                               MessageSource messageSource) {
        this.adquisicionRepository = adquisicionRepository;
        this.messageSource = messageSource;
    }

    /**
     * RF-04: Lista todos los productos adquiridos y sus proveedores.
     *
     * @return lista de adquisiciones
     */
    public List<AdquisicionDto> listarAdquisiciones() {
        List<RegistroAdquisicion> adquisiciones = adquisicionRepository.listar();
        return adquisiciones.stream()
                .map(a -> new AdquisicionDto(
                        a.getProducto().getCodigo(),
                        a.getProducto().consultarNombre(),
                        a.getProveedor().getCodigo(),
                        a.getProveedor().getNombre()))
                .toList();
    }

    /**
     * RF-05: Busca el proveedor asociado a un producto específico.
     *
     * @param codigoProducto código del producto a consultar
     * @return DTO del proveedor encontrado
     * @throws RecursoNoEncontradoException si no hay adquisición para el producto
     */
    public ProveedorDto buscarProveedorPorProducto(String codigoProducto) {
        RegistroAdquisicion adquisicion = adquisicionRepository
                .buscarPorCodigoProducto(codigoProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("proveedores.reposicion.sin.proveedor", codigoProducto)));

        Proveedor proveedor = adquisicion.consultarProveedor();
        return new ProveedorDto(proveedor.getCodigo(), proveedor.getNombre());
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
