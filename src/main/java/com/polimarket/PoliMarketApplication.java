package com.polimarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación PoliMarket.
 *
 * <p>Punto de entrada de la API REST que gestiona los módulos de
 * Recursos Humanos, Ventas, Bodega, Proveedores y Entregas de PoliMarket.</p>
 *
 * @author Equipo PoliMarket
 */
@SpringBootApplication
public class PoliMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoliMarketApplication.class, args);
    }

}
