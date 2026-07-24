package mx.uam.ayd.proyecto.presentacion.agregarProductos;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class VistaAgregarProductos {

    private Stage stage;
    private ControlAgregarProductos control;

    // Cambiamos el ComboBox por el Campo de Texto para buscar
    @FXML
    private TextField txtBuscarProducto;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TableView<DescripcionVenta> tablaCarrito;

    @FXML
    private TableColumn<DescripcionVenta, String> colNombre;

    @FXML
    private TableColumn<DescripcionVenta, Double> colPrecio;

    @FXML
    private TableColumn<DescripcionVenta, Integer> colCantidad;

    @FXML
    private TableColumn<DescripcionVenta, Double> colSubtotal;

    @FXML
    private Label lblTotal;

    private boolean initialized = false;

    // Almacenamos el catálogo de productos enviado por el ServicioProducto
    private Iterable<Producto> catalogoProductos;

    public VistaAgregarProductos() {
    }

    private void initializeUI() {
        if (initialized) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Agregar Productos a la Venta");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-agregar-productos.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 500);
            stage.setScene(scene);

            // Mapeo de columnas
            colNombre.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setControl(ControlAgregarProductos control) {
        this.control = control;
    }

    /**
     * Corresponde a mostrarVentanaVenta en tu diagrama de secuencia / HU
     */
    public void mostrarVentanaVenta(ControlAgregarProductos control, Iterable<Producto> productos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.mostrarVentanaVenta(control, productos));
            return;
        }

        this.setControl(control);
        this.catalogoProductos = productos; // Guardamos el catálogo localmente

        initializeUI();

        // Limpiamos la entrada de texto y el carrito
        if (txtBuscarProducto != null) {
            txtBuscarProducto.clear();
        }

        control.iniciarVenta();
        if (tablaCarrito != null) {
            tablaCarrito.getItems().clear();
        }
        if (lblTotal != null) {
            lblTotal.setText("$0.00");
        }

        stage.show();
    }

    public void mostrarVenta(Venta venta) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.mostrarVenta(venta));
            return;
        }

        if (venta != null && venta.getProductos() != null) {
            tablaCarrito.setItems(FXCollections.observableArrayList(venta.getProductos()));
            lblTotal.setText(String.format("$%.2f", venta.getTotal()));
        }
    }

    public void muestraMensajeError(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraMensajeError(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // =========================================================================
    // FXML EVENT HANDLERS
    // =========================================================================

    @FXML
    private void handleAgregarProducto() {
        String textoBusqueda = txtBuscarProducto.getText().trim();

        if (textoBusqueda.isEmpty()) {
            muestraMensajeError("Por favor ingresa el nombre o código del producto.");
            return;
        }

        // Búsqueda en el catálogo del producto deseado por nombre (ignora mayúsculas/minúsculas)
        Producto productoSeleccionado = null;
        if (catalogoProductos != null) {
            for (Producto p : catalogoProductos) {
                if (p.getNombre().equalsIgnoreCase(textoBusqueda)) {
                    productoSeleccionado = p;
                    break;
                }
            }
        }

        if (productoSeleccionado == null) {
            muestraMensajeError("No se encontró el producto: " + textoBusqueda);
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (cantidad <= 0) {
                muestraMensajeError("La cantidad debe ser mayor a 0.");
                return;
            }

            // Llamada directa al método original de tu HU
            control.agregarProductos(productoSeleccionado, cantidad);

        } catch (NumberFormatException e) {
            muestraMensajeError("Ingresa un número entero válido en la cantidad.");
        }
    }

    @FXML
    private void handleNuevaVenta() {
        if (control != null) {
            control.iniciarVenta();
            if (txtBuscarProducto != null) txtBuscarProducto.clear();
            if (tablaCarrito != null) tablaCarrito.getItems().clear();
            if (lblTotal != null) lblTotal.setText("$0.00");
        }
    }

    @FXML
    private void handleCerrar() {
        stage.close();
    }
}