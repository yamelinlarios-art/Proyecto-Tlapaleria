package mx.uam.ayd.proyecto.presentacion.actualizarPrecioProductos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;
import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Ventana para la actualización de precios de productos (HU09).
 * 
 * @author Yamelin Larios Nepomuseno
 */
@Component
public class VentanaActualizarPrecio {

    private Stage stage;
    private ControlActualizarPrecio control;
    
    @FXML private TextField txtIdProducto;
    @FXML private Label lblNombreProducto;
    @FXML private Label lblPrecioActual;
    @FXML private TextField txtNuevoPrecio;
    
    private boolean initialized = false;

    /** Constructor básico. */
    public VentanaActualizarPrecio() {}
    
    /** Inicializa los componentes de la interfaz de usuario en el hilo de JavaFX. */
    private void initializeUI() {
        if (initialized) return;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }
        try {
            stage = new Stage();
            stage.setTitle("Actualizar Precio de Producto");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-actualizar-precio.fxml"));
            // Se le asigna este bean como controller de la vista
            loader.setControllerFactory(param -> this);
            
            stage.setScene(new Scene(loader.load()));
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /** Establece el controlador asociado a esta ventana. */
    public void setControl(ControlActualizarPrecio control) {
        this.control = control;
    }
    
    /** Muestra la ventana de actualización de precio y limpia sus campos. */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }
        initializeUI();
        if (txtIdProducto != null) txtIdProducto.setText("");
        if (lblNombreProducto != null) lblNombreProducto.setText("-");
        if (lblPrecioActual != null) lblPrecioActual.setText("-");
        if (txtNuevoPrecio != null) txtNuevoPrecio.setText("");
        stage.show();
    }
    
    /** Muestra un diálogo de información con un mensaje. */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /** Oculta o muestra la ventana de manera segura. */
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
    
    // --- Manejadores de eventos de FXML ---
    
    @FXML
    private void onBuscarProducto() {
        String idText = txtIdProducto.getText();
        if (idText == null || idText.trim().isEmpty()) {
            muestraDialogoConMensaje("Por favor ingresa un ID de producto válido.");
            return;
        }
        try {
            long idProducto = Long.parseLong(idText.trim());
            Producto producto = control.buscarProducto(idProducto);
            if (producto != null) {
                lblNombreProducto.setText(producto.getNombre());
                lblPrecioActual.setText(String.valueOf(producto.getPrecio()));
            } else {
                muestraDialogoConMensaje("No se encontró ningún producto con el ID especificado.");
            }
        } catch (NumberFormatException e) {
            muestraDialogoConMensaje("El ID del producto debe ser un número entero válido.");
        }
    }
    
    @FXML
    private void onActualizarPrecio() {
        String idText = txtIdProducto.getText();
        String precioText = txtNuevoPrecio.getText();
        if (idText == null || idText.trim().isEmpty() || precioText == null || precioText.trim().isEmpty()) {
            muestraDialogoConMensaje("Debes buscar un producto e ingresar el nuevo precio.");
            return;
        }
        try {
            long idProducto = Long.parseLong(idText.trim());
            double nuevoPrecio = Double.parseDouble(precioText.trim());
            if (nuevoPrecio <= 0) {
                muestraDialogoConMensaje("El precio debe ser un número mayor a cero.");
                return;
            }
            
            // Delegamos al control la actualización e inserción en el historial
            Producto productoActualizado = control.actualizarPrecio(idProducto, nuevoPrecio);
            
            if (productoActualizado != null) {
                muestraDialogoConMensaje("El precio del producto '" + productoActualizado.getNombre() + "' fue actualizado exitosamente.");
                lblPrecioActual.setText(String.valueOf(productoActualizado.getPrecio()));
                txtNuevoPrecio.setText("");
            } else {
                muestraDialogoConMensaje("No se pudo actualizar el precio del producto.");
            }
        } catch (NumberFormatException e) {
            muestraDialogoConMensaje("Asegúrate de ingresar valores numéricos válidos en el ID y Nuevo Precio.");
        }
    }
    
    @FXML
    private void handleCancelar() {
        setVisible(false);
    }
}