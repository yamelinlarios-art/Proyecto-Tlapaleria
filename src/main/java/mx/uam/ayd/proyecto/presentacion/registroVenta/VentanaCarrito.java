package mx.uam.ayd.proyecto.presentacion.registroVenta;

import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class VentanaCarrito {

    private Stage stage;
    private ControlRegistroVenta control;
    private boolean initialized = false;

    // Elementos FXML
    @FXML private TableView<DescripcionVenta> tablaCarrito;
    @FXML private TableColumn<DescripcionVenta, String> colNombre;
    @FXML private TableColumn<DescripcionVenta, Double> colPrecio;
    @FXML private TableColumn<DescripcionVenta, Integer> colCantidad;
    @FXML private TableColumn<DescripcionVenta, Double> colSubtotal;
    @FXML private Label lblTotal;

    public VentanaCarrito() {
    }

    public void setControl(ControlRegistroVenta control) {
        this.control = control;
    }

private void initializeUI() {
    if (initialized) return;
    
    if (!Platform.isFxApplicationThread()) {
        Platform.runLater(this::initializeUI);
        return;
    }

    URL fxmlUrl = getClass().getResource("/fxml/ventana-carrito-compras.fxml");
    if (fxmlUrl == null) {
        System.err.println("❌ Error: No se encontró el FXML '/fxml/ventana-carrito-compras.fxml'");
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        
        // 🔧 FIX CRÍTICO: Usar setControllerFactory para que utilice la misma instancia de Spring
        loader.setControllerFactory(clazz -> this);

        Parent root = loader.load();
        
        stage = new Stage();
        stage.setTitle("Confirmación de Venta - Resumen");
        stage.setScene(new Scene(root));
        
        // Mapeo seguro de columnas
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("productoNombre")); 
        if (colPrecio != null) colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        if (colCantidad != null) colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        if (colSubtotal != null) colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        initialized = true;
    } catch (IOException e) {
        System.err.println("❌ Error al cargar la interfaz FXML de VentanaCarrito:");
        e.printStackTrace();
    }
}

    /**
     * Despliega la ventana recibiendo el objeto Venta completo.
     * @param venta Objeto Venta que contiene la lista de productos y el total acumulado.
     */
    public void muestra(Venta venta) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(venta));
            return;
        }

        if (venta == null) return;

        initializeUI();
        
        // Carga la tabla directamente leyendo de la Venta de forma segura
        if (tablaCarrito != null && venta.getProductos() != null) {
            tablaCarrito.setItems(FXCollections.observableArrayList(venta.getProductos()));
        }
        
        if (lblTotal != null) {
            lblTotal.setText(String.format("$%.2f", venta.getTotal()));
        }
        
        if (stage != null) {
            stage.show();
        }
    }

    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje != null ? mensaje : "");
        alert.showAndWait();
    }

    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }
        
        if (stage != null) {
            if (visible) {
                stage.show();
            } else {
                stage.hide();
            }
        }
    }

    @FXML
    private void onConfirmarVenta() {
        if (control != null) {
            control.procesarConfirmacionVenta();
        }
    }

    @FXML
    private void onCancelarVenta() {
        if (control != null) {
            control.termina();
        }
    }
}