package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;
import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import mx.uam.ayd.proyecto.negocio.ServicioProveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Controlador FXML para la vista de detalle y registro de pagos de facturas.
 */
@Component
public class ControlDetalleProveedor {

    private final ServicioProveedor servicioProveedor;
    private ControlProveedor controlPrincipal;
    private Proveedor proveedorActual;

    @FXML private Label lblNombreProveedor;
    @FXML private Label lblContacto;
    @FXML private Label lblTipo;
    @FXML private Label lblSaldoTotal;

    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, Long> colIdFactura;
    @FXML private TableColumn<Factura, String> colMontoTotal;
    @FXML private TableColumn<Factura, String> colSaldoPendiente;
    @FXML private TableColumn<Factura, String> colEstado;

    public ControlDetalleProveedor(ServicioProveedor servicioProveedor) {
        this.servicioProveedor = servicioProveedor;
    }

    @FXML
    public void initialize() {
        colIdFactura.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        colMontoTotal.setCellValueFactory(f -> new SimpleStringProperty(String.format("$%.2f", f.getValue().getMontoTotal())));
        colSaldoPendiente.setCellValueFactory(f -> new SimpleStringProperty(String.format("$%.2f", f.getValue().getSaldoPendiente())));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    public void inicializarDatos(ControlProveedor controlPrincipal, Proveedor proveedor) {
        this.controlPrincipal = controlPrincipal;
        this.proveedorActual = proveedor;
        refrescarDatos();
    }

    private void refrescarDatos() {
        lblNombreProveedor.setText("Proveedor: " + proveedorActual.getNombreCompleto());
        lblContacto.setText("Contacto: " + proveedorActual.getTelefono());
        lblTipo.setText("Categoría: " + proveedorActual.getTipoProveedor());

        List<Factura> pendientes = servicioProveedor.recuperarFacturasPendientes((int) proveedorActual.getIdProveedor());
        tablaFacturas.setItems(FXCollections.observableArrayList(pendientes));

        double saldoTotal = servicioProveedor.calcularSaldoPendienteProveedor((int) proveedorActual.getIdProveedor());
        lblSaldoTotal.setText(String.format("Saldo Pendiente Total: $%.2f", saldoTotal));
    }

    @FXML
    public void handleRegistrarPago() {
        Factura seleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor seleccione una factura de la lista.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea registrar el pago de esta factura?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Registrar pago en BD
                servicioProveedor.registrarPago(seleccionada.getIdFactura());
                
                // Actualizar la vista de detalle
                refrescarDatos();
                
                // Actualizar la vista principal en segundo plano
                if (controlPrincipal != null) {
                    controlPrincipal.muestra();
                }
            }
        });
    }

    @FXML
    public void handleRegresar() {
        lblNombreProveedor.getScene().getWindow().hide();
    }
}