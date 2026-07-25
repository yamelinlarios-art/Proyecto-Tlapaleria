package mx.uam.ayd.proyecto.presentacion.alertaStock;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * @author KEVIND DYDIER
 */
@Component
public class VentanaDetalleProducto {

    private Stage stage;
    
    @SuppressWarnings("unused")
    private ControlRevisarExistencia control;
    
    @FXML
    private Label lblClave;
    
    @FXML
    private Label lblNombre;
    
    @FXML
    private Label lblStockActual;
    
    @FXML
    private Label lblStockMinimo;
    
    private boolean initialized = false;

    public VentanaDetalleProducto() {
    }
    
    /**
     * Inicializa los componentes de la interfaz en el hilo de JavaFX
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        
        try {
            stage = new Stage();
            stage.setTitle("Detalle de Existencias");
            stage.initModality(Modality.APPLICATION_MODAL);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-dialogo-detalle-producto.fxml"));
            // 🚨 CONECTAMOS CON SPRING: Le decimos a JavaFX que USE ESTA INSTANCIA
            loader.setControllerFactory(clazz -> this);
            
            Scene scene = new Scene(loader.load(), 400, 300);
            stage.setScene(scene);
            
            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar FXML de detalle de producto:");
            e.printStackTrace();
        }
    }
    
    public void setControl(ControlRevisarExistencia control) {
        this.control = control;
    }
    
    /**
     * Muestra el diálogo y establece los datos del producto seleccionado
     */
    public void muestra(Producto producto, int stockMinimo) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(producto, stockMinimo));
            return;
        }
        
        // 1. Cargamos FXML si no se ha hecho
        initializeUI();
        
        // 2. Seteamos la información en los Labels
        if (producto != null) {
            if (lblClave != null) lblClave.setText(producto.getClave());
            if (lblNombre != null) lblNombre.setText(producto.getNombre());
            if (lblStockActual != null) lblStockActual.setText(String.valueOf(producto.getExistenciaActual()));
            if (lblStockMinimo != null) lblStockMinimo.setText(String.valueOf(stockMinimo));
        }
        
        // 3. Desplegamos la ventana modal (usamos show() para evitar bloqueos)
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
    
    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}