package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uam.ayd.proyecto.negocio.ServicioProveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Controlador FXML para la HU-06: Consultar saldos pendientes de proveedores.
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class ControlProveedor {

    private final ServicioProveedor servicioProveedor;

    @FXML
    private TableView<Proveedor> tablaProveedores;

    @FXML
    private TableColumn<Proveedor, Long> colId;

    @FXML
    private TableColumn<Proveedor, String> colNombre;

    @FXML
    private TableColumn<Proveedor, String> colTelefono;

    @FXML
    private TableColumn<Proveedor, String> colTipo;

    @FXML
    private Label lblSaldoTotalGeneral;

    public ControlProveedor(ServicioProveedor servicioProveedor) {
        this.servicioProveedor = servicioProveedor;
    }

    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla FXML
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoProveedor"));
    }

    /**
     * Muestra e inicializa el directorio de proveedores en la vista FXML.
     */
    public void muestra() {
        List<Proveedor> lista = servicioProveedor.recuperarProveedores();
        ObservableList<Proveedor> proveedoresObservable = FXCollections.observableArrayList(lista);
        tablaProveedores.setItems(proveedoresObservable);

        // Calcular saldo total
        double total = 0.0;
        for (Proveedor p : lista) {
            total += servicioProveedor.calcularSaldoPendienteProveedor((int) p.getIdProveedor());
        }
        lblSaldoTotalGeneral.setText(String.format("SALDO TOTAL PENDIENTE: $%.2f", total));
    }
}