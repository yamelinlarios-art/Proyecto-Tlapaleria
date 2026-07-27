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

/**
 * Pruebas unitarias para el ServicioDevolucion (HU-10).
 * Utiliza Mockito para aislar la lógica de negocio simulando los repositorios de datos.
 */
@ExtendWith(MockitoExtension.class)
class ServicioDevolucionTest {

    // Simulamos (Mock) los repositorios para no depender de una base de datos
    @Mock
    private DevolucionRepository devolucionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    // Inyectamos los mocks anteriores dentro de la clase que vamos a probar
    @InjectMocks
    private ServicioDevolucion servicioDevolucion;

    /**
     * Prueba 1: Valida el flujo completamente exitoso de una devolución por producto dañado.
     * Comprueba que se actualice el producto, se guarde la devolución y se registre en el historial.
     */
    @Test
    void testRegistrarDevolucionDanadoExitoso() {
        long idProducto = 1L;
        int cantidadDevolucion = 2;
        String motivo = "Pieza con defecto de fábrica";

        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Desarmador");
        productoExistente.setExistenciaActual(10);

        Devolucion devolucionMock = new Devolucion();
        devolucionMock.setIdDevolucion(100L);

        // Configuración de los comportamientos simulados (Mocks)
        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);
        when(devolucionRepository.save(any(Devolucion.class))).thenReturn(devolucionMock);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(new MovimientoInventario());

        // Ejecución del método a probar
        Devolucion resultado = servicioDevolucion.registrarDevolucionDanado(idProducto, cantidadDevolucion, motivo);

        // Validaciones
        assertNotNull(resultado, "El objeto devolución devuelto no debe ser nulo");
        // Verifica que los métodos guardados se hayan ejecutado exactamente una vez
        verify(devolucionRepository, times(1)).save(any(Devolucion.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));
    }

    /**
     * Prueba 2: Valida que el sistema rechace la operación lanzando una excepción 
     * cuando se envían datos incompletos, cantidades inválidas (<= 0), motivos vacíos 
     * o un producto que no existe en la base de datos.
     */
    @Test
    void testRegistrarDevolucionDatosIncompletosONulos() {
        long idProducto = 1L;
        String motivo = "Pieza con defecto de fábrica";

        // Caso A: Cantidad cero o negativa debe lanzar IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, 0, motivo);
        });

        // Caso B: Motivo vacío o con puros espacios en blanco debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, 2, "   ");
        });

        // Caso C: Producto inexistente (el repositorio regresa null) debe lanzar excepción
        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idInexistente, 2, motivo);
        });
    }

    /**
     * Prueba 3: Valida la regla de negocio que impide devolver más piezas 
     * de las que realmente hay registradas en el inventario actual (Control de stock).
     */
    @Test
    void testRegistrarDevolucionSuperaExistenciaActual() {
        long idProducto = 1L;
        int cantidadDevolucion = 15; // Se intentan devolver 15 piezas, superando la existencia actual
        String motivo = "Pieza con defecto de fábrica";

        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Desarmador");
        productoExistente.setExistenciaActual(10); // excede la existencia actual

        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);

        // Se espera que falle y lance la excepción por stock insuficiente
        assertThrows(IllegalArgumentException.class, () -> {
            servicioDevolucion.registrarDevolucionDanado(idProducto, cantidadDevolucion, motivo);
        });
    }
}