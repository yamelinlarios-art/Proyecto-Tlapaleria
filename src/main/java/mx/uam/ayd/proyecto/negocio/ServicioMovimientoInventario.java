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
 * de inventario (historial general de operaciones del sistema).
 *
 * @author Yael Mora Simón, Yamelin Larios Nepomuseno
 */
@Service
public class ServicioMovimientoInventario {

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    /**
     * Recupera todos los movimientos registrados en la base de datos,
     * ordenándolos del más reciente al más antiguo para mostrarlos en la vista.
     *
     * @return lista completa de movimientos ordenada por fecha descendente
     */
    public List<MovimientoInventario> obtenerMovimientos() {
        return movimientoInventarioRepository.findAllByOrderByFechaDesc();
    }

    /**
     * Busca y filtra los movimientos según el tipo especificado (ej. filtrar por devoluciones o precios).
     * Si el filtro viene vacío o nulo, devuelve la lista completa.
     *
     * @param filtro texto o palabra clave para buscar en el tipo de movimiento
     * @return lista de movimientos que coinciden con el filtro
     */
    public List<MovimientoInventario> buscarMovimiento(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return obtenerMovimientos();
        }

        return movimientoInventarioRepository
                .findByTipoMovimientoContainingIgnoreCaseOrderByFechaDesc(filtro);
    }

    /**
     * Recupera un movimiento específico de la bitácora por su identificador único.
     * Utiliza Optional para manejar de forma segura si el registro existe o no.
     *
     * @param idMovimiento identificador del movimiento a consultar
     * @return el objeto MovimientoInventario encontrado, o null si no existe
     */
    public MovimientoInventario consultarDetalleMovimiento(long idMovimiento) {
        Optional<MovimientoInventario> movimiento =
                movimientoInventarioRepository.findById(idMovimiento);

        return movimiento.orElse(null);
    }

    /**
     * Registra un nuevo movimiento en el historial del inventario.
     * - Se usa para HU-10: Registra la salida por devolución de material dañado y las existencias afectadas.
     * - Se usa para HU-09: Registra el cambio de precio 
     *
     * @param producto Producto afectado por el movimiento
     * @param cantidad Cantidad de piezas devueltas o ajustadas (0 si es cambio de precio)
     * @param existenciaAnterior Stock que tenía el producto antes de la operación
     * @param existenciaActual Stock final resultante después de la operación
     * @param tipoMovimiento Tipo de movimiento ("DEVOLUCION_DAÑADO" para HU-10, "CAMBIO_PRECIO" para HU-09)
     * @param motivo Justificación, observación o motivo del movimiento
     * @return El objeto MovimientoInventario ya guardado y persistido en la base de datos
     */
    @Transactional
    public MovimientoInventario registrarMovimiento(Producto producto, int cantidad, int existenciaAnterior, int existenciaActual, String tipoMovimiento, String motivo) {
        
        // Validación 1: El producto no puede venir vacío
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo al registrar el movimiento.");
        }
        
        // Excepción lógica para la HU-09: Un cambio de precio no altera piezas del inventario.
        boolean esCambioPrecio = "CAMBIO_PRECIO".equalsIgnoreCase(tipoMovimiento);

        // Validación 2: Si no es un cambio de precio, la cantidad de piezas afectadas obligatoriamente debe ser mayor a cero
        if (!esCambioPrecio && cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor a cero.");
        }

        // Construcción y almacenamiento del objeto de bitácora
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setExistenciaAnterior(existenciaAnterior);
        movimiento.setExistenciaActual(existenciaActual);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(motivo);

        return movimientoInventarioRepository.save(movimiento);
    }
}