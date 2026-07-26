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
        List<MovimientoInventario> listaMock = new ArrayList<>();
        when(movimientoInventarioRepository.findAllByOrderByFechaDesc()).thenReturn(listaMock);

        List<MovimientoInventario> resultadoVacio = servicioMovimientoInventario.buscarMovimiento("");
        assertNotNull(resultadoVacio);

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

        when(movimientoInventarioRepository.findById(idMovimiento)).thenReturn(Optional.of(mov));
        MovimientoInventario resultado = servicioMovimientoInventario.consultarDetalleMovimiento(idMovimiento);
        
        assertNotNull(resultado);
        assertEquals(idMovimiento, resultado.getIdMovimiento());

        when(movimientoInventarioRepository.findById(99L)).thenReturn(Optional.empty());
        MovimientoInventario resultadoNulo = servicioMovimientoInventario.consultarDetalleMovimiento(99L);
        
        assertNull(resultadoNulo);
    }

    @Test
    void testRegistrarMovimiento() {
        Producto producto = new Producto();
        producto.setNombre("Taladro");

        when(movimientoInventarioRepository.save(any(MovimientoInventario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoInventario resPrecio = servicioMovimientoInventario.registrarMovimiento(
            producto, 0, 10, 10, "CAMBIO_PRECIO", "Ajuste de precio"
        );
        assertNotNull(resPrecio);
        assertEquals("CAMBIO_PRECIO", resPrecio.getTipoMovimiento());

        MovimientoInventario resNormal = servicioMovimientoInventario.registrarMovimiento(
            producto, 5, 5, 10, "DEVOLUCION_DANADO", "Devolución de pieza"
        );
        assertNotNull(resNormal);
        assertEquals(5, resNormal.getCantidad());

        assertThrows(IllegalArgumentException.class, () -> {
            servicioMovimientoInventario.registrarMovimiento(null, 5, 5, 10, "DEVOLUCION_DANADO", "Error");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            servicioMovimientoInventario.registrarMovimiento(producto, 0, 5, 5, "DEVOLUCION_DANADO", "Cantidad inválida");
        });
    }
}