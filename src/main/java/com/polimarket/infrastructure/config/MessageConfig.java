package com.polimarket.infrastructure.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuración de internacionalización (i18n) de la aplicación.
 *
 * <p>Centraliza la resolución de mensajes en múltiples idiomas.
 * El idioma se determina automáticamente por el header {@code Accept-Language}
 * de la petición HTTP. El idioma por defecto es español (es).</p>
 *
 * <h3>Cómo agregar un nuevo idioma:</h3>
 * <ol>
 *   <li>Crear un archivo {@code messages_XX.properties} en {@code src/main/resources/}
 *       donde {@code XX} es el código ISO del idioma (ej: {@code messages_fr.properties}).</li>
 *   <li>Copiar las claves de {@code messages.properties} y traducir los valores.</li>
 *   <li>No es necesario modificar esta clase de configuración.</li>
 * </ol>
 *
 * <h3>Cómo usar en el cliente:</h3>
 * <p>Enviar el header {@code Accept-Language: en} para obtener respuestas en inglés,
 * o {@code Accept-Language: es} para español.</p>
 *
 * @author Equipo PoliMarket
 */
@Configuration
public class MessageConfig {

    /**
     * Configura el origen de mensajes internacionalizados.
     *
     * <p>Busca los archivos {@code messages*.properties} en el classpath.
     * Los archivos son recargables para facilitar el desarrollo.</p>
     *
     * @return MessageSource configurado con soporte multi-idioma
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * Configura el resolutor de locale basado en el header Accept-Language.
     *
     * <p>Si el cliente no envía el header, se usa español como idioma por defecto.</p>
     *
     * @return LocaleResolver configurado con locale por defecto en español
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.of("es"));
        return resolver;
    }

    /**
     * Integra el MessageSource con el sistema de validación de Bean Validation.
     *
     * <p>Permite que los mensajes de error de las anotaciones de validación
     * ({@code @NotNull}, {@code @Size}, etc.) también se internacionalicen.</p>
     *
     * @return LocalValidatorFactoryBean con MessageSource integrado
     */
    @Bean
    public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

}
