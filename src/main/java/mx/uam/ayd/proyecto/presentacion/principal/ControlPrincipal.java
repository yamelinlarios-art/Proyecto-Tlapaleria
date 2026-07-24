package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.actualizarPrecioProductos.ControlActualizarPrecio;
import mx.uam.ayd.proyecto.presentacion.agregarUsuario.ControlAgregarUsuario;
import mx.uam.ayd.proyecto.presentacion.consultarInventario.ControlConsultarInventario;
import mx.uam.ayd.proyecto.presentacion.devolucionProducto.ControlDevolucionProducto;
import mx.uam.ayd.proyecto.presentacion.historialMovimientos.ControlHistorialMovimientos;
import mx.uam.ayd.proyecto.presentacion.listarGrupos.ControlListarGrupos;
import mx.uam.ayd.proyecto.presentacion.listarInventario.ControlListarInventario;
import mx.uam.ayd.proyecto.presentacion.listarUsuarios.ControlListarUsuarios;
import mx.uam.ayd.proyecto.presentacion.registrarMercancia.ControlRegistrarMercancia;

/**
 * Esta clase lleva el flujo de control de la ventana principal.
 *
 * @author humbertocervantes
 */
@Component
public class ControlPrincipal {

    private final ControlAgregarUsuario controlAgregarUsuario;
    private final ControlListarUsuarios controlListarUsuarios;
    private final ControlListarGrupos controlListarGrupos;
    private final ControlListarInventario controlListarInventario;
    private final ControlRegistrarMercancia controlRegistrarMercancia;
    private final ControlActualizarPrecio controlActualizarPrecio;
    private final ControlDevolucionProducto controlDevolucion;
    private final ControlConsultarInventario controlConsultarInventario;
    private final ControlHistorialMovimientos controlHistorialMovimientos;
    private final VentanaPrincipal ventana;

    @Autowired
    public ControlPrincipal(
            ControlAgregarUsuario controlAgregarUsuario,
            ControlListarUsuarios controlListarUsuarios,
            ControlListarGrupos controlListarGrupos,
            ControlListarInventario controlListarInventario,
            ControlRegistrarMercancia controlRegistrarMercancia,
            ControlActualizarPrecio controlActualizarPrecio,
            ControlDevolucionProducto controlDevolucion,
            ControlConsultarInventario controlConsultarInventario,
            ControlHistorialMovimientos controlHistorialMovimientos,
            VentanaPrincipal ventana) {

        this.controlAgregarUsuario = controlAgregarUsuario;
        this.controlListarUsuarios = controlListarUsuarios;
        this.controlListarGrupos = controlListarGrupos;
        this.controlListarInventario = controlListarInventario;
        this.controlRegistrarMercancia = controlRegistrarMercancia;
        this.controlActualizarPrecio = controlActualizarPrecio;
        this.controlDevolucion = controlDevolucion;
        this.controlConsultarInventario = controlConsultarInventario;
        this.controlHistorialMovimientos = controlHistorialMovimientos;
        this.ventana = ventana;
    }

    /**
     * Método que se ejecuta después de la construcción del bean y realiza
     * la conexión entre el control principal y la ventana principal.
     */
    @PostConstruct
    public void init() {
        ventana.setControlPrincipal(this);
    }

    /**
     * Inicia el flujo de control de la ventana principal.
     */
    public void inicia() {
        ventana.muestra();
    }

    /**
     * Arranca la historia de usuario "Agregar usuario".
     */
    public void agregarUsuario() {
        controlAgregarUsuario.inicia();
    }

    /**
     * Arranca la historia de usuario "Listar usuarios".
     */
    public void listarUsuarios() {
        controlListarUsuarios.inicia();
    }

    /**
     * Arranca la historia de usuario "Listar grupos".
     */
    public void listarGrupos() {
        controlListarGrupos.inicia();
    }

    /**
     * Arranca la historia de usuario "Listar inventario".
     */
    public void listarInventario() {
        controlListarInventario.inicia();
    }

    /**
     * Arranca la historia de usuario "Registrar mercancía".
     */
    public void registrarMercancia() {
        controlRegistrarMercancia.inicia();
    }

    /**
     * Arranca la historia de usuario "Actualizar precio".
     */
    public void actualizarPrecio() {
        controlActualizarPrecio.inicia();
    }

    /**
     * Arranca la historia de usuario "Devolución de producto".
     */
    public void devolucionProducto() {
        controlDevolucion.inicia();
    }

    /**
     * Arranca la historia de usuario "Consultar inventario".
     */
    public void consultarInventario() {
        controlConsultarInventario.inicia();
    }

    /**
     * Arranca la historia de usuario "Historial de movimientos".
     */
    public void historialMovimientos() {
        controlHistorialMovimientos.inicia();
    }
}