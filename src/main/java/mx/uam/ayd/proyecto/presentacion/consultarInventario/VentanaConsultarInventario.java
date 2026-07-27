package mx.uam.ayd.proyecto.presentacion.consultarInventario;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Ventana para consultar el inventario.
 *
 * @author Yael Mora Simón
 */
@Component
public class VentanaConsultarInventario {

    private Stage stage;
    private ControlConsultarInventario control;
    private boolean initialized = false;

    @FXML
    private TextField textFieldNombre;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, Long> colId;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colTipoProducto;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colExistencia;

    /**
     * Constructor de la ventana de consulta de inventario.
     */
    public VentanaConsultarInventario() {
    }

    /**
     * Asocia el controlador que atenderá los eventos de esta ventana.
     *
     * @param control controlador de la ventana
     */
    public void setControl(ControlConsultarInventario control) {
        this.control = control;
    }

    /**
     * Carga el archivo FXML e inicializa todos los componentes de la ventana.
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/ventana-consultar-inventario.fxml"));

            loader.setController(this);

            Parent root = loader.load();

            stage = new Stage();
            stage.setTitle("Consultar inventario");
            stage.setScene(new Scene(root));

            colId.setCellValueFactory(
                    new PropertyValueFactory<>("idProducto"));

            colNombre.setCellValueFactory(
                    new PropertyValueFactory<>("nombre"));

            colTipoProducto.setCellValueFactory(
                    new PropertyValueFactory<>("tipoProducto"));

            colMarca.setCellValueFactory(
                    new PropertyValueFactory<>("marca"));

            colCategoria.setCellValueFactory(
                    new PropertyValueFactory<>("categoria"));

            colPrecio.setCellValueFactory(
                    new PropertyValueFactory<>("precio"));

            colExistencia.setCellValueFactory(
                    new PropertyValueFactory<>("existenciaActual"));

            initialized = true;

        } catch (IOException e) {
            e.printStackTrace();
            muestraDialogoConMensaje(
                    "No fue posible cargar la ventana de inventario");
        }
    }

    /**
     * Muestra la lista de productos recibida en la tabla.
     *
     * @param productos productos que se mostrarán en pantalla
     */
    public void muestra(Iterable<Producto> productos) {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestra(productos));
            return;
        }

        initializeUI();

        ObservableList<Producto> listaProductos =
                FXCollections.observableArrayList();

        for (Producto producto : productos) {
            listaProductos.add(producto);
        }

        tablaProductos.setItems(listaProductos);
        stage.show();
    }

    /**
     * Muestra únicamente el producto encontrado en la tabla.
     *
     * @param producto producto encontrado
     */
    public void muestraProducto(Producto producto) {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> muestraProducto(producto));
            return;
        }

        initializeUI();

        ObservableList<Producto> listaProductos =
                FXCollections.observableArrayList();

        if (producto != null) {
            listaProductos.add(producto);
        }

        tablaProductos.setItems(listaProductos);
    }

    /**
     * Muestra un mensaje informativo al usuario.
     *
     * @param mensaje texto que se mostrará en el diálogo
     */
    public void muestraDialogoConMensaje(String mensaje) {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(
                    () -> muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Consultar inventario");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra u oculta la ventana.
     *
     * @param visible true para mostrarla, false para ocultarla
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
     * Busca un producto utilizando el nombre escrito por el usuario.
     */
    @FXML
    private void onBuscarProducto() {

        String nombre = textFieldNombre.getText();

        control.buscaProducto(nombre);
    }

    /**
     * Vuelve a cargar todos los productos del inventario.
     */
    @FXML
    private void onMostrarTodos() {
        control.muestraTodos();
    }

    /**
     * Regresa a la pantalla anterior y cierra esta ventana.
     */
    @FXML
    private void onRegresar() {
        control.termina();
    }
}