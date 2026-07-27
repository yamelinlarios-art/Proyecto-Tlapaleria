package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Pruebas unitarias para el ServicioMovimientoInventario.
 * Verifica la consulta, filtrado, detalle y registro de los movimientos en el historial del sistema.
 */
@ExtendWith(MockitoExtension.class)
class ServicioMovimientoInventarioTest {

    // Simulamos el repositorio de movimientos de inventario
    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    // Inyectamos el mock en el servicio que vamos a probar
    @InjectMocks
    private ServicioMovimientoInventario servicioMovimientoInventario;

    /**
     * Prueba 1: Valida la obtención de todos los movimientos ordenados por fecha descendiente.
     */
    @Test
    void testObtenerMovimientos() {
        List<MovimientoInventario> listaMock = new ArrayList<>();
        listaMock.add(new MovimientoInventario());
        
        when(movimientoInventarioRepository.findAllByOrderByFechaDesc()).thenReturn(listaMock);

        List<MovimientoInventario> resultado = servicioMovimientoInventario.obtenerMovimientos();
        
        assertNotNull(resultado, "La lista de movimientos no debe ser nula");
        assertEquals(1, resultado.size(), "Debe regresar exactamente un movimiento simulado");
        verify(movimientoInventarioRepository, times(1)).findAllByOrderByFechaDesc();
    }

    /**
     * Prueba 2: Valida la funcionalidad de búsqueda/filtrado de movimientos por tipo.
     * Comprueba que si se manda un filtro vacío o un texto específico, el repositorio responda adecuadamente.
     */
    @Test
    void testBuscarMovimiento() {
        List<MovimientoInventario> listaMock = new ArrayList<>();
        when(movimientoInventarioRepository.findAllByOrderByFechaDesc()).thenReturn(listaMock);

        // Caso A: Filtro vacío o nulo debe regresar la lista general ordenada por fecha
        List<MovimientoInventario> resultadoVacio = servicioMovimientoInventario.buscarMovimiento("");
        assertNotNull(resultadoVacio);

        // Caso B: Búsqueda con un filtro específico (por ejemplo, cambios de precio)
        String filtro = "CAMBIO_PRECIO";
        when(movimientoInventarioRepository.findByTipoMovimientoContainingIgnoreCaseOrderByFechaDesc(filtro))
            .thenReturn(listaMock);

        List<MovimientoInventario> resultadoFiltro = servicioMovimientoInventario.buscarMovimiento(filtro);
        assertNotNull(resultadoFiltro);
        verify(movimientoInventarioRepository, times(1))
            .findByTipoMovimientoContainingIgnoreCaseOrderByFechaDesc(filtro);
    }

    /**
     * Prueba 3: Valida la consulta del detalle de un movimiento específico por su ID.
     * Maneja tanto el escenario donde existe como cuando no se encuentra (regresa null o Optional vacío).
     */
    @Test
    void testConsultarDetalleMovimiento() {
        long idMovimiento = 1L;
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(idMovimiento);

        // Caso A: El movimiento sí existe
        when(movimientoInventarioRepository.findById(idMovimiento)).thenReturn(Optional.of(mov));
        MovimientoInventario resultado = servicioMovimientoInventario.consultarDetalleMovimiento(idMovimiento);
        
        assertNotNull(resultado);
        assertEquals(idMovimiento, resultado.getIdMovimiento());

        // Caso B: El movimiento no existe (ID 99)
        when(movimientoInventarioRepository.findById(99L)).thenReturn(Optional.empty());
        MovimientoInventario resultadoNulo = servicioMovimientoInventario.consultarDetalleMovimiento(99L);
        
        assertNull(resultadoNulo, "Debe retornar null si el movimiento no se encuentra");
    }

    /**
     * Prueba 4: Valida el registro correcto de nuevos movimientos en la bitácora 
     * y las validaciones de datos obligatorios (como evitar productos nulos o cantidades inválidas).
     */
    @Test
    void testRegistrarMovimiento() {
        Producto producto = new Producto();
        producto.setNombre("Taladro");

        // Configuramos para que el save regrese exactamente el objeto que recibe
        when(movimientoInventarioRepository.save(any(MovimientoInventario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Caso A: Registro exitoso de un movimiento de tipo cambio de precio
        MovimientoInventario resPrecio = servicioMovimientoInventario.registrarMovimiento(
            producto, 0, 10, 10, "CAMBIO_PRECIO", "Ajuste de precio"
        );
        assertNotNull(resPrecio);
        assertEquals("CAMBIO_PRECIO", resPrecio.getTipoMovimiento());

        // Caso B: Registro exitoso de un movimiento normal (por ejemplo, devolución)
        MovimientoInventario resNormal = servicioMovimientoInventario.registrarMovimiento(
            producto, 5, 5, 10, "DEVOLUCION_DANADO", "Devolución de pieza"
        );
        assertNotNull(resNormal);
        assertEquals(5, resNormal.getCantidad());

        // Caso C: Debe lanzar excepción si el producto asociado es nulo
        assertThrows(IllegalArgumentException.class, () -> {
            servicioMovimientoInventario.registrarMovimiento(null, 5, 5, 10, "DEVOLUCION_DANADO", "Error");
        });

        // Caso D: Debe lanzar excepción si la cantidad o los datos no cumplen con las reglas
        assertThrows(IllegalArgumentException.class, () -> {
            servicioMovimientoInventario.registrarMovimiento(producto, 0, 5, 5, "DEVOLUCION_DANADO", "Cantidad inválida");
        });
    }
}