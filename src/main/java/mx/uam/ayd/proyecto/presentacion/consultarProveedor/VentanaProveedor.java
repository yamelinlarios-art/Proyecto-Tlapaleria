package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Ventana para consultar el directorio de proveedores.
 */
@Component
public class VentanaProveedor {

    private Stage stage;
    private ControlProveedor control;
    private boolean initialized = false;

    @FXML
    private Label lblSaldoTotalGeneral;

    @FXML
    private TableView<Proveedor> tablaProveedores;

    @FXML
    private TableColumn<Proveedor, Long> colId;

    @FXML
    private TableColumn<Proveedor, String> colProveedor;

    @FXML
    private TableColumn<Proveedor, String> colContactoPersonal;

    @FXML
    private TableColumn<Proveedor, String> colCategoria;

    @FXML
    private TableColumn<Proveedor, String> colSaldo;

    // Inyección del nuevo botón de la vista
    @FXML
    private Button btnVerDetalle;

    public VentanaProveedor() {
    }

    public void setControl(ControlProveedor control) {
        this.control = control;
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ventana-proveedor.fxml"));

            // Asignamos esta misma clase como controlador FXML
            loader.setController(this);

            Parent root = loader.load();

            stage = new Stage();
            stage.setTitle("Directorio de Proveedores - La Nueva");
            stage.setScene(new Scene(root));

            // Configuración de celdas según las propiedades de Proveedor.java
            colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
            colProveedor.setCellValueFactory(new PropertyValueFactory<>("corporativo"));
            colContactoPersonal.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
            colCategoria.setCellValueFactory(new PropertyValueFactory<>("tipoProveedor"));

            // Cálculo dinámico del saldo llamando al control
            colSaldo.setCellValueFactory(cellData -> {
                Proveedor p = cellData.getValue();
                double saldo = control.obtenerSaldoProveedor(p.getIdProveedor());
                return new SimpleStringProperty(String.format("$%.2f", saldo));
            });

            // --- HABILITAR/DESHABILITAR BOTÓN SEGÚN SELECCIÓN ---
            if (btnVerDetalle != null) {
                btnVerDetalle.setDisable(true); // Deshabilitado de inicio
                
                tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    btnVerDetalle.setDisable(newSelection == null);
                });
            }

            initialized = true;

        } catch (IOException e) {
            e.printStackTrace();
            muestraDialogoConMensaje("No fue posible cargar la ventana de proveedores");
        }
    }

    /**
     * Acción invocada al presionar el botón "Ver Detalles" (definido en FXML via onAction="#handleVerDetalle")
     */
    @FXML
    private void handleVerDetalle() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            control.mostrarDetalleProveedor(seleccionado);
        }
    }

    public void muestra(List<Proveedor> proveedores) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestra(proveedores));
            return;
        }

        initializeUI();

        ObservableList<Proveedor> listaObservable = FXCollections.observableArrayList();
        double saldoTotalGeneral = 0.0;

        if (proveedores != null) {
            for (Proveedor proveedor : proveedores) {
                listaObservable.add(proveedor);
                saldoTotalGeneral += control.obtenerSaldoProveedor(proveedor.getIdProveedor());
            }
        }

        tablaProveedores.setItems(listaObservable);
        
        // Limpiamos la selección activa de la tabla para que el botón empiece desactivado
        tablaProveedores.getSelectionModel().clearSelection();

        lblSaldoTotalGeneral.setText(String.format("SALDO TOTAL PENDIENTE: $%.2f", saldoTotalGeneral));

        stage.show();
        stage.toFront();
    }

    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Directorio de Proveedores");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setVisible(visible));
            return;
        }

        if (stage == null) {
            initializeUI();
        }

        if (visible) {
            stage.show();
        } else {
            stage.hide();
        }
    }

    @FXML
    private void onRegresar() {
        control.termina();
    }
}