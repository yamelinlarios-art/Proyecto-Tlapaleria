package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.InventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@ExtendWith(MockitoExtension.class)
class ServicioInventarioTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ServicioInventario servicioInventario;

    @Test
    @DisplayName("Debería regresar lista de productos cuando existen alertas por stock bajo")
    void testConsultarAlertasConProductosBajoStock() {
        // Given
        List<Inventario> inventarios = new ArrayList<>();
        Inventario itemAlerta = new Inventario();
        itemAlerta.setIdProducto(1L);
        itemAlerta.setExistenciaActual(5);
        itemAlerta.setStockMinimo(10);
        inventarios.add(itemAlerta);

        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Martillo");

        when(inventarioRepository.findAll()).thenReturn(inventarios);
        when(productoRepository.findByIdProducto(1L)).thenReturn(producto);

        // When
        List<Producto> resultado = servicioInventario.consultarAlertas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getExistenciaActual());
        verify(inventarioRepository).findAll();
    }

    @Test
    @DisplayName("Debería regresar una lista vacía cuando no hay productos con stock por debajo del mínimo")
    void testConsultarAlertasSinProductosEnRojo() {
        // Given
        List<Inventario> inventarios = new ArrayList<>();
        Inventario itemSuficiente = new Inventario();
        itemSuficiente.setExistenciaActual(20);
        itemSuficiente.setStockMinimo(10);
        inventarios.add(itemSuficiente);

        when(inventarioRepository.findAll()).thenReturn(inventarios);

        // When
        List<Producto> resultado = servicioInventario.consultarAlertas();

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Debería crear un producto temporal cuando no existe en la base de datos")
    void testConsultarAlertasProductoInexistenteCreaObjeto() {
        // Given
        List<Inventario> inventarios = new ArrayList<>();
        Inventario item = new Inventario();
        item.setIdProducto(99L);
        item.setExistenciaActual(2);
        item.setStockMinimo(5);
        inventarios.add(item);

        when(inventarioRepository.findAll()).thenReturn(inventarios);
        when(productoRepository.findByIdProducto(99L)).thenReturn(null);
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        List<Producto> resultado = servicioInventario.consultarAlertas();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("PROD-099", resultado.get(0).getClave());
    }

    @Test
    @DisplayName("Debería obtener el detalle completo del producto correctamente")
    void testObtenerDetalleProductoExitoso() {
        // Given
        Producto martillo = new Producto();
        martillo.setIdProducto(1L);
        martillo.setNombre("Martillo");

        Inventario inv = new Inventario();
        inv.setExistenciaActual(15);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(martillo));
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));

        // When
        Producto resultado = servicioInventario.obtenerDetalleProducto(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(15, resultado.getExistenciaActual());
    }

    @Test
    @DisplayName("Debería regresar 0 si el registro de stock mínimo no existe")
    void testObtenerStockMinimoInexistenteRegresaCero() {
        // Given
        when(inventarioRepository.findById(500L)).thenReturn(Optional.empty());

        // When
        int stockMin = servicioInventario.obtenerStockMinimo(500L);

        // Then
        assertEquals(0, stockMin);
    }
}