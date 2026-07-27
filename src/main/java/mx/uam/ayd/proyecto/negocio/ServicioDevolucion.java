package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.DevolucionRepository;
import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Devolucion;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Servicio encargado de la lógica de negocio para la devolución de productos dañados (HU-10).
 * Maneja las validaciones de stock, actualización de existencias y registro en historial.
 *
 * @author Yamelin Larios Nepomuseno
 */
@Service
public class ServicioDevolucion {

    private static final Logger log = LoggerFactory.getLogger(ServicioDevolucion.class);

    private final DevolucionRepository devolucionRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    /**
     * Inyección de dependencias de los repositorios necesarios para procesar la devolución.
     */
    @Autowired
    public ServicioDevolucion(DevolucionRepository devolucionRepository, 
                            ProductoRepository productoRepository,
                            MovimientoInventarioRepository movimientoRepository) {
        this.devolucionRepository = devolucionRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Registra una devolución por producto dañado cumpliendo con la HU-10.
     * 1. Valida que la cantidad sea mayor a cero y que el motivo no esté vacío.
     * 2. Busca el producto en la base de datos y comprueba que exista suficiente stock.
     * 3. Descuenta las piezas dañadas del inventario actual del producto.
     * 4. Guarda el ticket o registro formal de la devolución.
     * 5. Genera de forma automática un registro en la bitácora (MovimientoInventario) 
     *    para mantener la trazabilidad y auditoría.
     * 
     * @param idProducto Identificador del producto a devolver
     * @param cantidad Número de piezas que se van a descontar por daño
     * @param motivo Explicación o razón del daño (ej. empaque roto)
     * @return El objeto Devolucion guardado exitosamente
     */
    @Transactional
    public Devolucion registrarDevolucionDanado(long idProducto, int cantidad, String motivo) {
        log.info("Iniciando proceso de devolución por daño para producto ID: {}, Cantidad: {}", idProducto, cantidad);

        // Validación 1: Asegura que la cantidad sea lógica (positiva)
        if (cantidad <= 0) {
            log.warn("La cantidad a devolver debe ser mayor a cero: {}", cantidad);
            throw new IllegalArgumentException("La cantidad a devolver debe ser mayor a cero.");
        }

        // Validación 2: Asegura que el usuario escribió una razón obligatoria
        if (motivo == null || motivo.trim().isEmpty()) {
            log.warn("El motivo de la devolución es obligatorio");
            throw new IllegalArgumentException("Debes especificar el motivo de la devolución por daño.");
        }

        // Validación 3: Busca que el producto realmente exista en la base de datos
        Producto producto = productoRepository.findByIdProducto(idProducto);
        if (producto == null) {
            log.warn("No se encontró el producto con ID: {}", idProducto);
            throw new IllegalArgumentException("No se encontró ningún producto registrado con el ID: " + idProducto);
        }

        int existenciaAnterior = producto.getExistenciaActual();

        // Validación 4: Comprueba que no intenten devolver más piezas de las que hay en existencia
        if (existenciaAnterior < cantidad) {
            log.warn("Existencia insuficiente para devolver. Disponible: {}, Solicitado: {}", 
                    existenciaAnterior, cantidad);
            throw new IllegalArgumentException("No hay suficiente stock disponible para devolver esa cantidad. Stock actual: " 
                    + existenciaAnterior);
        }

        // 1. Descontar la mercancía dañada del inventario y actualizar el producto
        int nuevaExistencia = existenciaAnterior - cantidad;
        producto.setExistenciaActual(nuevaExistencia);
        Producto productoGuardado = productoRepository.save(producto);

        // 2. Crear y guardar el registro de la devolución
        Devolucion devolucion = new Devolucion();
        devolucion.setProducto(productoGuardado); 
        devolucion.setCantidad(cantidad);
        devolucion.setMotivo(motivo);
        devolucion.setTipoDevolucion("DAÑADO");
        devolucion.setFecha(LocalDateTime.now());

        Devolucion devolucionGuardada = devolucionRepository.save(devolucion);
        log.info("Devolución registrada exitosamente con ID: {} para el producto {}", 
                devolucionGuardada.getIdDevolucion(), productoGuardado.getNombre());

        // 3. Registrar directamente en el historial de movimientos
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento("DEVOLUCION_DANADO");
        movimiento.setCantidad(cantidad);
        movimiento.setExistenciaAnterior(existenciaAnterior);
        movimiento.setExistenciaActual(nuevaExistencia);
        movimiento.setObservacion("Devolución por daño (Folio #" + devolucionGuardada.getIdDevolucion() + "): " + motivo);
        movimiento.setProducto(productoGuardado);

        movimientoRepository.save(movimiento);

        log.info("Movimiento de devolución guardado exitosamente en MovimientoInventarioRepository.");

        return devolucionGuardada;
    }
}