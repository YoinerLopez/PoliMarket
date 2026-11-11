package com.polimarket.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.polimarket.application.service.ServicioRecursosHumanos;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Pruebas de Integración - Caché en Memoria e Invalidación Reactiva")
class CacheIntegrationTest {

    @Autowired
    private ServicioRecursosHumanos servicioRRHH;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Caché: Debe almacenar en memoria al verificar y purgar al autorizar o revocar")
    void debeVerificarCacheYEviccionReactiva() {
        Cache cache = cacheManager.getCache(CacheConfig.CACHE_AUTORIZACION_VENDEDOR);
        assertNotNull(cache);

        // 1. Verificación inicial de 'acastro' (precargado como true)
        boolean estado1 = servicioRRHH.verificarAutorizacion("acastro");
        assertTrue(estado1);

        // 2. Verificar que ahora está almacenado en memoria (Cache Hit)
        Cache.ValueWrapper enCache = cache.get("acastro");
        assertNotNull(enCache, "El estado debe residir en la caché en memoria");
        assertEquals(Boolean.TRUE, enCache.get());

        // 3. Revocar al vendedor (debe invalidar/evictar la caché reactivamente)
        servicioRRHH.revocarAutorizacion("acastro");

        // 4. Verificar que la entrada fue purgada de la memoria
        assertNull(cache.get("acastro"), "La entrada debe ser purgada de la caché tras la revocación");

        // 5. La nueva consulta lee el nuevo estado falso y lo cachea
        boolean estadoRevocado = servicioRRHH.verificarAutorizacion("acastro");
        assertFalse(estadoRevocado);

        // 6. Volver a autorizar para dejarlo consistente
        servicioRRHH.autorizarVendedor("acastro");
        assertNull(cache.get("acastro"), "La entrada debe volver a purgarse tras autorizar");
    }
}
