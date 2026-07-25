package mx.uam.ayd.proyecto.presentacion.registroVenta;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class ControlRegistroVenta {

    // Dependencias
    private final ServicioVenta servicioVenta;
    private final VentanaCarrito ventanaCarrito;
    private final VentanaCobro ventanaCobro;

    // Estado del proceso: Guardamos directamente la Venta
    private Venta ventaActual;

    @Autowired
    public ControlRegistroVenta(ServicioVenta servicioVenta, VentanaCarrito v1, VentanaCobro v2) {
        this.servicioVenta = servicioVenta;
        this.ventanaCarrito = v1;
        this.ventanaCobro = v2;
    }

    @PostConstruct
    public void init() {
        if (ventanaCarrito != null) ventanaCarrito.setControl(this);
        if (ventanaCobro != null) ventanaCobro.setControl(this);
    }

    /**
     * Recibe la venta completa creada en la HU de selección de productos.
     */
    public void iniciaConVenta(Venta venta) {
        if (venta == null) return;

        this.ventaActual = venta;
        
        // Ya no necesitamos calcular nada; la Venta ya tiene sus productos y su total
        if (ventanaCarrito != null) {
            ventanaCarrito.muestra(this.ventaActual);
        }
    }

    /**
     * Valida reglas de negocio antes de permitir el cobro.
     */
/**
 * Valida reglas de negocio antes de permitir el cobro.
 */
public void procesarConfirmacionVenta() {
    if (ventaActual == null || ventaActual.getProductos() == null || ventaActual.getProductos().isEmpty()) {
        if (ventanaCarrito != null) {
            ventanaCarrito.muestraDialogoConMensaje("El carrito de compras está vacío.");
        }
        return;
    }

    // RN-04: Validar precios antes de habilitar pantalla de cobro
    for (DescripcionVenta d : ventaActual.getProductos()) {
        if (d != null && d.getPrecioUnitario() <= 0) {
            if (ventanaCarrito != null) {
                ventanaCarrito.muestraDialogoConMensaje("RN-04: Todo precio asignado debe ser strictly mayor a cero.");
            }
            return;
        }
    }

    // 🔧 FIX DE TRANSICIÓN: Ocultar carrito y mostrar pantalla de cobro
    if (ventanaCarrito != null) {
        ventanaCarrito.setVisible(false); // Oculta la ventana de Carrito
    }

    if (ventanaCobro != null) {
        ventanaCobro.muestra(ventaActual.getTotal()); // Abre la ventana de Cobro con el total
    }
}

    /**
     * Calcula el cambio basándose en el total de la Venta activa
     */
    public void calcularCambio(double efectivoRecibido) {
        if (ventanaCobro == null || ventaActual == null) return;

        double totalVenta = ventaActual.getTotal();

        if (efectivoRecibido < totalVenta) {
            ventanaCobro.actualizaCambio(0, "Efectivo insuficiente");
        } else {
            double cambio = efectivoRecibido - totalVenta;
            ventanaCobro.actualizaCambio(cambio, null);
        }
    }

    /**
     * Finaliza la compra pasando la Venta al servicio
     */
    public void finalizarCompra(double efectivoRecibido) {
        if (servicioVenta == null || ventanaCobro == null || ventaActual == null) return;

        try {
            // Pasamos la venta o sus detalles según tu ServicioVenta
            servicioVenta.registrarVenta(ventaActual.getProductos(), efectivoRecibido);
            
            ventanaCobro.muestraDialogoConMensaje("La venta ha sido exitosa.");
            termina();
            
        } catch (Exception ex) {
            String mensajeError = (ex.getMessage() != null) ? ex.getMessage() : "Error en el registro de venta.";
            ventanaCobro.muestraDialogoConMensaje("Error al registrar: " + mensajeError);
        }
    }

    public void termina() {
        if (ventanaCobro != null) ventanaCobro.setVisible(false);
        if (ventanaCarrito != null) ventanaCarrito.setVisible(false);
    }
}