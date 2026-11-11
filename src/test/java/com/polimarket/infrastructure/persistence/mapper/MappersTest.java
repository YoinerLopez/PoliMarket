package com.polimarket.infrastructure.persistence.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.polimarket.domain.model.ClientePotencial;
import com.polimarket.domain.model.Existencia;
import com.polimarket.domain.model.Producto;
import com.polimarket.domain.model.Proveedor;
import com.polimarket.domain.model.RegistroAdquisicion;
import com.polimarket.domain.model.Vendedor;
import com.polimarket.infrastructure.persistence.entity.ClientePotencialEntity;
import com.polimarket.infrastructure.persistence.entity.ExistenciaEntity;
import com.polimarket.infrastructure.persistence.entity.ProductoEntity;
import com.polimarket.infrastructure.persistence.entity.ProveedorEntity;
import com.polimarket.infrastructure.persistence.entity.RegistroAdquisicionEntity;
import com.polimarket.infrastructure.persistence.entity.VendedorEntity;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Mappers de Persistencia")
class MappersTest {

    @Test
    @DisplayName("Debe mapear Vendedor <-> VendedorEntity")
    void testVendedorMapper() {
        Vendedor domain = new Vendedor("admin.rrhh", "pass123", true, "RECURSOS_HUMANOS", "GESTION_RRHH");
        VendedorEntity entity = VendedorMapper.toEntity(domain);

        assertEquals("admin.rrhh", entity.getNombreUsuario());
        assertTrue(entity.isAutorizado());
        assertEquals("RECURSOS_HUMANOS", entity.getRol());
        assertEquals("GESTION_RRHH", entity.getPermisos());

        Vendedor reconverted = VendedorMapper.toDomain(entity);
        assertEquals(domain.getNombreUsuario(), reconverted.getNombreUsuario());
        assertEquals(domain.estaAutorizado(), reconverted.estaAutorizado());
        assertEquals("RECURSOS_HUMANOS", reconverted.getRol());

        assertNull(VendedorMapper.toDomain(null));
        assertNull(VendedorMapper.toEntity(null));
    }

    @Test
    @DisplayName("Debe mapear ClientePotencialEntity -> ClientePotencial")
    void testClientePotencialMapper() {
        ClientePotencialEntity entity = new ClientePotencialEntity("Cliente Test");
        entity.setId(5L);

        ClientePotencial domain = ClientePotencialMapper.toDomain(entity);
        assertEquals(5L, domain.getId());
        assertEquals("Cliente Test", domain.getNombre());
    }

    @Test
    @DisplayName("Debe mapear ProductoEntity -> Producto")
    void testProductoMapper() {
        ProductoEntity entity = new ProductoEntity("P1", "Laptop");
        Producto domain = ProductoMapper.toDomain(entity);

        assertEquals("P1", domain.getCodigo());
        assertEquals("Laptop", domain.getNombre());
    }

    @Test
    @DisplayName("Debe mapear ProveedorEntity -> Proveedor")
    void testProveedorMapper() {
        ProveedorEntity entity = new ProveedorEntity("PR1", "Proveedor Test");
        Proveedor domain = ProveedorMapper.toDomain(entity);

        assertEquals("PR1", domain.getCodigo());
        assertEquals("Proveedor Test", domain.getNombre());
    }

    @Test
    @DisplayName("Debe mapear ExistenciaEntity -> Existencia y actualizar entidad")
    void testExistenciaMapper() {
        ProductoEntity prodEntity = new ProductoEntity("P1", "Laptop");
        ExistenciaEntity entity = new ExistenciaEntity(prodEntity, 15);
        entity.setId(10L);

        Existencia domain = ExistenciaMapper.toDomain(entity);
        assertEquals("P1", domain.getProducto().getCodigo());
        assertEquals(15, domain.getCantidadDisponible());

        domain.descontar(5);
        ExistenciaMapper.updateEntity(entity, domain);
        assertEquals(10, entity.getCantidadDisponible());
    }

    @Test
    @DisplayName("Debe mapear RegistroAdquisicionEntity -> RegistroAdquisicion")
    void testRegistroAdquisicionMapper() {
        ProductoEntity prodEntity = new ProductoEntity("P1", "Laptop");
        ProveedorEntity provEntity = new ProveedorEntity("PR1", "TechDist");
        RegistroAdquisicionEntity entity = new RegistroAdquisicionEntity(prodEntity, provEntity);
        entity.setId(99L);

        RegistroAdquisicion domain = RegistroAdquisicionMapper.toDomain(entity);
        assertEquals(99L, domain.getId());
        assertEquals("P1", domain.getProducto().getCodigo());
        assertEquals("PR1", domain.getProveedor().getCodigo());
    }
}
