package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioProveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Control para el caso de uso Consultar Proveedores (HU-06).
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class ControlProveedor {

    private final ServicioProveedor servicioProveedor;
    private final VentanaProveedor ventana;

    @Autowired
    private ControlDetalleProveedor controlDetalle;

    @Autowired
    public ControlProveedor(ServicioProveedor servicioProveedor, VentanaProveedor ventana) {
        this.servicioProveedor = servicioProveedor;
        this.ventana = ventana;
    }

  @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    /**
     * Carga la lista general de proveedores para la HU-06 y los despliega
     * en la ventana principal.
     */
    public void buscaProveedores() {
        try {
            List<Proveedor> proveedores = servicioProveedor.recuperarProveedores();
            ventana.muestra(proveedores);
        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje("Error al recuperar los proveedores: " + ex.getMessage());
        }
    }

    /**
     * Abre el detalle de un proveedor especifico (HU-06) pasandole su ID
     * al controlador correspondiente.
     * 
     * @param idProveedor identificador del proveedor
     */
    public void consultarProveedor(long idProveedor) {
        controlDetalle.inicia(idProveedor);
    }

    /**
     * Obtiene el saldo total pendiente de un proveedor (HU-06)
     * para mostrarlo en la tabla o lista de la vista.
     * 
     * @param idProveedor identificador del proveedor
     * @return saldo pendiente acumulado o 0.0 si ocurre un error
     */
    public double obtenerSaldoProveedor(long idProveedor) {
        try {
            return servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Cierra la ventana de consulta de proveedores (HU-06).
     */
    public void termina() {
        ventana.setVisible(false);
    }
}