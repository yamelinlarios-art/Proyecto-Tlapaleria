package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

@Component
public class ControlDetalleProveedor {

    @Autowired
    private VentanaDetalleProveedor ventana;

    /**
     * Inicia el flujo para mostrar los detalles de un proveedor.
     * Manda llamar a la ventana y le pasa el proveedor seleccionado.
     * 
     * @param proveedor El proveedor del cual se quieren consultar los detalles
     */
    public void inicia(Proveedor proveedor) {
        ventana.muestra(proveedor);
    }

    /**
     * Termina el flujo y le ordena a la ventana cerrarse.
     */
    public void cerrarVentana() {
        ventana.cierra();
    }
}