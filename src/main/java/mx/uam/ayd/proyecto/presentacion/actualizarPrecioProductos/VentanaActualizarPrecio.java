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

    public VentanaActualizarPrecio() {}

    private void initializeUI() {
        if (stage != null) return; // Si ya fue creada, no se vuelve a cargar

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-actualizar-precio.fxml"));
            
            // Asignamos manualmente este bean como el controlador del FXML
            loader.setController(this);
            
            stage = new Stage();
            stage.setTitle("Actualizar Precio de Producto");
            stage.setScene(new Scene(loader.load()));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setControl(ControlActualizarPrecio control) {
        this.control = control;
    }
    
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }
        
        initializeUI();
        limpiarCampos();
        
        if (stage != null) {
            stage.show();
        }
    }
    
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
    
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }
        if (stage == null && visible) initializeUI();
        if (stage != null) {
            if (visible) stage.show();
            else stage.hide();
        }
    }

    private void limpiarCampos() {
        if (txtIdProducto != null) txtIdProducto.setText("");
        if (lblNombreProducto != null) lblNombreProducto.setText("-");
        if (lblPrecioActual != null) lblPrecioActual.setText("-");
        if (txtNuevoPrecio != null) txtNuevoPrecio.setText("");
    }
    
    // --- Manejadores de eventos FXML ---
    
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
                lblPrecioActual.setText(String.format("$%.2f", producto.getPrecio()));
            } else {
                lblNombreProducto.setText("-");
                lblPrecioActual.setText("-");
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
            
            Producto productoActualizado = control.actualizarPrecio(idProducto, nuevoPrecio);
            if (productoActualizado != null) {
                muestraDialogoConMensaje("El precio del producto '" + productoActualizado.getNombre() + "' fue actualizado exitosamente.");
                lblPrecioActual.setText(String.format("$%.2f", productoActualizado.getPrecio()));
                txtNuevoPrecio.setText("");
            } else {
                muestraDialogoConMensaje("No se pudo actualizar el precio del producto.");
            }
        } catch (NumberFormatException e) {
            muestraDialogoConMensaje("Asegúrate de ingresar valores numéricos válidos en el ID y Nuevo Precio.");
        } catch (IllegalArgumentException e) {
            muestraDialogoConMensaje(e.getMessage());
        } catch (Exception e) {
            muestraError("Ocurrió un error inesperado al actualizar el precio.");
        }
    }
    
    @FXML
    private void handleCancelar() {
        setVisible(false);
    }
}