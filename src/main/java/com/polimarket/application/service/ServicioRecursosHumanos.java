package com.polimarket.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polimarket.application.dto.VendedorDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.ReglaNegocioException;
import com.polimarket.domain.model.Vendedor;
import com.polimarket.domain.repository.VendedorRepository;
import com.polimarket.infrastructure.config.CacheConfig;

/**
 * Servicio de Recursos Humanos.
 *
 * <p>Gestiona la autorización de vendedores registrados y la asignación de permisos.
 * Implementa caché en memoria con invalidación reactiva para verificar
 * autorizaciones a velocidad de RAM sin saturar la base de datos.</p>
 */
@Service
@Transactional
public class ServicioRecursosHumanos {

    private static final Logger log = LoggerFactory.getLogger(ServicioRecursosHumanos.class);

    private final VendedorRepository vendedorRepository;
    private final MessageSource messageSource;

    public ServicioRecursosHumanos(VendedorRepository vendedorRepository,
                                    MessageSource messageSource) {
        this.vendedorRepository = vendedorRepository;
        this.messageSource = messageSource;
    }

    /**
     * Lista todos los vendedores y usuarios registrados en el sistema.
     *
     * @return lista completa de vendedores con sus roles y estados
     */
    @Transactional(readOnly = true)
    public List<VendedorDto> listarVendedores() {
        log.debug("Listando todos los usuarios y vendedores del sistema");
        return vendedorRepository.listarTodos().stream()
                .map(v -> new VendedorDto(v.getNombreUsuario(), v.estaAutorizado(), v.getRol(), v.getPermisos()))
                .toList();
    }

    /**
     * Autoriza a un vendedor registrado para interactuar con los sistemas de PoliMarket.
     * Invalida inmediatamente la entrada de la caché para este usuario.
     *
     * @param nombreUsuario nombre de usuario del vendedor a autorizar
     * @return DTO del vendedor autorizado
     * @throws RecursoNoEncontradoException si el vendedor no existe
     * @throws ReglaNegocioException si el vendedor ya está autorizado
     */
    @CacheEvict(value = CacheConfig.CACHE_AUTORIZACION_VENDEDOR, key = "#nombreUsuario")
    public VendedorDto autorizarVendedor(String nombreUsuario) {
        log.info("Autorizando vendedor y limpiando caché para '{}'", nombreUsuario);
        Vendedor vendedor = vendedorRepository.buscarPorNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("rrhh.vendedor.no.encontrado", nombreUsuario)));

        if (vendedor.estaAutorizado()) {
            throw new ReglaNegocioException(
                    getMessage("rrhh.vendedor.ya.autorizado", nombreUsuario));
        }

        vendedor.autorizar();
        vendedorRepository.guardar(vendedor);

        return new VendedorDto(vendedor.getNombreUsuario(), vendedor.estaAutorizado(), vendedor.getRol(), vendedor.getPermisos());
    }

    /**
     * Revoca la autorización de un vendedor.
     * Invalida inmediatamente la entrada de la caché, tumbando cualquier sesión activa.
     *
     * @param nombreUsuario nombre de usuario del vendedor a revocar
     * @return DTO del vendedor con su nuevo estado desautorizado
     * @throws RecursoNoEncontradoException si el vendedor no existe
     * @throws ReglaNegocioException si el vendedor ya estaba desautorizado
     */
    @CacheEvict(value = CacheConfig.CACHE_AUTORIZACION_VENDEDOR, key = "#nombreUsuario")
    public VendedorDto revocarAutorizacion(String nombreUsuario) {
        log.info("Revocando autorización y purgando caché para '{}'", nombreUsuario);
        Vendedor vendedor = vendedorRepository.buscarPorNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("rrhh.vendedor.no.encontrado", nombreUsuario)));

        if (!vendedor.estaAutorizado()) {
            throw new ReglaNegocioException(
                    getMessage("rrhh.vendedor.ya.revocado", nombreUsuario));
        }

        vendedor.desautorizar();
        vendedorRepository.guardar(vendedor);

        return new VendedorDto(vendedor.getNombreUsuario(), vendedor.estaAutorizado(), vendedor.getRol(), vendedor.getPermisos());
    }

    /**
     * Verifica si un vendedor está autorizado.
     *
     * <p>Utiliza caché en memoria: la primera vez consulta la base de datos y
     * las subsiguientes se resuelven en microsegundos desde RAM.</p>
     *
     * @param nombreUsuario nombre de usuario del vendedor
     * @return {@code true} si está autorizado
     * @throws RecursoNoEncontradoException si el vendedor no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.CACHE_AUTORIZACION_VENDEDOR, key = "#nombreUsuario")
    public boolean verificarAutorizacion(String nombreUsuario) {
        log.debug("Consulta a Base de Datos: verificando autorización para '{}'", nombreUsuario);
        Vendedor vendedor = vendedorRepository.buscarPorNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("rrhh.vendedor.no.encontrado", nombreUsuario)));
        return vendedor.estaAutorizado();
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
