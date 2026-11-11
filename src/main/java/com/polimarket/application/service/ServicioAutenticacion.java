package com.polimarket.application.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polimarket.application.dto.LoginRequestDto;
import com.polimarket.application.dto.SesionUsuarioDto;
import com.polimarket.domain.exception.RecursoNoEncontradoException;
import com.polimarket.domain.exception.VendedorNoAutorizadoException;
import com.polimarket.domain.model.Vendedor;
import com.polimarket.domain.repository.VendedorRepository;

@Service
@Transactional(readOnly = true)
public class ServicioAutenticacion {

    private final VendedorRepository vendedorRepository;
    private final MessageSource messageSource;

    public ServicioAutenticacion(VendedorRepository vendedorRepository, MessageSource messageSource) {
        this.vendedorRepository = vendedorRepository;
        this.messageSource = messageSource;
    }

    /**
     * Valida las credenciales del usuario y su estado de autorización.
     *
     * @param request credenciales ingresadas
     * @return datos de sesión del usuario
     */
    public SesionUsuarioDto autenticar(LoginRequestDto request) {
        Vendedor vendedor = vendedorRepository.buscarPorNombreUsuario(request.nombreUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        getMessage("rrhh.vendedor.no.encontrado", request.nombreUsuario())));

        if (!vendedor.validarPassword(request.password())) {
            throw new IllegalArgumentException(getMessage("auth.credenciales.invalidas"));
        }

        if (!vendedor.estaAutorizado()) {
            throw new VendedorNoAutorizadoException(
                    getMessage("auth.usuario.no.autorizado", request.nombreUsuario()));
        }

        return new SesionUsuarioDto(vendedor.getNombreUsuario(), vendedor.estaAutorizado(), vendedor.getRol(), vendedor.getPermisos());
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
