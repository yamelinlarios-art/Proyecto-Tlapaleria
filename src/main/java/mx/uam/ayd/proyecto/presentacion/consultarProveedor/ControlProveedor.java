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
     * Inicia el flujo principal recuperando los proveedores y mostrándolos en la ventana.
     */
    public void inicia() {
        try {
            List<Proveedor> proveedores = servicioProveedor.recuperarProveedores();
            ventana.muestra(proveedores);
        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje("Error al recuperar los proveedores: " + ex.getMessage());
        }
    }

    /**
     * Inicia el detalle de un proveedor a partir del objeto seleccionado en la tabla.
     */
    public void mostrarDetalleProveedor(Proveedor proveedor) {
        if (proveedor != null) {
            controlDetalle.inicia(proveedor);
        }
    }

    /**
     * Inicia el detalle recuperando el proveedor por su ID (Según Diagrama de Secuencia).
     */
    public void consultarProveedor(long idProveedor) {
        Proveedor proveedor = servicioProveedor.recuperarProveedor(idProveedor);
        if (proveedor != null) {
            controlDetalle.inicia(proveedor);
        } else {
            ventana.muestraDialogoConMensaje("No se encontró el proveedor solicitado.");
        }
    }

    /**
     * Calcula el saldo total de un proveedor llamando al servicio.
     * Método utilizado por la vista para rendering dinámico o cálculo de columnas.
     */
    public double obtenerSaldoProveedor(long idProveedor) {
        try {
            return servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void termina() {
        ventana.setVisible(false);
    }
}