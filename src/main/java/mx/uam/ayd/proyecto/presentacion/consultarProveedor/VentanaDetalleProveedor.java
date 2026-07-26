package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Ventana emergente para consultar los detalles de un proveedor y sus facturas pendientes (HU-06).
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class VentanaDetalleProveedor {

    @Autowired
    @Lazy
    private ControlDetalleProveedor control;

    @Autowired
    @Lazy
    private ControlProveedor controlProveedor;

    private Stage stage;
    private Proveedor proveedorActual;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblContacto;

    @FXML
    private Label lblTelefono;

    @FXML
    private Label lblCorreo;

    @FXML
    private Label lblDireccion;

    @FXML
    private Label lblTotalAdeudado;

    @FXML
    private TableView<Factura> tablaFacturas;

    @FXML
    private TableColumn<Factura, String> colNumeroFactura;

    @FXML
    private TableColumn<Factura, LocalDate> colFechaEmision;

    @FXML
    private TableColumn<Factura, LocalDate> colFechaVencimiento;

    @FXML
    private TableColumn<Factura, Double> colMonto;

    @FXML
    private TableColumn<Factura, Double> colSaldo;

    @FXML
    private TableColumn<Factura, Void> colAccion;

    @FXML
    private Button btnCerrar;

    public void setControl(ControlDetalleProveedor control) {
        this.control = control;
    }

    public void muestra(Proveedor proveedor) {
        try {
            this.proveedorActual = proveedor;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-detalle-proveedor.fxml"));
            loader.setController(this);

            Parent root = loader.load();

            configurarTablaFacturas();
            poblarDatos(proveedor);

            stage = new Stage();
            stage.setTitle("Detalles del Proveedor - " + (proveedor != null ? proveedor.getNombreCompleto() : ""));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarTablaFacturas() {
        if (tablaFacturas == null) return;

        colNumeroFactura.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colFechaEmision.setCellValueFactory(new PropertyValueFactory<>("fechaEmision"));
        colFechaVencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldoPendiente"));

        // Formato para mostrar moneda en la columna de saldo
        colSaldo.setCellFactory(tc -> new TableCell<Factura, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        // Columna con botón dinámico para pagar la factura
        if (colAccion != null) {
            colAccion.setCellFactory(param -> new TableCell<Factura, Void>() {
                private final Button btnPagar = new Button("Registrar Pago");

                {
                    btnPagar.getStyleClass().add("button-primary");
                    btnPagar.setOnAction(event -> {
                        Factura factura = getTableView().getItems().get(getIndex());
                        if (factura != null) {
                            handleRegistrarPago(factura);
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnPagar);
                    }
                }
            });
        }
    }

    private void poblarDatos(Proveedor proveedor) {
        if (proveedor != null) {
            lblNombre.setText(proveedor.getNombreCompleto() != null ? proveedor.getNombreCompleto() : "N/A");
            lblContacto.setText(proveedor.getCorporativo() != null ? proveedor.getCorporativo() : "N/A");
            lblTelefono.setText(proveedor.getTelefono() != null ? proveedor.getTelefono() : "N/A");
            lblCorreo.setText(proveedor.getTipoProveedor() != null ? proveedor.getTipoProveedor() : "N/A");
            lblDireccion.setText("ID: " + proveedor.getIdProveedor());

            recargarFacturasYSaldo();
        }
    }

    private void recargarFacturasYSaldo() {
        if (proveedorActual == null) return;

        // Recuperar facturas pendientes desde el control de detalle
        List<Factura> facturas = control.obtenerFacturasPendientes(proveedorActual.getIdProveedor());
        if (tablaFacturas != null && facturas != null) {
            ObservableList<Factura> listaObservable = FXCollections.observableArrayList(facturas);
            tablaFacturas.setItems(listaObservable);
        }

        // Recalcular saldo total adeudado
        double saldo = control.obtenerSaldoPendiente(proveedorActual.getIdProveedor());
        lblTotalAdeudado.setText(String.format("$%.2f", saldo));
    }

    private void handleRegistrarPago(Factura factura) {
        boolean exito = control.registrarPagoFactura(factura.getIdFactura());
        if (exito) {
            muestraMensaje("Pago Registrado", "La factura " + factura.getNumeroFactura() + " fue saldada con éxito.");
            recargarFacturasYSaldo();
        } else {
            muestraMensaje("Error", "No fue posible procesar el pago de la factura.");
        }
    }

    private void muestraMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleCerrar() {
        if (control != null) {
            control.cerrarVentana();
        } else {
            cierra();
        }
    }

    public void cierra() {
        if (stage != null) {
            stage.close();
        }
    }
}