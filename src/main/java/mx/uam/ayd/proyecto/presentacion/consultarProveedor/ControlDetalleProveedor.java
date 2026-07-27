package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Control para la ventana de detalle de un proveedor (HU-06).
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class ControlDetalleProveedor {

    private final ServicioProveedor servicioProveedor;
    private final VentanaDetalleProveedor ventana;

    @Autowired
    public ControlDetalleProveedor(ServicioProveedor servicioProveedor, VentanaDetalleProveedor ventana) {
        this.servicioProveedor = servicioProveedor;
        this.ventana = ventana;
    }

    /**
     * Inicia el flujo recuperando el proveedor, sus facturas pendientes y
     * su saldo total adeudado para enviarlos juntos a la vista.
     * 
     * @param idProveedor Identificador único del proveedor
     */
    public void inicia(long idProveedor) {
        Proveedor proveedor = servicioProveedor.recuperarProveedor(idProveedor);
        
        if (proveedor != null) {
            List<Factura> facturas = servicioProveedor.recuperarFacturasPendientes(idProveedor);
            double saldoTotal = servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);
            
            ventana.setControl(this);
            ventana.muestra(proveedor, facturas, saldoTotal);
        }
    }

    /**
     * Recupera las facturas pendientes de un proveedor dado su ID.
     */
    public List<Factura> obtenerFacturasPendientes(long idProveedor) {
        return servicioProveedor.recuperarFacturasPendientes(idProveedor);
    }

    /**
     * Obtiene el saldo pendiente total calculado por el servicio.
     */
    public double obtenerSaldoPendiente(long idProveedor) {
        return servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);
    }

    /**
     * Registra el pago de una factura pendiente.
     */
    public boolean registrarPagoFactura(long idFactura) {
        Factura facturaPagada = servicioProveedor.registrarPago(idFactura);
        return facturaPagada != null;
    }

    /**
     * Termina el flujo y le ordena a la ventana cerrarse.
     */
    public void cerrarVentana() {
        ventana.cierra();
    }
}