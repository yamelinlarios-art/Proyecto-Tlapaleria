package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleStringProperty;
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
 * Controlador FXML para la HU-06: Directorio de Proveedores y saldos pendientes.
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class ControlProveedor {

    private final ServicioProveedor servicioProveedor;

    @Autowired
    private VentanaProveedor ventanaProveedor;

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
    private Label lblSaldoTotalGeneral;

    public ControlProveedor(ServicioProveedor servicioProveedor) {
        this.servicioProveedor = servicioProveedor;
    }

    /**
     * Método de entrada principal invocado desde la navegación del sistema.
     */
    public void inicia() {
        ventanaProveedor.muestra(this);
    }

    @FXML
    public void initialize() {
        // Mapeo de propiedades simples de la entidad Proveedor
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colContactoPersonal.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("tipoProveedor"));

        // Mapeo dinámico para el cálculo del Saldo de cada proveedor en la tabla
        colSaldo.setCellValueFactory(cellData -> {
            Proveedor p = cellData.getValue();
            double saldo = servicioProveedor.calcularSaldoPendienteProveedor((int) p.getIdProveedor());
            return new SimpleStringProperty(String.format("$%.2f", saldo));
        });
    }

    /**
     * Carga los proveedores registrados y refresca el saldo general.
     */
    public void muestra() {
        List<Proveedor> lista = servicioProveedor.recuperarProveedores();
        ObservableList<Proveedor> proveedoresObservable = FXCollections.observableArrayList(lista);
        tablaProveedores.setItems(proveedoresObservable);

        // Calcular la suma del saldo total pendiente
        double totalGeneral = 0.0;
        for (Proveedor p : lista) {
            totalGeneral += servicioProveedor.calcularSaldoPendienteProveedor((int) p.getIdProveedor());
        }
        lblSaldoTotalGeneral.setText(String.format("SALDO TOTAL PENDIENTE: $%.2f", totalGeneral));
    }
}