package com.polimarket.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Modelo de Dominio: Proveedor y RegistroAdquisicion")
class ProveedorYAdquisicionTest {

    @Test
    @DisplayName("Debe crear proveedor correctamente y validar equals/hashCode")
    void debeCrearProveedor() {
        Proveedor prov1 = new Proveedor("PROV-001", "TechDist");
        Proveedor prov2 = new Proveedor("PROV-001", "Otra Razon");
        Proveedor prov3 = new Proveedor("PROV-002", "TechDist");

        assertEquals("PROV-001", prov1.getCodigo());
        assertEquals("TechDist", prov1.getNombre());

        assertEquals(prov1, prov1);
        assertEquals(prov1, prov2);
        assertNotEquals(prov1, prov3);
        assertNotEquals(prov1, null);
        assertNotEquals(prov1, "otro");
        assertEquals(prov1.hashCode(), prov2.hashCode());

        assertThrows(NullPointerException.class, () -> new Proveedor(null, "Tech"));
        assertThrows(NullPointerException.class, () -> new Proveedor("PROV-001", null));
    }

    @Test
    @DisplayName("Debe crear registro de adquisición y consultar proveedor")
    void debeCrearRegistroAdquisicion() {
        Producto prod = new Producto("PROD-001", "Laptop");
        Proveedor prov = new Proveedor("PROV-001", "TechDist");

        RegistroAdquisicion reg = new RegistroAdquisicion(100L, prod, prov);

        assertEquals(100L, reg.getId());
        assertEquals(prod, reg.getProducto());
        assertEquals(prov, reg.getProveedor());
        assertEquals(prov, reg.consultarProveedor());

        RegistroAdquisicion reg2 = new RegistroAdquisicion(100L, prod, prov);
        RegistroAdquisicion reg3 = new RegistroAdquisicion(200L, prod, prov);

        assertEquals(reg, reg);
        assertEquals(reg, reg2);
        assertNotEquals(reg, reg3);
        assertNotEquals(reg, null);
        assertNotEquals(reg, "objeto");
        assertEquals(reg.hashCode(), reg2.hashCode());

        assertThrows(NullPointerException.class, () -> new RegistroAdquisicion(1L, null, prov));
        assertThrows(NullPointerException.class, () -> new RegistroAdquisicion(1L, prod, null));
    }
}
