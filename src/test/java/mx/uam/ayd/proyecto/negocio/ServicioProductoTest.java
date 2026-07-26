package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@ExtendWith(MockitoExtension.class)
class ServicioProductoTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @InjectMocks
    private ServicioProducto servicioProducto;

    @Test
    void testActualizarPrecioProductoExitoso() {
        // Caso 1: Probar la actualización exitosa del precio de un producto[cite: 2]
        long idProducto = 1L;
        double nuevoPrecio = 55.50;

        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Martillo");
        productoExistente.setPrecio(40.00);

        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(new MovimientoInventario());

        Producto resultado = servicioProducto.actualizarPrecioProducto(idProducto, nuevoPrecio);

        assertNotNull(resultado);
        assertEquals(nuevoPrecio, resultado.getPrecio());
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));
    }

    @Test
    void testActualizarPrecioProductoInexistente() {
        // Caso 2: Probar el fallo al actualizar un producto que no existe[cite: 2]
        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idInexistente, 50.0);
        });
    }

    @Test
    void testActualizarPrecioValoresInvalidos() {
        // Caso 3: Probar la validación con precios inválidos o nulos ($\le 0$)[cite: 2]
        long idProducto = 1L;
        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        
        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, 0.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, -5.0);
        });
    }
}