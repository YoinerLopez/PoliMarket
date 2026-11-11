package com.polimarket.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Pruebas de Integración REST API - PoliMarket")
class ApiControllersIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    // =========================================================================
    // RF-01: Recursos Humanos
    // =========================================================================

    @Test
    @DisplayName("RF-01 Éxito: Autorizar vendedor existente y pendiente")
    void rf01_autorizarVendedorExitoso() throws Exception {
        mockMvc.perform(patch("/api/v1/recursos-humanos/vendedores/jperez/autorizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos.nombreUsuario").value("jperez"))
                .andExpect(jsonPath("$.datos.autorizado").value(true))
                .andExpect(jsonPath("$.errores").isEmpty());
    }

    @Test
    @DisplayName("RF-01 Error 404: Autorizar vendedor inexistente")
    void rf01_autorizarVendedorInexistente() throws Exception {
        mockMvc.perform(patch("/api/v1/recursos-humanos/vendedores/usuario_fantasma/autorizar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exitoso").value(false))
                .andExpect(jsonPath("$.errores", hasSize(1)));
    }

    @Test
    @DisplayName("RF-01 Error 409: Autorizar vendedor que ya estaba autorizado")
    void rf01_autorizarVendedorYaAutorizado() throws Exception {
        // 'acastro' viene precargado como autorizado en DataInitializer
        mockMvc.perform(patch("/api/v1/recursos-humanos/vendedores/acastro/autorizar"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.exitoso").value(false))
                .andExpect(jsonPath("$.errores", hasSize(1)));
    }

    @Test
    @DisplayName("Revocar: Debe revocar autorización a vendedor autorizado y tumbar su acceso")
    void rf01_revocarVendedorExitoso() throws Exception {
        // Revocamos 'lrodriguez' tras autorizarlo
        mockMvc.perform(patch("/api/v1/recursos-humanos/vendedores/lrodriguez/autorizar"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/recursos-humanos/vendedores/lrodriguez/revocar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos.autorizado").value(false));

        // Comprobar que en Ventas su sesión/acceso queda inmediatamente rechazado (403)
        mockMvc.perform(get("/api/v1/ventas/clientes-potenciales")
                        .param("nombreUsuario", "lrodriguez"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RRHH: Listar todos los vendedores y usuarios con sus roles y estados")
    void rf01_listarVendedores() throws Exception {
        mockMvc.perform(get("/api/v1/recursos-humanos/vendedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$.datos[?(@.nombreUsuario == 'admin.rrhh')].rol").value(hasItem("RECURSOS_HUMANOS")))
                .andExpect(jsonPath("$.datos[?(@.nombreUsuario == 'acastro')].autorizado").value(hasItem(true)));
    }

    // =========================================================================
    // RF-02: Ventas - Clientes Potenciales
    // =========================================================================

    @Test
    @DisplayName("RF-02 Éxito: Consultar clientes potenciales con vendedor autorizado")
    void rf02_consultarClientesConVendedorAutorizado() throws Exception {
        mockMvc.perform(get("/api/v1/ventas/clientes-potenciales")
                        .param("nombreUsuario", "acastro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$.datos[0].nombre").exists());
    }

    @Test
    @DisplayName("RF-02 Error 403: Vendedor pendiente no puede consultar clientes")
    void rf02_consultarClientesConVendedorPendiente() throws Exception {
        // 'mgarcia' está pendiente de autorización
        mockMvc.perform(get("/api/v1/ventas/clientes-potenciales")
                        .param("nombreUsuario", "mgarcia"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.exitoso").value(false))
                .andExpect(jsonPath("$.errores", hasSize(1)));
    }

    // =========================================================================
    // RF-03: Ventas / Bodega - Productos Disponibles
    // =========================================================================

    @Test
    @DisplayName("RF-03 Éxito: Consultar productos disponibles en Ventas con vendedor autorizado")
    void rf03_consultarProductosDisponiblesVentas() throws Exception {
        mockMvc.perform(get("/api/v1/ventas/productos-disponibles")
                        .param("nombreUsuario", "acastro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos", hasSize(4))) // 5 productos total, pero PROD-003 tiene 0 stock
                .andExpect(jsonPath("$.datos[*].codigoProducto", not(hasItem("PROD-003"))));
    }

    @Test
    @DisplayName("RF-03: Consultar existencias directas en Bodega")
    void rf03_consultarExistenciasBodega() throws Exception {
        mockMvc.perform(get("/api/v1/bodega/existencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos", hasSize(4)));
    }

    // =========================================================================
    // RF-04: Bodega / Proveedores - Adquisiciones
    // =========================================================================

    @Test
    @DisplayName("RF-04: Consultar productos adquiridos desde Bodega")
    void rf04_consultarAdquisicionesBodega() throws Exception {
        mockMvc.perform(get("/api/v1/bodega/adquisiciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos.contenido", hasSize(5)))
                .andExpect(jsonPath("$.datos.totalElementos").value(5))
                .andExpect(jsonPath("$.datos.contenido[0].nombreProveedor").exists());
    }

    @Test
    @DisplayName("RF-04: Listar adquisiciones desde Proveedores")
    void rf04_listarAdquisicionesProveedores() throws Exception {
        mockMvc.perform(get("/api/v1/proveedores/adquisiciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos", hasSize(5)));
    }

    // =========================================================================
    // RF-05: Bodega - Identificar Proveedor para Reposición
    // =========================================================================

    @Test
    @DisplayName("RF-05 Éxito: Identificar proveedor para producto agotado (PROD-003)")
    void rf05_identificarProveedorAgotado() throws Exception {
        mockMvc.perform(get("/api/v1/bodega/productos/PROD-003/proveedor-reposicion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos.codigo").value("PROV-003"))
                .andExpect(jsonPath("$.datos.nombre").value("ElectroComponentes S.A.S."));
    }

    @Test
    @DisplayName("RF-05 Error 409: Falla reposición si el producto no está agotado (PROD-001 tiene stock)")
    void rf05_fallaReposicionSiProductoTieneStock() throws Exception {
        mockMvc.perform(get("/api/v1/bodega/productos/PROD-001/proveedor-reposicion"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.exitoso").value(false));
    }

    @Test
    @DisplayName("RF-05: Buscar proveedor por producto en controlador Proveedores")
    void rf05_buscarProveedorPorProducto() throws Exception {
        mockMvc.perform(get("/api/v1/proveedores/productos/PROD-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.codigo").value("PROV-001"));
    }

    // =========================================================================
    // RF-07: Bodega - Registrar salida de producto
    // =========================================================================

    @Test
    @DisplayName("RF-07 Éxito: Descontar existencias al registrar salida de bodega")
    void rf07_registrarSalidaExitosa() throws Exception {
        mockMvc.perform(post("/api/v1/bodega/productos/PROD-004/salida")
                        .param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true));
    }

    @Test
    @DisplayName("RF-07 Error 400: Falla si se intenta retirar más unidades que las disponibles")
    void rf07_fallaSalidaStockInsuficiente() throws Exception {
        mockMvc.perform(post("/api/v1/bodega/productos/PROD-005/salida")
                        .param("cantidad", "9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exitoso").value(false));
    }

    // =========================================================================
    // Pruebas de i18n (Internacionalización) y Errores HTTP
    // =========================================================================

    @Test
    @DisplayName("i18n: Mensaje en inglés al enviar Accept-Language: en")
    void testI18nIngles() throws Exception {
        mockMvc.perform(patch("/api/v1/recursos-humanos/vendedores/usuario_fantasma/autorizar")
                        .header("Accept-Language", "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(containsString("No seller found")));
    }

    @Test
    @DisplayName("Manejador Global: Error 400 al faltar parámetro requerido")
    void testParametroRequeridoFaltante() throws Exception {
        mockMvc.perform(get("/api/v1/ventas/clientes-potenciales"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exitoso").value(false))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("Manejador Global: Error 405 en método HTTP no permitido")
    void testMetodoHttpNoSoportado() throws Exception {
        mockMvc.perform(delete("/api/v1/bodega/existencias"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.exitoso").value(false));
    }

    // =========================================================================
    // Autenticación (Login)
    // =========================================================================

    @Test
    @DisplayName("Auth Éxito: Login con credenciales válidas y vendedor autorizado")
    void auth_loginExitoso() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreUsuario\":\"acastro\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.datos.nombreUsuario").value("acastro"))
                .andExpect(jsonPath("$.datos.autorizado").value(true));
    }

    @Test
    @DisplayName("Auth Error 400: Contraseña errónea")
    void auth_passwordInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreUsuario\":\"acastro\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exitoso").value(false));
    }

    @Test
    @DisplayName("Auth Error 404: Usuario inexistente")
    void auth_usuarioNoExiste() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreUsuario\":\"no_existe\",\"password\":\"password123\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exitoso").value(false));
    }

    @Test
    @DisplayName("Auth Error 403: Vendedor no autorizado")
    void auth_vendedorNoAutorizado() throws Exception {
        // En DataInitializer, 'mgarcia' está registrado como no autorizado
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreUsuario\":\"mgarcia\",\"password\":\"password123\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.exitoso").value(false));
    }
}
