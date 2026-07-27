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
 * Ventana principal para la HU-06 (Consultar datos pendientes de proveedores).
 * Muestra el directorio de proveedores junto con sus saldos acumulados.
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

            loader.setController(this);

            Parent root = loader.load();

            stage = new Stage();
            stage.setTitle("Directorio de Proveedores - La Nueva");
            stage.setScene(new Scene(root));

            colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
            colProveedor.setCellValueFactory(new PropertyValueFactory<>("corporativo"));
            colContactoPersonal.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
            colCategoria.setCellValueFactory(new PropertyValueFactory<>("tipoProveedor"));

            colSaldo.setCellValueFactory(cellData -> {
                Proveedor p = cellData.getValue();
                double saldo = 0.0;
                if (p != null && control != null) {
                    try {
                        saldo = control.obtenerSaldoProveedor(p.getIdProveedor());
                    } catch (Exception e) {
                        saldo = 0.0;
                    }
                }
                return new SimpleStringProperty(String.format("$%.2f", saldo));
            });

            if (btnVerDetalle != null) {
                btnVerDetalle.setDisable(true);
                
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
     * Pide consultar el detalle del proveedor seleccionado en la tabla (HU-06).
     */
    @FXML
    private void handleVerDetalle() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            control.consultarProveedor(seleccionado.getIdProveedor());
        }
    }

    /**
     * Llena la tabla con la lista de proveedores, calcula el saldo total general
     * y despliega la ventana en pantalla (HU-06).
     * 
     * @param proveedores lista de proveedores a mostrar
     */
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
                if (control != null) {
                    saldoTotalGeneral += control.obtenerSaldoProveedor(proveedor.getIdProveedor());
                }
            }
        }

        tablaProveedores.setItems(listaObservable);
        tablaProveedores.refresh();
        tablaProveedores.getSelectionModel().clearSelection();

        lblSaldoTotalGeneral.setText(String.format("SALDO TOTAL PENDIENTE: $%.2f", saldoTotalGeneral));

        stage.show();
        stage.toFront();
    }

    /**
     * Muestra un dialogo emergente con un mensaje informativo o de error.
     * 
     * @param mensaje texto a desplegar
     */
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

    /**
     * Muestra u oculta la ventana según el valor recibido.
     * 
     * @param visible true para mostrar, false para ocultar
     */
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

    /**
     * Cierra la ventana invocando la finalizacion del flujo en el controlador (HU-06).
     */
    @FXML
    private void onRegresar() {
        control.termina();
    }
}