package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Controlador del Caso de Uso para la HU-06: Directorio de Proveedores.
 */
@Component
public class ControlProveedor {

    private final ServicioProveedor servicioProveedor;

    @Autowired
    private VentanaProveedor ventanaProveedor;

    public ControlProveedor(ServicioProveedor servicioProveedor) {
        this.servicioProveedor = servicioProveedor;
    }

    /**
     * Inicia el flujo del módulo abriendo la ventana correspondiente.
     */
    public void inicia() {
        ventanaProveedor.muestra(this);
    }

    /**
     * Recupera la lista de proveedores desde el servicio.
     */
    public List<Proveedor> obtenerProveedores() {
        return servicioProveedor.recuperarProveedores();
    }

    /**
     * Calcula el saldo pendiente de un proveedor.
     */
    public double calcularSaldoProveedor(int idProveedor) {
        return servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);
    }
}