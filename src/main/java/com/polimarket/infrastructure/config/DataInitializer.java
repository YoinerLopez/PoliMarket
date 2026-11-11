package com.polimarket.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.polimarket.infrastructure.persistence.entity.ClientePotencialEntity;
import com.polimarket.infrastructure.persistence.entity.ExistenciaEntity;
import com.polimarket.infrastructure.persistence.entity.ProductoEntity;
import com.polimarket.infrastructure.persistence.entity.ProveedorEntity;
import com.polimarket.infrastructure.persistence.entity.RegistroAdquisicionEntity;
import com.polimarket.infrastructure.persistence.entity.VendedorEntity;
import com.polimarket.infrastructure.persistence.repository.JpaAdquisicionRepository;
import com.polimarket.infrastructure.persistence.repository.JpaClientePotencialRepository;
import com.polimarket.infrastructure.persistence.repository.JpaExistenciaRepository;
import com.polimarket.infrastructure.persistence.repository.JpaProductoRepository;
import com.polimarket.infrastructure.persistence.repository.JpaProveedorRepository;
import com.polimarket.infrastructure.persistence.repository.JpaVendedorRepository;

/**
 * Inicializador de datos de demostración para PoliMarket.
 *
 * <p>Carga datos precargados según los supuestos del caso de estudio:
 * vendedores, clientes potenciales, productos, existencias, proveedores
 * y registros de adquisición.</p>
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initData(
            JpaVendedorRepository vendedorRepo,
            JpaClientePotencialRepository clienteRepo,
            JpaProductoRepository productoRepo,
            JpaProveedorRepository proveedorRepo,
            JpaExistenciaRepository existenciaRepo,
            JpaAdquisicionRepository adquisicionRepo) {

        return args -> cargarDatos(vendedorRepo, clienteRepo, productoRepo, proveedorRepo, existenciaRepo, adquisicionRepo);
    }

    @Transactional
    public void cargarDatos(
            JpaVendedorRepository vendedorRepo,
            JpaClientePotencialRepository clienteRepo,
            JpaProductoRepository productoRepo,
            JpaProveedorRepository proveedorRepo,
            JpaExistenciaRepository existenciaRepo,
            JpaAdquisicionRepository adquisicionRepo) {

        log.info("=== Inicializando datos de demostración de PoliMarket ===");

        // --- Usuarios y Vendedores con credenciales, roles y permisos (password123) ---
        vendedorRepo.save(new VendedorEntity("admin.rrhh", "password123", true, "RECURSOS_HUMANOS", "GESTION_RRHH,CONSULTA_GENERAL"));
        vendedorRepo.save(new VendedorEntity("jperez", "password123", false, "VENDEDOR", "VENTAS,BODEGA"));
        vendedorRepo.save(new VendedorEntity("mgarcia", "password123", false, "VENDEDOR", "VENTAS,BODEGA"));
        vendedorRepo.save(new VendedorEntity("lrodriguez", "password123", false, "VENDEDOR", "VENTAS,BODEGA"));
        vendedorRepo.save(new VendedorEntity("acastro", "password123", true, "VENDEDOR", "VENTAS,BODEGA"));
        log.info("  → 5 cuentas cargadas (1 administrador de RRHH: admin.rrhh, 4 vendedores con acastro ya autorizado)");

        // --- Clientes potenciales con datos de contacto enriquecidos ---
        clienteRepo.save(new ClientePotencialEntity("Empresa ABC S.A.S.", "compras@empresaabc.com", "+57 310 444 8899", "Bogotá D.C."));
        clienteRepo.save(new ClientePotencialEntity("Comercial del Valle Ltda.", "adquisiciones@delvalle.com.co", "+57 315 222 3344", "Cali"));
        clienteRepo.save(new ClientePotencialEntity("Importaciones del Norte S.A.", "contacto@importnorte.com", "+57 301 777 5566", "Medellín"));
        clienteRepo.save(new ClientePotencialEntity("Grupo Industrial Andino", "ventas@grupoandino.org", "+57 320 999 1122", "Barranquilla"));
        clienteRepo.save(new ClientePotencialEntity("Soluciones Digitales S.A.S.", "gerencia@solucionesdigitales.co", "+57 318 666 0033", "Bucaramanga"));
        log.info("  → 5 clientes potenciales cargados con datos de contacto");

        // --- Proveedores ---
        ProveedorEntity prov1 = proveedorRepo.save(new ProveedorEntity("PROV-001", "TechDistribuciones S.A."));
        ProveedorEntity prov2 = proveedorRepo.save(new ProveedorEntity("PROV-002", "Suministros Globales Ltda."));
        ProveedorEntity prov3 = proveedorRepo.save(new ProveedorEntity("PROV-003", "ElectroComponentes S.A.S."));
        log.info("  → 3 proveedores cargados");

        // --- Productos ---
        ProductoEntity prod1 = productoRepo.save(new ProductoEntity("PROD-001", "Laptop HP ProBook"));
        ProductoEntity prod2 = productoRepo.save(new ProductoEntity("PROD-002", "Monitor Dell 27\""));
        ProductoEntity prod3 = productoRepo.save(new ProductoEntity("PROD-003", "Teclado Mecánico Logitech"));
        ProductoEntity prod4 = productoRepo.save(new ProductoEntity("PROD-004", "Mouse Inalámbrico Microsoft"));
        ProductoEntity prod5 = productoRepo.save(new ProductoEntity("PROD-005", "Impresora Epson L3250"));
        log.info("  → 5 productos cargados");

        // --- Existencias (PROD-003 agotado para demo de RF-05) ---
        existenciaRepo.save(new ExistenciaEntity(prod1, 25));
        existenciaRepo.save(new ExistenciaEntity(prod2, 15));
        existenciaRepo.save(new ExistenciaEntity(prod3, 0));   // ← Agotado
        existenciaRepo.save(new ExistenciaEntity(prod4, 50));
        existenciaRepo.save(new ExistenciaEntity(prod5, 8));
        log.info("  → 5 existencias cargadas (PROD-003 agotado para demo RF-05)");

        // --- Registros de adquisición (producto → proveedor) ---
        adquisicionRepo.save(new RegistroAdquisicionEntity(prod1, prov1));
        adquisicionRepo.save(new RegistroAdquisicionEntity(prod2, prov1));
        adquisicionRepo.save(new RegistroAdquisicionEntity(prod3, prov3));
        adquisicionRepo.save(new RegistroAdquisicionEntity(prod4, prov2));
        adquisicionRepo.save(new RegistroAdquisicionEntity(prod5, prov2));
        log.info("  → 5 registros de adquisición cargados");

        log.info("=== Datos de demostración cargados exitosamente ===");
    }

}
