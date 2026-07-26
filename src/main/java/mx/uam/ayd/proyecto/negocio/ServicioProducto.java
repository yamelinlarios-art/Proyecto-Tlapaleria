package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Servicio encargado de la lógica de negocio de los productos.
 *
 * @author Javitos
 */
@Service
public class ServicioProducto {

    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Autowired
    public ServicioProducto(
            ProductoRepository productoRepository, 
            MovimientoInventarioRepository movimientoRepository) {
        
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public Iterable<Producto> recuperaProductos() {
        log.info("Recuperando todos los productos");
        return productoRepository.findAll();
    }

    public Producto buscaProducto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        return productoRepository.findByNombreIgnoreCase(nombre.trim());
    }

    public boolean verificaDisponibilidad(Producto producto, int cantidad) {
        log.info("Verificando disponibilidad para el producto: {} (Cantidad solicitada: {})", 
                 producto != null ? producto.getNombre() : "null", cantidad);

        if (producto == null || cantidad <= 0) {
            log.warn("Producto nulo o cantidad inválida");
            return false;
        }

        boolean disponible = producto.getExistenciaActual() >= cantidad;
        
        if (!disponible) {
            log.warn("Stock insuficiente para {}. Disponible: {}, Solicitado: {}", 
                     producto.getNombre(), producto.getExistenciaActual(), cantidad);
        }

        return disponible;
    }

    public Producto buscaProductoPorClave(String clave) {
        log.info("Buscando producto por clave: {}", clave);
        return productoRepository.findByClave(clave);
    }

    @Transactional
    public boolean registrarMercancia(String clave, int cantidad) {
        Producto producto = productoRepository.findByClave(clave);

        if (producto == null) {
            return false;
        }

        int existenciaAnterior = producto.getExistenciaActual();
        int nuevaExistencia = existenciaAnterior + cantidad;

        producto.setExistenciaActual(nuevaExistencia);
        Producto productoGuardado = productoRepository.save(producto);

        // Registro del movimiento de entrada de mercancía
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(cantidad);
        movimiento.setExistenciaAnterior(existenciaAnterior);
        movimiento.setExistenciaActual(nuevaExistencia);
        movimiento.setObservacion("Entrada de mercancía registrada por clave");
        movimiento.setProducto(productoGuardado);

        movimientoRepository.save(movimiento);

        log.info("Se registró entrada de {} unidades para el producto {}", cantidad, clave);

        return true;
    }

    // MÉTODOS PARA LA HISTORIA DE USUARIO 09 (HU09)

    public Producto buscarProductoPorId(long idProducto) {
        log.info("Buscando producto por idProducto: {}", idProducto);
        return productoRepository.findByIdProducto(idProducto);
    }

    /**
     * Actualiza el precio de un producto y registra el ajuste en el
     * Historial de Movimientos de Inventario (HU09).
     */
    @Transactional
    public Producto actualizarPrecioProducto(long idProducto, double nuevoPrecio) {
        log.info("Intentando actualizar precio para el producto con ID: {} a nuevo precio: {}", idProducto, nuevoPrecio);

        if (nuevoPrecio <= 0) {
            log.warn("Intento de actualización con un precio inválido: {}", nuevoPrecio);
            throw new IllegalArgumentException("El precio debe ser un valor mayor a cero.");
        }

        Producto producto = productoRepository.findByIdProducto(idProducto);
        if (producto == null) {
            log.warn("No se encontró el producto con el ID: {}", idProducto);
            throw new IllegalArgumentException("No se encontró ningún producto con el ID: " + idProducto);
        }

        double precioAnterior = producto.getPrecio();
        producto.setPrecio(nuevoPrecio);

        Producto productoGuardado = productoRepository.save(producto);
        log.info("Precio del producto {} (ID: {}) actualizado exitosamente de ${} a ${}", 
                 productoGuardado.getNombre(), idProducto, precioAnterior, nuevoPrecio);

        // Guardar directamente en el repositorio de movimientos
        int existencia = productoGuardado.getExistenciaActual();

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento("CAMBIO_PRECIO");
        movimiento.setCantidad(0);
        movimiento.setExistenciaAnterior(existencia);
        movimiento.setExistenciaActual(existencia);
        movimiento.setObservacion("Ajuste de precio: de $" + precioAnterior + " a $" + nuevoPrecio);
        movimiento.setProducto(productoGuardado);

        movimientoRepository.save(movimiento);

        log.info("Movimiento de cambio de precio guardado exitosamente para el producto ID: {}", idProducto);

        return productoGuardado;
    }
}