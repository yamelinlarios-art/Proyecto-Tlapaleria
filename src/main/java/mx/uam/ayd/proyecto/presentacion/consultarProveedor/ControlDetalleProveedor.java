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
     * Carga y muestra los datos pendientes del proveedor (HU-06):
     * sus datos generales, facturas con adeudo y el saldo total acumulado.
     * 
     * @param idProveedor id del proveedor a consultar
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
     * Obtiene la lista de facturas pendientes de pago del proveedor (HU-06).
     * 
     * @param idProveedor id del proveedor
     * @return lista de facturas pendientes
     */
    public List<Factura> obtenerFacturasPendientes(long idProveedor) {
        return servicioProveedor.recuperarFacturasPendientes(idProveedor);
    }

    /**
     * Consulta el saldo total pendiente de pago con el proveedor (HU-06).
     * 
     * @param idProveedor id del proveedor
     * @return monto total pendiente
     */
    public double obtenerSaldoPendiente(long idProveedor) {
        return servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);
    }

    /**
     * Registra el pago de una factura para actualizar los adeudos pendientes (HU-06).
     * 
     * @param idFactura id de la factura a pagar
     * @return true si se registro el pago correctamente, false si fallo
     */
    public boolean registrarPagoFactura(long idFactura) {
        Factura facturaPagada = servicioProveedor.registrarPago(idFactura);
        return facturaPagada != null;
    }

    /**
     * Cierra la ventana del detalle del proveedor (HU-06).
     */
    public void cerrarVentana() {
        ventana.cierra();
    }
}