package com.polimarket.infrastructure.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Configuración de la caché en memoria ultrarrápida utilizando Caffeine.
 *
 * <p>Implementa el patrón Cache-Aside con invalidación reactiva (@CacheEvict):
 * - Las consultas de verificación de autorización se leen en microsegundos desde RAM.
 * - Cero saturación a la base de datos para lecturas frecuentes.
 * - Al autorizar o revocar un usuario, la entrada se purga inmediatamente de la caché.</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_AUTORIZACION_VENDEDOR = "vendedores-autorizacion";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CACHE_AUTORIZACION_VENDEDOR);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10_000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }

}
