package com.polimarket.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuración de OpenAPI / Swagger UI.
 *
 * <p>Define la documentación interactiva de la API REST de PoliMarket.
 * Accesible en {@code /swagger-ui.html}.</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI poliMarketOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PoliMarket API")
                        .description("""
                                Plataforma de Gestión Comercial y Bodega de PoliMarket.

                                Módulos disponibles:
                                - **Autenticación**: Inicio de sesión y validación de permisos
                                - **Recursos Humanos**: Gestión de autorización y roles de vendedores
                                - **Ventas**: Cartera de clientes potenciales y catálogo comercial
                                - **Bodega e Inventario**: Control de existencias, recarga, salida y reposición
                                - **Proveedores**: Historial de adquisiciones e identificación de proveedores

                                ### Internacionalización
                                La API soporta múltiples idiomas. Envíe el header `Accept-Language` con el código del idioma deseado:
                                - `es` — Español (por defecto)
                                - `en` — English
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo PoliMarket")
                                .email("contacto@polimarket.com"))
                        .license(new License()
                                .name("Licencia Propietaria PoliMarket")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor Local PoliMarket"));
    }

}
