package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Control para la HU-06: Consultar saldos pendientes de proveedores.
 *
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class ControlProveedor {

    @Autowired
    private ServicioProveedor servicioProveedor;

    @Autowired
    private VentanaProveedor ventanaProveedor;

    @Autowired
    private VentanaDetalleProveedor ventanaDetalleProveedor;

    /**
     * Inicia la historia de usuario mostrando la lista de proveedores
     * y calculando los saldos correspondientes.
     */
    public void inicia() {
        List<Proveedor> proveedores = servicioProveedor.recuperarProveedores();
        ventanaProveedor.muestra(this, proveedores);
    }

    /**
     * Muestra la pantalla de detalle para un proveedor seleccionado.
     *
     * @param idProveedor Identificador del proveedor a consultar
     */
    public void consultarProveedor(long idProveedor) {
        Proveedor proveedor = servicioProveedor.recuperarProveedor(idProveedor);
        if (proveedor != null) {
            List<Factura> facturasPendientes = servicioProveedor.recuperarFacturasPendientes((int) idProveedor);
            double saldoTotal = servicioProveedor.calcularSaldoPendienteProveedor((int) idProveedor);
            
            ventanaDetalleProveedor.muestra(this, proveedor, facturasPendientes, saldoTotal);
        }
    }

    /**
     * Registra el pago de una factura y actualiza la vista del detalle del proveedor.
     *
     * @param idFactura ID de la factura pagada
     * @param idProveedor ID del proveedor al que pertenece la factura
     */
    public void registrarPago(long idFactura, long idProveedor) {
        Factura facturaActualizada = servicioProveedor.registrarPago(idFactura);
        
        if (facturaActualizada != null) {
            // Se refrescan los datos de la pantalla de detalle automáticamente
            consultarProveedor(idProveedor);
        }
    }

    /**
     * Obtiene el saldo pendiente total para mostrar en el directorio/lista.
     *
     * @param idProveedor Identificador del proveedor
     * @return El saldo total pendiente
     */
    public double obtenerSaldoPendiente(int idProveedor) {
        return servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);
    }

    /**
     * Regresa a la vista principal de la lista de proveedores.
     */
    public void regresar() {
        inicia();
    }
}