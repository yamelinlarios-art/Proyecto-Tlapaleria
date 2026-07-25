package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Servicio encargado de la lógica de negocio de los movimientos
 * de inventario.
 *
 * @author Yael Mora Simón, Yamelin Larios Nepomuseno
 */
@Service
public class ServicioMovimientoInventario {

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    /**
     * Recupera todos los movimientos registrados.
     *
     * @return lista de movimientos
     */
    public List<MovimientoInventario> obtenerMovimientos() {

        return movimientoInventarioRepository.findAllByOrderByFechaDesc();
    }

    /**
     * Busca movimientos por tipo.
     *
     * @param filtro texto del filtro
     * @return lista de movimientos encontrados
     */
    public List<MovimientoInventario> buscarMovimiento(String filtro) {

        if (filtro == null || filtro.trim().isEmpty()) {
            return obtenerMovimientos();
        }

        return movimientoInventarioRepository
                .findByTipoMovimientoContainingIgnoreCaseOrderByFechaDesc(filtro);
    }

    /**
     * Recupera un movimiento por su identificador.
     *
     * @param idMovimiento identificador del movimiento
     * @return movimiento encontrado o null
     */
    public MovimientoInventario consultarDetalleMovimiento(long idMovimiento) {

        Optional<MovimientoInventario> movimiento =
                movimientoInventarioRepository.findById(idMovimiento);

        return movimiento.orElse(null);
    }

    /**
     * Registra un nuevo movimiento en el historial/bitácora del inventario.
     * Requerido para HU-10: Registrar devolución por material dañado.
     *
     * @param producto Producto afectado por el movimiento
     * @param cantidad Cantidad devuelta o ajustada
     * @param existenciaAnterior Stock antes de la devolución
     * @param existenciaActual Stock final después de la devolución
     * @param tipoMovimiento Tipo de movimiento (ej. "DEVOLUCION_DANADO")
     * @param motivo Justificación/Observación del movimiento
     * @return MovimientoInventario guardado en la base de datos
     */
    @Transactional
    public MovimientoInventario registrarMovimiento(Producto producto, int cantidad, int existenciaAnterior, int existenciaActual, String tipoMovimiento, String motivo) {
        
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo al registrar el movimiento.");
        }
        
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor a cero.");
        }

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setExistenciaAnterior(existenciaAnterior);
        movimiento.setExistenciaActual(existenciaActual);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(motivo); // Usamos setObservacion de tu entidad

        return movimientoInventarioRepository.save(movimiento);
    }

}