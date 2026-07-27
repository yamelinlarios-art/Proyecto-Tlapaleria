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
 * Ventana emergente modal para ver el detalle completo y el stock minimo de un producto.
 *
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

    /**
     * Constructor vacio para que spring pueda instanciar el componente
     */
    public VentanaDetalleProducto() {
    }
    
    /**
     * Inicializa la interfaz grafica en el hilo de javafx y carga el archivo fxml
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
            loader.setControllerFactory(clazz -> this);
            
            Scene scene = new Scene(loader.load(), 400, 300);
            stage.setScene(scene);
            
            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar FXML de detalle de producto:");
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda la referencia al controlador principal por si se ocupa
     * 
     * @param control instancia del controlador
     */
    public void setControl(ControlRevisarExistencia control) {
        this.control = control;
    }
    
    /**
     * Despliega la ventana modal y le pasa los datos del producto a las etiquetas
     * 
     * @param producto objeto producto a mostrar
     * @param stockMinimo limite de stock minimo configurado
     */
    public void muestra(Producto producto, int stockMinimo) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(producto, stockMinimo));
            return;
        }
        
        //Cargamos FXML si no se ha hecho
        initializeUI();
        
        //Seteamos la información en los Labels
        if (producto != null) {
            if (lblClave != null) lblClave.setText(producto.getClave());
            if (lblNombre != null) lblNombre.setText(producto.getNombre());
            if (lblStockActual != null) lblStockActual.setText(String.valueOf(producto.getExistenciaActual()));
            if (lblStockMinimo != null) lblStockMinimo.setText(String.valueOf(stockMinimo));
        }
        
        //Desplegamos la ventana modal (usamos show() para evitar bloqueos)
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
    
    /**
     * Evento para cerrar la ventana emergente al dar clic en cerrar
     */
    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}