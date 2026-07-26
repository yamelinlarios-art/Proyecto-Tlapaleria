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

    @Autowired
    private ServicioProveedor servicioProveedor;

    @Autowired
    private VentanaDetalleProveedor ventana;

    /**
     * Inicia el flujo para mostrar los detalles de un proveedor.
     * Manda llamar a la ventana y le pasa el proveedor seleccionado.
     * 
     * @param proveedor El proveedor del cual se quieren consultar los detalles
     */
    public void detalleProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            // Se inyecta la referencia de este control en la ventana si fuera necesario
            ventana.setControl(this);
            ventana.muestra(proveedor);
        }
    }

    /**
     * Inicia el flujo recuperando el proveedor por ID.
     */
    public void inicia(long idProveedor) {
        Proveedor proveedor = servicioProveedor.recuperarProveedor(idProveedor);
        detalleProveedor(proveedor);
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
     * Registra el pago de una factura pendiente y actualiza la vista.
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