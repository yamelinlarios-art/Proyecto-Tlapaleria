package mx.uam.ayd.proyecto.presentacion.historialMovimientos;

import java.util.List;

import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioMovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;

/**
 * Controlador de la ventana Historial de Movimientos.
 *
 * Se encarga de comunicar la interfaz con la lógica de negocio.
 *
 * @author Yael Mora Simón
 */
@Component
public class ControlHistorialMovimientos {

    private final ServicioMovimientoInventario
            servicioMovimientoInventario;

    private final VentanaHistorialMovimientos
            ventanaHistorialMovimientos;

    /**
     * Crea el controlador del historial de movimientos.
     *
     * @param servicioMovimientoInventario servicio de movimientos
     * @param ventanaHistorialMovimientos ventana del historial
     */
    public ControlHistorialMovimientos(
            ServicioMovimientoInventario servicioMovimientoInventario,
            VentanaHistorialMovimientos ventanaHistorialMovimientos) {

        this.servicioMovimientoInventario =
                servicioMovimientoInventario;

        this.ventanaHistorialMovimientos =
                ventanaHistorialMovimientos;
    }

    /**
     * Abre la ventana del historial y carga los movimientos registrados.
     */
    public void inicia() {

        ventanaHistorialMovimientos.setControl(this);

        ventanaHistorialMovimientos.muestra();

        cargarMovimientos();
    }

    /**
     * Recupera todos los movimientos y los muestra en la tabla.
     */
    public void cargarMovimientos() {

        List<MovimientoInventario> movimientos =
                servicioMovimientoInventario.obtenerMovimientos();

        ventanaHistorialMovimientos.muestraMovimientos(movimientos);
    }

    /**
     * Busca movimientos utilizando el filtro escrito por el usuario.
     *
     * @param filtro texto para realizar la búsqueda
     */
    public void buscarMovimiento(String filtro) {

        List<MovimientoInventario> movimientos =
                servicioMovimientoInventario
                        .buscarMovimiento(filtro);

        ventanaHistorialMovimientos
                .muestraMovimientos(movimientos);
    }

    /**
     * Recupera la información completa de un movimiento.
     *
     * @param idMovimiento identificador del movimiento
     * @return movimiento encontrado o null si no existe
     */
    public MovimientoInventario consultarDetalleMovimiento(
            long idMovimiento) {

        return servicioMovimientoInventario
                .consultarDetalleMovimiento(idMovimiento);
    }
}