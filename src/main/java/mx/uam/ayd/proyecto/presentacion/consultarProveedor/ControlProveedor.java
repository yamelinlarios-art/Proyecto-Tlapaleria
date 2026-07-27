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
    public void buscaProveedores() {
        try {
            List<Proveedor> proveedores = servicioProveedor.recuperarProveedores();
            ventana.muestra(proveedores);
        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje("Error al recuperar los proveedores: " + ex.getMessage());
        }
    }

    /**
     * Inicia el flujo de consulta de detalle delegando al control de detalle mediante el ID.
     * Método alineado con el Diagrama de Secuencia UML.
     * 
     * @param idProveedor Identificador único del proveedor
     */
    public void consultarProveedor(long idProveedor) {
        controlDetalle.inicia(idProveedor);
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