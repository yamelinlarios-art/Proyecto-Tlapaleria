package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioProveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Control para el caso de uso Consultar Proveedores (HU-06).
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

    public void inicia() {
        try {
            List<Proveedor> proveedores = servicioProveedor.recuperarProveedores();
            ventana.muestra(proveedores);
        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje("Error al recuperar los proveedores: " + ex.getMessage());
        }
    }

    /**
     * Pasa el proveedor seleccionado al ControlDetalleProveedor para iniciar
     * el flujo de ver el detalle.
     */
    public void mostrarDetalleProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            controlDetalle.inicia(proveedor);
        }
    }

    /**
     * Calcula el saldo total de un proveedor llamando al servicio.
     * Este método es consultado por la vista para rendering dinámico.
     */
    public double obtenerSaldoProveedor(long idProveedor) {
        try {
            return servicioProveedor.calcularSaldoPendienteProveedor((int) idProveedor);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void termina() {
        ventana.setVisible(false);
    }
}