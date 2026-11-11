package com.polimarket.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Adaptadores de Repositorio")
class AdaptersTest {

    @Mock
    private JpaVendedorRepository jpaVendedorRepo;

    @Mock
    private JpaClientePotencialRepository jpaClienteRepo;

    @Mock
    private JpaExistenciaRepository jpaExistenciaRepo;

    @Mock
    private JpaAdquisicionRepository jpaAdquisicionRepo;

    @InjectMocks
    private VendedorRepositoryAdapter vendedorAdapter;

    @InjectMocks
    private ClientePotencialRepositoryAdapter clienteAdapter;

    @InjectMocks
    private ExistenciaRepositoryAdapter existenciaAdapter;

    @InjectMocks
    private AdquisicionRepositoryAdapter adquisicionAdapter;

    @Test
    void testVendedorAdapter() {
        VendedorEntity entity = new VendedorEntity("jperez", false);
        when(jpaVendedorRepo.findByNombreUsuario("jperez")).thenReturn(Optional.of(entity));

        Optional<Vendedor> res = vendedorAdapter.buscarPorNombreUsuario("jperez");
        assertTrue(res.isPresent());
        assertEquals("jperez", res.get().getNombreUsuario());

        vendedorAdapter.guardar(new Vendedor("jperez", true));
        verify(jpaVendedorRepo).save(any());
    }

    @Test
    void testClienteAdapter() {
        ClientePotencialEntity entity = new ClientePotencialEntity("Cliente 1");
        entity.setId(1L);
        when(jpaClienteRepo.findAll()).thenReturn(List.of(entity));

        List<ClientePotencial> lista = clienteAdapter.listar();
        assertEquals(1, lista.size());
        assertEquals("Cliente 1", lista.get(0).getNombre());
    }

    @Test
    void testExistenciaAdapter() {
        ProductoEntity p = new ProductoEntity("PROD-1", "Laptop");
        ExistenciaEntity e = new ExistenciaEntity(p, 5);

        when(jpaExistenciaRepo.findAll()).thenReturn(List.of(e));
        when(jpaExistenciaRepo.findByProductoCodigo("PROD-1")).thenReturn(Optional.of(e));

        List<Existencia> lista = existenciaAdapter.listar();
        assertEquals(1, lista.size());

        Optional<Existencia> find = existenciaAdapter.buscarPorCodigoProducto("PROD-1");
        assertTrue(find.isPresent());

        existenciaAdapter.guardar(new Existencia(new Producto("PROD-1", "Laptop"), 3));
        verify(jpaExistenciaRepo).save(e);
    }

    @Test
    void testAdquisicionAdapter() {
        ProductoEntity p = new ProductoEntity("P1", "Mouse");
        ProveedorEntity prov = new ProveedorEntity("PR1", "Distribuidora");
        RegistroAdquisicionEntity reg = new RegistroAdquisicionEntity(p, prov);
        reg.setId(10L);

        when(jpaAdquisicionRepo.findAll()).thenReturn(List.of(reg));
        when(jpaAdquisicionRepo.findByProductoCodigo("P1")).thenReturn(Optional.of(reg));

        List<RegistroAdquisicion> list = adquisicionAdapter.listar();
        assertEquals(1, list.size());

        when(jpaAdquisicionRepo.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(reg)));
        when(jpaAdquisicionRepo.count()).thenReturn(1L);

        List<RegistroAdquisicion> paginado = adquisicionAdapter.listarPaginado(0, 10);
        assertEquals(1, paginado.size());
        assertEquals(1L, adquisicionAdapter.contarTotal());

        Optional<RegistroAdquisicion> find = adquisicionAdapter.buscarPorCodigoProducto("P1");
        assertTrue(find.isPresent());
    }
}
