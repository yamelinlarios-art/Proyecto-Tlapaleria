package mx.uam.ayd.proyecto.presentacion.devolucionProducto;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;
import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Ventana para la devolución de productos dañados (HU-10).
 * 
 * @author Yamelin Larios Nepomuseno
 */
@Component
public class VentanaDevolucionProducto {

    private Stage stage;
    private ControlDevolucionProducto control;
    private boolean initialized = false;

    // Componentes enlazados con el FXML
    @FXML private TextField txtIdProducto;
    @FXML private Label lblNombreProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtMotivo;
    @FXML private Label lblMensaje;

    /** Constructor sin inicialización de UI para evitar conflictos en hilos de JavaFX. */
    public VentanaDevolucionProducto() {}

    /** Inicializa los componentes de la interfaz de usuario en el hilo de JavaFX. */
    private void initializeUI() {
        if (initialized) return;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        try {
            stage = new Stage();
            stage.setTitle("Devolución de Producto Dañado");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-devolucion-producto.fxml"));
            loader.setController(this);
            // Carga la escena adaptada a las dimensiones del FXML
            stage.setScene(new Scene(loader.load()));
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Establece el controlador asociado a esta ventana. */
    public void setControl(ControlDevolucionProducto control) {
        this.control = control;
    }

    /** Muestra la ventana y resetea los campos. */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }
        initializeUI();
        limpiarCampos();
        stage.show();
    }

    /** Muestra en la interfaz los datos del producto encontrado. */
    public void muestraProductoEncontrado(Producto producto) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraProductoEncontrado(producto));
            return;
        }
        if (producto != null) {
            if (lblNombreProducto != null) lblNombreProducto.setText(producto.getNombre());
            if (lblMensaje != null) lblMensaje.setText("");
        } else {
            muestraError("No se encontró ningún producto con el ID especificado.");
            if (lblNombreProducto != null) lblNombreProducto.setText("-");
        }
    }

    /** Notifica un resultado exitoso al usuario y limpia el formulario. */
    public void muestraDevolucionExitosa(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDevolucionExitosa(mensaje));
            return;
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
        limpiarCampos();
    }

    /** Muestra una alerta de error al usuario. */
    public void muestraError(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraError(mensaje));
            return;
        }
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /** Oculta o muestra la ventana. */
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }
        if (!initialized && visible) initializeUI();
        if (stage != null) {
            if (visible) stage.show();
            else stage.hide();
        }
    }

    private void limpiarCampos() {
        if (txtIdProducto != null) txtIdProducto.setText("");
        if (lblNombreProducto != null) lblNombreProducto.setText("-");
        if (txtCantidad != null) txtCantidad.setText("");
        if (txtMotivo != null) txtMotivo.setText("");
        if (lblMensaje != null) lblMensaje.setText("");
    }

    // --- Manejadores de eventos FXML (Coinciden con onAction del FXML) ---

    @FXML
    private void accionBuscarProducto() {
        String idText = txtIdProducto.getText();
        if (idText == null || idText.trim().isEmpty()) {
            muestraError("Ingresa un ID de producto válido.");
            return;
        }
        try {
            long idProducto = Long.parseLong(idText.trim());
            control.buscarProducto(idProducto);
        } catch (NumberFormatException e) {
            muestraError("El ID del producto debe ser un número entero.");
        }
    }

    @FXML
    private void accionRegistrarDevolucion() {
        String idText = txtIdProducto.getText();
        String cantText = txtCantidad.getText();
        String motivo = txtMotivo != null ? txtMotivo.getText() : "";

        if (idText == null || idText.trim().isEmpty() || cantText == null || cantText.trim().isEmpty()) {
            muestraError("Por favor completa los campos de ID y Cantidad.");
            return;
        }

        try {
            long idProducto = Long.parseLong(idText.trim());
            int cantidad = Integer.parseInt(cantText.trim());

            if (cantidad <= 0) {
                muestraError("La cantidad a devolver debe ser mayor a 0.");
                return;
            }

            control.registrarDevolucion(idProducto, cantidad, motivo);

        } catch (NumberFormatException e) {
            muestraError("Asegúrate de ingresar números válidos en los campos de ID y Cantidad.");
        }
    }

    @FXML
    private void accionCancelar() {
        setVisible(false);
    }
}