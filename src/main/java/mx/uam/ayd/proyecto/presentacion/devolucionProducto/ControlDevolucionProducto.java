package mx.uam.ayd.proyecto.presentacion.devolucionProducto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioDevolucion;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Devolucion;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controlador de presentación para coordinar el flujo de la HU-10 (Devolución de productos dañados). 
 * Conecta las acciones de la interfaz gráfica (VentanaDevolucionProducto) 
 * con los servicios de negocio de devoluciones y productos.
 *
 * @author Yamelin Larios Nepomuseno
 */
@Component
public class ControlDevolucionProducto {

    private static final Logger log = LoggerFactory.getLogger(ControlDevolucionProducto.class);

    private final ServicioDevolucion servicioDevolucion;
    private final ServicioProducto servicioProducto;
    private final VentanaDevolucionProducto ventana;

    /**
     * Constructor utilizado por Spring para inyectar automáticamente 
     * los servicios necesarios y la ventana asociada.
     *
     * @param servicioDevolucion servicio encargado de procesar devoluciones y actualizar stock
     * @param servicioProducto servicio encargado de buscar y consultar productos
     * @param ventana interfaz visual de la HU-10
     */
    @Autowired
    public ControlDevolucionProducto(ServicioDevolucion servicioDevolucion, 
                                     ServicioProducto servicioProducto, 
                                     VentanaDevolucionProducto ventana) {
        this.servicioDevolucion = servicioDevolucion;
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
    }

    /**
     * Inicia el flujo de la historia de usuario (HU-10) configurando 
     * el enlace bidireccional con el controlador y ordenando que se muestre la ventana.
     */
    public void inicia() {
        log.info("Iniciando flujo de HU-10: Devolución de productos dañados");
        ventana.setControl(this);
        ventana.muestra();
    }

    /**
     * Busca la información de un producto por su ID para previsualizar sus datos 
     * (como nombre y stock actual) directamente en la vista antes de aplicar la devolución.
     *
     * @param idProducto Identificador único del producto a buscar
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
     * Delega la lógica de negocio para procesar la devolución por daño.
     * Maneja las excepciones de validación (como stock insuficiente o datos vacíos) 
     * para ordenarle a la ventana que despliegue alertas gráficas al usuario.
     *
     * @param idProducto Identificador del producto afectado
     * @param cantidad Cantidad de piezas dañadas a descontar
     * @param motivo Justificación o razón de la devolución
     */
    public Devolucion registrarDevolucion(long idProducto, int cantidad, String motivo) {
        try {
            return servicioDevolucion.registrarDevolucionDanado(idProducto, cantidad, motivo);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en HU-10: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al registrar devolución en HU-10", e);
            throw e;
        }
    }
}