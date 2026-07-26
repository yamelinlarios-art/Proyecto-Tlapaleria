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
    void testRegistrarDevolucionDanadoExitoso() {
        // Caso 1: Probar el registro exitoso de una devolución por producto dañado[cite: 2]
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
    }

    @Test
    void testRegistrarDevolucionDatosIncompletosONulos() {
        // Caso 2: Probar el comportamiento ante datos incompletos o nulos[cite: 2]
        long idProducto = 1L;
        String motivo = "Pieza con defecto de fábrica";

        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, 0, motivo);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, 2, "   ");
        });

        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idInexistente, 2, motivo);
        });
    }

    @Test
    void testRegistrarDevolucionSuperaExistenciaActual() {
        // Caso 3: Probar el rechazo cuando la cantidad a devolver supera la cantidad original/stock[cite: 2]
        long idProducto = 1L;
        int cantidadDevolucion = 15;
        String motivo = "Pieza con defecto de fábrica";

        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Desarmador");
        productoExistente.setExistenciaActual(10);

        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, cantidadDevolucion, motivo);
        });
    }
}