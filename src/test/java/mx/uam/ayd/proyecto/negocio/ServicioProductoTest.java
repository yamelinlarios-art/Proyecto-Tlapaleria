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
    void testActualizarPrecioProducto() {
        // Caso 1: Actualizar precio con éxito (precio válido y producto existente)
        long idProducto = 1L;
        double nuevoPrecio = 55.50;

        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Martillo");
        productoExistente.setPrecio(40.00);
        productoExistente.setExistenciaActual(10);

        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(new MovimientoInventario());

        Producto resultado = servicioProducto.actualizarPrecioProducto(idProducto, nuevoPrecio);

        assertNotNull(resultado);
        assertEquals(nuevoPrecio, resultado.getPrecio());
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));

        // Caso 2: Intentar actualizar con un precio inválido (menor o igual a 0)
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, 0.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, -10.50);
        });

        // Caso 3: Intentar actualizar un producto que no existe en la base de datos
        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idInexistente, 50.0);
        });
    }
}