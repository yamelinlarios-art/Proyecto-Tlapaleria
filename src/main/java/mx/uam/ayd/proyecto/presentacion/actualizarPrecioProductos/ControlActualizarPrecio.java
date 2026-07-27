package mx.uam.ayd.proyecto.presentacion.actualizarPrecioProductos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controlador de la ventana Actualizar Precio (HU-09).
 * Actúa como intermediario entre la interfaz gráfica (VentanaActualizarPrecio) 
 * y la lógica de negocio (ServicioProducto).
 *
 * @author Yamelin Larios Nepomuseno
 */
@Component
public class ControlActualizarPrecio {

    private static final Logger log = LoggerFactory.getLogger(ControlActualizarPrecio.class);

    private final ServicioProducto servicioProducto;
    private final VentanaActualizarPrecio ventanaActualizarPrecio;

    /**
     * Constructor utilizado por Spring para inyectar las dependencias 
     * del servicio de productos y la ventana de JavaFX.
     *
     * @param servicioProducto servicio encargado de la lógica de productos
     * @param ventanaActualizarPrecio ventana visual de actualización de precios
     */
    public ControlActualizarPrecio(
            ServicioProducto servicioProducto,
            VentanaActualizarPrecio ventanaActualizarPrecio) {

        this.servicioProducto = servicioProducto;
        this.ventanaActualizarPrecio = ventanaActualizarPrecio;
    }

    /**
     * Inicia el flujo de la HU-09 abriendo la ventana de actualización de precios.
     * Conecta la ventana con este controlador y manda la orden de mostrarse.
     */
    public void inicia() {
        log.info("Iniciando ventana de actualización de precios (HU-09)");
        ventanaActualizarPrecio.setControl(this);
        ventanaActualizarPrecio.muestra();
    }

    /**
     * Busca un producto por su ID para mostrar sus datos actuales 
     * en la interfaz antes de realizar el cambio de precio.
     *
     * @param idProducto identificador único del producto
     * @return el producto encontrado, o null si ocurrió un error o no existe
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
     * Solicita al servicio la actualización del precio de un producto y su respectivo registro en bitácora.
     * Captura y maneja las excepciones de validación para asegurar que la vista pueda mostrar alertas claras.
     *
     * @param idProducto identificador del producto a modificar
     * @param nuevoPrecio nuevo valor monetario a asignar
     * @return el producto actualizado correctamente
     * @throws IllegalArgumentException si el precio es inválido o el producto no existe
     */
    public Producto actualizarPrecio(long idProducto, double nuevoPrecio) {
        try {
            return servicioProducto.actualizarPrecioProducto(idProducto, nuevoPrecio);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en HU-09: {}", e.getMessage());
            // Se relanza la excepción para que la Vista la capture y muestre una alerta gráfica al usuario
            throw e; 
        } catch (Exception e) {
            log.error("Error inesperado al actualizar precio para producto ID: {}", idProducto, e);
            throw e;
        }
    }
}