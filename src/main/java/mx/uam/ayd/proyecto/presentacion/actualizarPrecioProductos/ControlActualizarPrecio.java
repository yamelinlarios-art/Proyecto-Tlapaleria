package mx.uam.ayd.proyecto.presentacion.actualizarPrecioProductos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controlador de la ventana Actualizar Precio (HU09).
 *
 * Se encarga de comunicar la interfaz con la lógica de negocio.
 *
 * @author Yamelin Larios Nepomuseno
 */
@Component
public class ControlActualizarPrecio {

    private static final Logger log = LoggerFactory.getLogger(ControlActualizarPrecio.class);

    private final ServicioProducto servicioProducto;
    private final VentanaActualizarPrecio ventanaActualizarPrecio;

    /**
     * Constructor utilizado por Spring para inyectar dependencias.
     *
     * @param servicioProducto servicio de productos
     * @param ventanaActualizarPrecio ventana de actualizar precio
     */
    public ControlActualizarPrecio(
            ServicioProducto servicioProducto,
            VentanaActualizarPrecio ventanaActualizarPrecio) {

        this.servicioProducto = servicioProducto;
        this.ventanaActualizarPrecio = ventanaActualizarPrecio;
    }

    /**
     * Inicia la ventana de actualización de precios.
     */
    public void inicia() {
        log.info("Iniciando ventana de actualización de precios (HU09)");
        ventanaActualizarPrecio.setControl(this);
        ventanaActualizarPrecio.muestra();
    }

    /**
     * Busca un producto por su ID para mostrar sus datos en la ventana.
     *
     * @param idProducto identificador del producto
     * @return producto encontrado o null si no existe
     */
    public Producto buscarProducto(long idProducto) {
        try {
            return servicioProducto.buscarProductoPorId(idProducto);
        } catch (Exception e) {
            log.error("Error al buscar producto con ID: {}", idProducto, e);
            return null;
        }
    }

    /**
     * Solicita la actualización del precio de un producto.
     *
     * @param idProducto identificador del producto
     * @param nuevoPrecio nuevo precio a asignar
     * @return producto actualizado o null si hubo un error de validación/sistema
     */
    public Producto actualizarPrecio(long idProducto, double nuevoPrecio) {
        try {
            return servicioProducto.actualizarPrecioProducto(idProducto, nuevoPrecio);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en HU09: {}", e.getMessage());
            // Si la ventana tiene un método para desplegar alertas, puedes invocarlo aquí:
            // ventanaActualizarPrecio.muestraError(e.getMessage());
            throw e; // O re-lanzar para que la Vista maneje el mensaje de alerta
        } catch (Exception e) {
            log.error("Error inesperado al actualizar precio para producto ID: {}", idProducto, e);
            throw e;
        }
    }
}