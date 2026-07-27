package mx.uam.ayd.proyecto.presentacion.alertaStock;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controlador de la HU-03 para las alertas de stock y revision de existencias.
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
    /**
     * Constructor donde spring nos inyecta el servicio de inventarios y las vistas.
     * 
     * @param servicioInventario el servicio de negocio
     * @param ventanaRevision vista principal de la tabla de alertas
     * @param ventanaDetalle vista modal del detalle
     */
    @Autowired
    public ControlRevisarExistencia(ServicioInventario servicioInventario, VentanaRevisionExistencia ventanaRevision, VentanaDetalleProducto ventanaDetalle) {
        this.servicioInventario = servicioInventario;
        this.ventanaRevision = ventanaRevision;
        this.ventanaDetalle = ventanaDetalle;
    }

    ////////////////////////////////////////////////////////////////////////////////////// Métodos del controlador
    /**
     * Arranca todo el flujo de la vista de alertas al abrir la pantalla.
     */
    public void inicia() {
        this.ventanaDetalle.setControl(this); // Asigna la referencia del controlador al detalle
        this.ventanaRevision.muestra(this);  // Muestra la ventana principal de alertas
        this.consultarAlertas();              // Carga los productos con stock bajo en la tabla
    }

    ////////////////////////////////////////////////////////////////////////////////////////////// obtiene los productos con bajo stock 
    /**
     * Pide las alertas al servicio y decide si mandar la lista a la tabla o sacar aviso de que no hay alertas.
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
     * Pide la informacion detallada de un producto seleccionado para mostralo en el dialogo emergente.
     *
     * @param idProducto id del producto que selecciono el usuario en la tabla
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