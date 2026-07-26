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

import mx.uam.ayd.proyecto.datos.DevolucionRepository;
import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Devolucion;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@ExtendWith(MockitoExtension.class)
class ServicioDevolucionTest {

    @Mock
    private DevolucionRepository devolucionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @InjectMocks
    private ServicioDevolucion servicioDevolucion;

    @Test
    void testRegistrarDevolucionDanado() {
        // Caso 1: Registro de devolución por daño exitoso
        long idProducto = 1L;
        int cantidadDevolucion = 2;
        String motivo = "Pieza con defecto de fábrica";

        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Desarmador");
        productoExistente.setExistenciaActual(10);

        Devolucion devolucionMock = new Devolucion();
        devolucionMock.setIdDevolucion(100L);

        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);
        when(devolucionRepository.save(any(Devolucion.class))).thenReturn(devolucionMock);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(new MovimientoInventario());

        Devolucion resultado = servicioDevolucion.registrarDevolucionDanado(idProducto, cantidadDevolucion, motivo);

        assertNotNull(resultado);
        verify(devolucionRepository, times(1)).save(any(Devolucion.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));

        // Caso 2: Intentar devolver una cantidad menor o igual a cero
        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, 0, motivo);
        });

        // Caso 3: Intentar devolver con motivo nulo o vacío
        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, 2, "   ");
        });

        // Caso 4: Intentar devolver un producto que no existe
        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idInexistente, 2, motivo);
        });

        // Caso 5: Intentar devolver más cantidad de la que existe en el inventario
        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, 15, motivo); // Stock actual es 10
        });
    }
}