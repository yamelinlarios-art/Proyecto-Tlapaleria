package mx.uam.ayd.proyecto.presentacion.agregarProductos; // Revisa que el paquete coincida con tu proyecto

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.presentacion.registroVenta.ControlRegistroVenta;

@Component
public class ControlAgregarProductos {

    @Autowired
    private ServicioProducto servicioProducto;

    @Autowired
    private ServicioVenta servicioVenta;

    @Autowired
    private VistaAgregarProductos vistaAgregarProductos;

    // Inyección del controlador de la HU de tu compañero
    @Autowired
    @Lazy
    private ControlRegistroVenta controlRegistroVenta;

    // Referencia de la venta activa
    private Venta ventaActual;

    /**
     * Punto de entrada principal. Carga el catálogo inicial y despliega la ventana.
     * Corresponde a inicia() en el diagrama de secuencia.
     */
    public void inicia() {
        Iterable<Producto> productos = servicioProducto.recuperaProductos();
        vistaAgregarProductos.mostrarVentanaVenta(this, productos);
    }

    /**
     * Genera una nueva transacción de venta.
     * Corresponde a iniciarVenta() en el diagrama de secuencia.
     */
    public void iniciarVenta() {
        this.ventaActual = servicioVenta.iniciarVenta();
    }

    /**
     * Procesa la adición de un producto al carrito previa validación de inventario.
     * Corresponde al flujo verificaDisponibilidad y agregarProducto en el diagrama.
     * 
     * @param producto Producto seleccionado en la vista
     * @param cantidad Cantidad ingresada por el usuario
     */
    public void agregarProductos(Producto producto, int cantidad) {
        // 1. Verifica si hay existencias suficientes
        boolean disponible = servicioProducto.verificaDisponibilidad(producto, cantidad);

        if (disponible) {
            // 2. Si hay stock, agrega el producto a la venta
            this.ventaActual = servicioVenta.agregarProducto(producto, cantidad, this.ventaActual);
            
            // 3. Persiste/actualiza la venta
            servicioVenta.actualizarVenta(this.ventaActual);
            
            // 4. Notifica a la vista para reflejar el carrito actualizado
            vistaAgregarProductos.mostrarVenta(this.ventaActual);
        } else {
            // Notifica a la vista que no hay suficiente stock
            vistaAgregarProductos.muestraMensajeError("No hay inventario suficiente para el producto: " + producto.getNombre());
        }
    }

    /**
     * Pasa el control a la HU de Registro/Confirmación de Venta al presionar "Siguiente".
     */
   public void continuarRegistroVenta() {
        if (this.ventaActual == null || 
            this.ventaActual.getProductos() == null || 
            this.ventaActual.getProductos().isEmpty()) {
            
            vistaAgregarProductos.muestraMensajeError("Debes agregar al menos un producto a la compra para poder continuar.");
            return;
        }

        // Ocultar tu vista
        vistaAgregarProductos.setVisible(false);

        // Enviar el objeto VENTA completo a tu compañero
        Platform.runLater(() -> {
            controlRegistroVenta.iniciaConVenta(this.ventaActual);
        });
    }
}