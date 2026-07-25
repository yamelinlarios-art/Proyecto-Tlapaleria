package mx.uam.ayd.proyecto.presentacion.alertaStock;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controlador para la HU de Revisión de Existencias / Alertas de Stock.
 * 
 * @author kevin dydier
 */
@Component
public class ControlRevisarExistencia {

    private final ServicioInventario servicioInventario;
    
    ////////////////////////////////////////////////////////////////////// referencias a las vistas
    private final VentanaRevisionExistencia ventanaRevision;
    private final VentanaDetalleProducto ventanaDetalle;

    ///////////////////////////////////////////////////////////////////////////// constructor para inyección de dependencias
    @Autowired
    public ControlRevisarExistencia(ServicioInventario servicioInventario, VentanaRevisionExistencia ventanaRevision, VentanaDetalleProducto ventanaDetalle) {
        this.servicioInventario = servicioInventario;
        this.ventanaRevision = ventanaRevision;
        this.ventanaDetalle = ventanaDetalle;
    }

    ////////////////////////////////////////////////////////////////////////////////////// Métodos del controlador
    /**
     * Inicia el flujo de la vista de alertas de existencia.
     */
    public void inicia() {
        this.ventanaDetalle.setControl(this); // Asigna la referencia del controlador al detalle
        this.ventanaRevision.muestra(this);  // Muestra la ventana principal de alertas
        this.consultarAlertas();              // Carga los productos con stock bajo en la tabla
    }

    ////////////////////////////////////////////////////////////////////////////////////////////// obtiene los productos con bajo stock 
    /**
     * Consulta al servicio los productos en alerta y los envía a la vista.
     */
    public void consultarAlertas() {
        List<Producto> productosAlerta = servicioInventario.consultarAlertas();
        
        if (productosAlerta == null || productosAlerta.isEmpty()) {
            ventanaRevision.mostrarMensajeSinAlertas();
        } else {
            // Envía la lista a la vista para el resaltado en rojo de los productos con bajo stock
            ventanaRevision.mostrarAlertas(productosAlerta);
        }
    }

    /**
     * Despliega el detalle del stock actual y el límite mínimo configurado.
     * Se ejecuta cuando el usuario hace clic sobre una advertencia en la tabla.
     *
     * @param idProducto Identificador técnico para recuperar el detalle.
     */
    public void consultarDetalleProducto(long idProducto) {
        // Recupera el producto desde el negocio
        Producto producto = servicioInventario.obtenerDetalleProducto(idProducto);
        
        if (producto != null) {
            // Obtener el stock mínimo real directamente del inventario en lugar de un valor estático
            int stockMinimoReal = servicioInventario.obtenerStockMinimo(idProducto);
            
            ventanaDetalle.muestra(producto, stockMinimoReal);
        } else {
            ventanaRevision.muestraDialogoConMensaje("No se pudo obtener la información del producto.");
        }
    }
}