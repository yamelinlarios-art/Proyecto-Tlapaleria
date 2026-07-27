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
 * Maneja consultas de stock, entradas de mercancía y la actualización de precios (HU-09).
 *
 * @author Javitos
 */
@Service
public class ServicioProducto {

    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    /**
     * Inyección de dependencias para los repositorios de productos y movimientos.
     */
    @Autowired
    public ServicioProducto(
            ProductoRepository productoRepository, 
            MovimientoInventarioRepository movimientoRepository) {
        
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Recupera y lista todos los productos registrados en el sistema.
     * 
     * @return Iterable con todos los productos
     */
    public Iterable<Producto> recuperaProductos() {
        log.info("Recuperando todos los productos");
        return productoRepository.findAll();
    }

    /**
     * Busca un producto específico por su nombre ignorando mayúsculas y minúsculas.
     * 
     * @param nombre Nombre del producto a buscar
     * @return El producto encontrado, o null si no existe o el filtro está vacío
     */
    public Producto buscaProducto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        return productoRepository.findByNombreIgnoreCase(nombre.trim());
    }

    /**
     * Verifica si existe suficiente stock de un producto para surtir una cantidad solicitada.
     * 
     * @param producto Producto a evaluar
     * @param cantidad Cantidad de piezas requeridas
     * @return true si hay stock suficiente, false en caso contrario
     */
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

    /**
     * Busca un producto utilizando su clave única de articulo.
     * 
     * @param clave Clave del producto
     * @return El producto encontrado
     */
    public Producto buscaProductoPorClave(String clave) {
        log.info("Buscando producto por clave: {}", clave);
        return productoRepository.findByClave(clave);
    }

    /**
     * Registra una entrada de nueva mercancía sumándola al stock actual del producto
     * y generando el movimiento correspondiente en el historial.
     * 
     * @param clave Clave del producto que recibe la mercancía
     * @param cantidad Número de piezas nuevas que entran al almacén
     * @return true si se registró con éxito, false si el producto no existe
     */
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

        // Registro automático del movimiento de entrada en el historial
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

    // MÉTODOS PARA LA HISTORIA DE USUARIO (HU-09)

    /**
     * Busca un producto por su identificador numérico único (idProducto).
     * Esencial para localizar el artículo que se va a modificar en la HU-09.
     * 
     * @param idProducto Identificador único del producto
     * @return El producto encontrado
     */
    public Producto buscarProductoPorId(long idProducto) {
        log.info("Buscando producto por idProducto: {}", idProducto);
        return productoRepository.findByIdProducto(idProducto);
    }

    /**
     * Actualiza el precio de un producto y registra el ajuste en el
     * Historial de Movimientos de Inventario (HU-09).
     * 1. Valida que el nuevo precio sea un valor positivo mayor a cero.
     * 2. Busca el producto en la base de datos por su ID.
     * 3. Cambia el precio anterior por el nuevo precio y guarda los cambios.
     * 4. Genera de forma automática un registro en la bitácora (MovimientoInventario) 
     *    marcando el tipo como "CAMBIO_PRECIO", con cantidad 0 (porque no afecta piezas físicas)
     *    y detallando la modificación en las observaciones.
     * 
     * @param idProducto ID del producto cuyo precio va a cambiar
     * @param nuevoPrecio El nuevo valor monetario asignado al producto
     * @return El objeto Producto actualizado y guardado
     */
    @Transactional
    public Producto actualizarPrecioProducto(long idProducto, double nuevoPrecio) {
        log.info("Intentando actualizar precio para el producto con ID: {} a nuevo precio: {}", idProducto, nuevoPrecio);

        // Validación 1: El precio no puede ser cero o negativo
        if (nuevoPrecio <= 0) {
            log.warn("Intento de actualización con un precio inválido: {}", nuevoPrecio);
            throw new IllegalArgumentException("El precio debe ser un valor mayor a cero.");
        }

        // Validación 2: Comprueba que el producto exista en la base de datos
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

        // Registro automático en el repositorio de movimientos
        int existencia = productoGuardado.getExistenciaActual();

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento("CAMBIO_PRECIO");
        movimiento.setCantidad(0); // Cero piezas afectadas porque solo cambió el costo/precio
        movimiento.setExistenciaAnterior(existencia);
        movimiento.setExistenciaActual(existencia);
        movimiento.setObservacion("Ajuste de precio: de $" + precioAnterior + " a $" + nuevoPrecio);
        movimiento.setProducto(productoGuardado);

        movimientoRepository.save(movimiento);

        log.info("Movimiento de cambio de precio guardado exitosamente para el producto ID: {}", idProducto);

        return productoGuardado;
    }
}