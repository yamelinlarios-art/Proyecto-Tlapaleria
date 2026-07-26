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

@ExtendWith(MockitoExtension.class)
class ServicioMovimientoInventarioTest {

    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @InjectMocks
    private ServicioMovimientoInventario servicioMovimientoInventario;

    @Test
    void testObtenerMovimientos() {
        // Caso: Existen movimientos registrados
        List<MovimientoInventario> listaMock = new ArrayList<>();
        listaMock.add(new MovimientoInventario());
        
        when(movimientoInventarioRepository.findAllByOrderByFechaDesc()).thenReturn(listaMock);

        List<MovimientoInventario> resultado = servicioMovimientoInventario.obtenerMovimientos();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(movimientoInventarioRepository, times(1)).findAllByOrderByFechaDesc();
    }

    @Test
    void testBuscarMovimiento() {
        // Caso 1: Filtro nulo o vacío (debe retornar todos)
        List<MovimientoInventario> listaMock = new ArrayList<>();
        when(movimientoInventarioRepository.findAllByOrderByFechaDesc()).thenReturn(listaMock);

        List<MovimientoInventario> resultadoVacio = servicioMovimientoInventario.buscarMovimiento("");
        assertNotNull(resultadoVacio);

        // Caso 2: Filtro con texto válido
        String filtro = "CAMBIO_PRECIO";
        when(movimientoInventarioRepository.findByTipoMovimientoContainingIgnoreCaseOrderByFechaDesc(filtro))
            .thenReturn(listaMock);

        List<MovimientoInventario> resultadoFiltro = servicioMovimientoInventario.buscarMovimiento(filtro);
        assertNotNull(resultadoFiltro);
        verify(movimientoInventarioRepository, times(1))
            .findByTipoMovimientoContainingIgnoreCaseOrderByFechaDesc(filtro);
    }

    @Test
    void testConsultarDetalleMovimiento() {
        long idMovimiento = 1L;
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(idMovimiento);

        // Caso 1: Encuentra el movimiento
        when(movimientoInventarioRepository.findById(idMovimiento)).thenReturn(Optional.of(mov));
        MovimientoInventario resultado = servicioMovimientoInventario.consultarDetalleMovimiento(idMovimiento);
        
        assertNotNull(resultado);
        assertEquals(idMovimiento, resultado.getIdMovimiento());

        // Caso 2: No encuentra el movimiento (retorna null)
        when(movimientoInventarioRepository.findById(99L)).thenReturn(Optional.empty());
        MovimientoInventario resultadoNulo = servicioMovimientoInventario.consultarDetalleMovimiento(99L);
        
        assertNull(resultadoNulo);
    }

    @Test
    void testRegistrarMovimiento() {
        Producto producto = new Producto();
        producto.setNombre("Taladro");

        // Caso 1: Registrar movimiento de tipo CAMBIO_PRECIO con cantidad 0 de forma exitosa
        when(movimientoInventarioRepository.save(any(MovimientoInventario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoInventario resPrecio = servicioMovimientoInventario.registrarMovimiento(
            producto, 0, 10, 10, "CAMBIO_PRECIO", "Ajuste de precio"
        );
        assertNotNull(resPrecio);
        assertEquals("CAMBIO_PRECIO", resPrecio.getTipoMovimiento());

        // Caso 2: Registrar movimiento normal (ej. ENTRADA o DEVOLUCION) con cantidad válida
        MovimientoInventario resNormal = servicioMovimientoInventario.registrarMovimiento(
            producto, 5, 5, 10, "ENTRADA", "Nueva mercancía"
        );
        assertNotNull(resNormal);
        assertEquals(5, resNormal.getCantidad());

        // Caso 3: Intentar registrar movimiento con producto nulo (lanza excepción)
        assertThrows(IllegalArgumentException.class, () -> {
            servicioMovimientoInventario.registrarMovimiento(null, 5, 5, 10, "ENTRADA", "Error");
        });

        // Caso 4: Intentar registrar movimiento que no es cambio de precio con cantidad <= 0 (lanza excepción)
        assertThrows(IllegalArgumentException.class, () -> {
            servicioMovimientoInventario.registrarMovimiento(producto, 0, 5, 5, "ENTRADA", "Cantidad inválida");
        });
    }
}