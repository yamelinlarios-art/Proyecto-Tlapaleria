package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Servicio encargado de la lógica de negocio de los productos.
 *
 * @author Javitos
 */
@Service
public class ServicioProducto {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);

    // Repositorios y Servicios
    private final ProductoRepository productoRepository;
    private final ServicioMovimientoInventario servicioMovimientoInventario;

    @Autowired
    public ServicioProducto(
            ProductoRepository productoRepository, 
            ServicioMovimientoInventario servicioMovimientoInventario) {
        
        this.productoRepository = productoRepository;
        this.servicioMovimientoInventario = servicioMovimientoInventario;
    }

    /**
     * Recupera todos los productos registrados.
     * @return Todos los productos.
     */
    public Iterable<Producto> recuperaProductos() {
        log.info("Recuperando todos los productos");
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por su nombre ignorando mayúsculas/minúsculas.
     * 
     * @param nombre El nombre del producto
     * @return El producto encontrado o null si no existe
     */
    public Producto buscaProducto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        return productoRepository.findByNombreIgnoreCase(nombre.trim());
    }

    /**
     * Verifica si hay suficiente stock disponible para la venta.
     * 
     * @param producto El producto a verificar.
     * @param cantidad La cantidad solicitada por el cliente.
     * @return true si hay suficiente stock, false en caso contrario.
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
     * Busca un producto por su clave.
     *
     * @param clave Clave del producto.
     * @return Producto encontrado o null si no existe.
     */
    public Producto buscaProductoPorClave(String clave) {
        log.info("Buscando producto por clave: {}", clave);
        return productoRepository.findByClave(clave);
    }

    /**
     * Registra la entrada de mercancía.
     *
     * @param clave Clave del producto.
     * @param cantidad Cantidad a agregar.
     * @return true si se actualizó correctamente.
     */
    public boolean registrarMercancia(String clave, int cantidad) {
        Producto producto = productoRepository.findByClave(clave);

        if (producto == null) {
            return false;
        }

        producto.setExistenciaActual(
                producto.getExistenciaActual() + cantidad);

        productoRepository.save(producto);

        log.info("Se registró entrada de {} unidades para el producto {}",
                cantidad, clave);

        return true;
    }

    //                     MÉTODOS PARA LA HISTORIA DE USUARIO 09 (HU09)

    /**
     * Busca un producto por su ID único.
     * Utilizado para cargar la información previa en la interfaz de HU09.
     * 
     * @param idProducto Identificador único del producto.
     * @return Producto encontrado o null si no existe.
     */
    public Producto buscarProductoPorId(long idProducto) {
        log.info("Buscando producto por idProducto: {}", idProducto);
        return productoRepository.findByIdProducto(idProducto);
    }

    /**
     * Actualiza el precio de un producto y registra el ajuste en el
     * Historial de Movimientos de Inventario (HU09).
     * 
     * @param idProducto Identificador del producto a modificar.
     * @param nuevoPrecio El nuevo precio a asignar.
     * @return El objeto Producto actualizado y persistido.
     */
    @Transactional
    public Producto actualizarPrecioProducto(long idProducto, double nuevoPrecio) {
        log.info("Intentando actualizar precio para el producto con ID: {} a nuevo precio: {}", idProducto, nuevoPrecio);

        // Regla de Negocio 1: El nuevo precio debe ser mayor a cero
        if (nuevoPrecio <= 0) {
            log.warn("Intento de actualización con un precio inválido: {}", nuevoPrecio);
            throw new IllegalArgumentException("El precio debe ser un valor mayor a cero.");
        }

        // Regla de Negocio 2: El producto debe existir
        Producto producto = productoRepository.findByIdProducto(idProducto);
        if (producto == null) {
            log.warn("No se encontró el producto con el ID: {}", idProducto);
            throw new IllegalArgumentException("No se encontró ningún producto con el ID: " + idProducto);
        }

        // Guardamos el precio anterior antes de realizar la modificación
        double precioAnterior = producto.getPrecio();

        // Asignamos el nuevo precio al producto
        producto.setPrecio(nuevoPrecio);

        // Guardamos el producto actualizado en el repositorio
        Producto productoGuardado = productoRepository.save(producto);
        log.info("Precio del producto {} (ID: {}) actualizado exitosamente de ${} a ${}", 
                 productoGuardado.getNombre(), idProducto, precioAnterior, nuevoPrecio);

        // Registro del ajuste en ServicioMovimientoInventario (HU09)
        String observacion = "Ajuste de precio: de $" + precioAnterior + " a $" + nuevoPrecio;
        
        servicioMovimientoInventario.registrarMovimiento(
            productoGuardado, 
            0, // La cantidad de stock no cambia en un ajuste de precio
            productoGuardado.getExistenciaActual(), 
            productoGuardado.getExistenciaActual(), 
            "CAMBIO_PRECIO", 
            observacion
        );
        
        log.info("Movimiento de cambio de precio registrado exitosamente para el producto ID: {}", idProducto);

        return productoGuardado;
    }
}