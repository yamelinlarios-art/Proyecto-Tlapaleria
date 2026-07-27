package mx.uam.ayd.proyecto.presentacion.alertaStock;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
 * Ventana principal de javafx para la vista de revision de existencias y productos con alerta.
 *
 * @author kevin dydier
 */
@Component
public class VentanaRevisionExistencia {

    private Stage stage;
    private ControlRevisarExistencia control;
    private ObservableList<Producto> productosData;
    private FilteredList<Producto> filteredData; // Lista filtrada para la búsqueda en tiempo real
    
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

    /**
     * Constructor donde inicializamos las listas observables para llenar la tabla de alertas
     */
    public VentanaRevisionExistencia() {
        productosData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(productosData, p -> true); // Inicializa aceptando todos los elementos
    }
    
    /**
     * Prepara y carga los componentes de la interfaz grafica de javafx desde el fxml
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
            stage.setTitle("Alertas de Inventario - Revisión de Existencias");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-revision-existencia.fxml"));
            loader.setControllerFactory(clazz -> this);
            
            Scene scene = new Scene(loader.load(), 700, 500);
            stage.setScene(scene);
            
            // Configuración de columnas
            colClave.setCellValueFactory(new PropertyValueFactory<>("clave"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("existenciaActual"));
            
            // Ajuste automático del ancho de las columnas para ocupar todo el ancho disponible
            tablaAlertas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Listener de búsqueda en tiempo real
            txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(producto -> {
                    // Si el cuadro de búsqueda está vacío, muestra todos los productos
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return true;
                    }
                    
                    String lowerCaseFilter = newValue.toLowerCase().trim();
                    
                    // Compara por clave
                    if (producto.getClave() != null && producto.getClave().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    // Compara por nombre
                    if (producto.getNombre() != null && producto.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    return false; // No coincide
                });
            });

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

            // Se asigna la lista filtrada a la tabla
            tablaAlertas.setItems(filteredData);
            initialized = true;
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de alertas:");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la pantalla principal asignandole primero su controlador
     * 
     * @param control instancia del controlador
     */
    public void muestra(ControlRevisarExistencia control) {
        this.control = control;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(control));
            return;
        }
        
        initializeUI();
        stage.show();
    }
    
    /**
     * Recibe la lista de productos en alerta y los manda a la tabla para que se vean en rojo
     * 
     * @param productos lista de productos recuperados del servicio
     */
    public void mostrarAlertas(List<Producto> productos) {
        Platform.runLater(() -> {
            productosData.clear();
            if (productos != null) {
                productosData.addAll(productos);
            }
        });
    }

    /**
     * Despliega un dialogo de alerta generico con un mensaje en pantalla
     * 
     * @param mensaje texto a mostrar en la ventana
     */
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

    /**
     * Muestra el aviso informativo cuando no hay productos bajo el minimo
     */
    public void mostrarMensajeSinAlertas() {
        muestraDialogoConMensaje("No se detectaron productos con stock bajo el mínimo.");
    }
    
    /**
     * Evento al dar clic en ver detalle, valida si se selecciono algo en la tabla
     */
    @FXML
    private void handleVerDetalle() {
        Producto seleccionado = tablaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            control.consultarDetalleProducto(seleccionado.getIdProducto());
        } else {
            muestraDialogoConMensaje("Por favor, seleccione un producto de la lista roja.");
        }
    }
    
    /**
     * Cierra la ventana actual
     */
    @FXML
    private void handleCerrar() {
        stage.close();
    }
}