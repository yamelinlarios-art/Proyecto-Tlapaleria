package mx.uam.ayd.proyecto.presentacion.alertaStock;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * @author kevin dydier
 */
@Component
public class VentanaRevisionExistencia {

    private Stage stage;
    private ControlRevisarExistencia control;
    private ObservableList<Producto> productosData;
    
    @FXML
    private TableView<Producto> tablaAlertas;
    
    @FXML
    private TableColumn<Producto, String> colClave;
    
    @FXML
    private TableColumn<Producto, String> colNombre;
    
    @FXML
    private TableColumn<Producto, Integer> colStock;
    
    @FXML
    private TextField txtBusqueda;
    
    private boolean initialized = false;

    public VentanaRevisionExistencia() {
        productosData = FXCollections.observableArrayList();
    }
    
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
            stage.setTitle("Alertas de Inventario - Revisión de Existencias");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-revision-existencia.fxml"));
            loader.setControllerFactory(clazz -> this);
            
            Scene scene = new Scene(loader.load(), 700, 500);
            stage.setScene(scene);
            
            // Configuración de columnas
            colClave.setCellValueFactory(new PropertyValueFactory<>("clave"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("existenciaActual"));
            
            // 🚨 CORRECCIÓN AQUÍ: Resaltado en rojo respetando la selección de JavaFX
            tablaAlertas.setRowFactory(tv -> new TableRow<Producto>() {
                @Override
                protected void updateItem(Producto item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setStyle("");
                    } else if (isSelected()) {
                        // Si el usuario hace clic, aplicamos un azul de selección claro con texto oscuro visible
                        setStyle("-fx-background-color: #0078d7; -fx-text-fill: white;");
                    } else {
                        // Fila normal en alerta (rojo claro con texto oscuro bien legible)
                        setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #333333;");
                    }
                }
            });

            tablaAlertas.setItems(productosData);
            initialized = true;
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de alertas:");
            e.printStackTrace();
        }
    }
    
    public void muestra(ControlRevisarExistencia control) {
        this.control = control;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(control));
            return;
        }
        
        initializeUI();
        stage.show();
    }
    
    public void mostrarAlertas(List<Producto> productos) {
        Platform.runLater(() -> {
            productosData.clear();
            if (productos != null) {
                productosData.addAll(productos);
            }
        });
    }

    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información de Inventario");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void mostrarMensajeSinAlertas() {
        muestraDialogoConMensaje("No se detectaron productos con stock bajo el mínimo.");
    }
    
    @FXML
    private void handleVerDetalle() {
        Producto seleccionado = tablaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            control.consultarDetalleProducto(seleccionado.getIdProducto());
        } else {
            muestraDialogoConMensaje("Por favor, seleccione un producto de la lista roja.");
        }
    }
    
    @FXML
    private void handleCerrar() {
        stage.close();
    }
}