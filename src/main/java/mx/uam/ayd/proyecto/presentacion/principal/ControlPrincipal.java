package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.actualizarPrecioProductos.ControlActualizarPrecio;
import mx.uam.ayd.proyecto.presentacion.agregarUsuario.ControlAgregarUsuario;
import mx.uam.ayd.proyecto.presentacion.devolucionProducto.ControlDevolucionProducto;
import mx.uam.ayd.proyecto.presentacion.listarUsuarios.ControlListarUsuarios;
import mx.uam.ayd.proyecto.presentacion.listarGrupos.ControlListarGrupos;
import mx.uam.ayd.proyecto.presentacion.listarInventario.ControlListarInventario;
import mx.uam.ayd.proyecto.presentacion.registrarMercancia.ControlRegistrarMercancia;

/**
 * Esta clase lleva el flujo de control de la ventana principal
 * 
 * @author humbertocervantes
 *
 */
@Component
public class ControlPrincipal {

    private final ControlAgregarUsuario controlAgregarUsuario;
    private final ControlListarUsuarios controlListarUsuarios;
    private final ControlListarGrupos controlListarGrupos;
    private final VentanaPrincipal ventana;
    private final ControlListarInventario controlListarInventario;
    private final ControlRegistrarMercancia controlRegistrarMercancia;
    private final ControlActualizarPrecio controlActualizarPrecio;
    private final ControlDevolucionProducto controlDevolucion;

    @Autowired
    public ControlPrincipal(
        ControlAgregarUsuario controlAgregarUsuario,
        ControlListarUsuarios controlListarUsuarios,
        ControlListarGrupos controlListarGrupos,
        ControlListarInventario controlListarInventario,
        ControlRegistrarMercancia controlRegistrarMercancia,
        ControlActualizarPrecio controlActualizarPrecio, 
        ControlDevolucionProducto controlDevolucion,
        VentanaPrincipal ventana) {

        this.controlAgregarUsuario = controlAgregarUsuario;
        this.controlListarUsuarios = controlListarUsuarios;
        this.controlListarGrupos = controlListarGrupos;
        this.controlListarInventario = controlListarInventario;
        this.controlRegistrarMercancia = controlRegistrarMercancia;
        this.controlActualizarPrecio = controlActualizarPrecio;
        this.controlDevolucion = controlDevolucion;
        this.ventana = ventana;
    }
    
    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control principal y la ventana principal
     */
    @PostConstruct
    public void init() {
        ventana.setControlPrincipal(this);
    }
    
    /**
     * Inicia el flujo de control de la ventana principal
     * 
     */
    public void inicia() {
        ventana.muestra();
    }

    /**
     * Método que arranca la historia de usuario "agregar usuario"
     * 
     */
    public void agregarUsuario() {
        controlAgregarUsuario.inicia();
    }
    
    /**
     * Método que arranca la historia de usuario "listar usuarios"
     * 
     */
    public void listarUsuarios() {
        controlListarUsuarios.inicia();
    }

    /**
     * Método que arranca la historia de usuario "listar grupos"
     * 
     */
    public void listarGrupos() {
        controlListarGrupos.inicia();
    }

    /**
     * Método que arranca la historia de usuario "listar inventario"
     */
    public void listarInventario() {
        controlListarInventario.inicia();
    }

    /**
     * Método que arranca la historia de usuario "Registrar mercancía".
     */
    public void registrarMercancia() {
        controlRegistrarMercancia.inicia();
    }

    /**
     * Método que arranca la historia de usuario "Actualizar precio".
     */
    public void actualizarPrecio() {
        controlActualizarPrecio.inicia(); // Al usarlo, desaparece el warning de atributo no usado
    }

    /**
     * Método que arranca la historia de usuario "Devolución de producto".
     */
    public void devolucionProducto() {
        controlDevolucion.inicia(); // Al usarlo, desaparece el warning de atributo no usado
    }
}