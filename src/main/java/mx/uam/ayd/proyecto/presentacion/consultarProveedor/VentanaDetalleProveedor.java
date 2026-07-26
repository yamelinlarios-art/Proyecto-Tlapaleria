package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

@Component
public class VentanaDetalleProveedor {

    @Autowired
    private ControlDetalleProveedor control;

    private Stage stage;

    // --- ELEMENTOS FXML DE LA INTERFAZ ---
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
    private Button btnCerrar;

    /**
     * Carga el FXML con el nombre correcto en minúsculas y guiones
     */
    public void muestra(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/ventana-detalle-proveedor.fxml"));
            
            // Le decimos a JavaFX que esta clase maneja sus @FXML
            loader.setController(this);

            Parent root = loader.load();

            // Poblamos los campos visuales
            poblarDatos(proveedor);

            stage = new Stage();
            stage.setTitle("Detalles del Proveedor");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void poblarDatos(Proveedor proveedor) {
        if (proveedor != null) {
            lblNombre.setText(proveedor.getNombre() != null ? proveedor.getNombre() : "N/A");
            lblContacto.setText(proveedor.getNombreContacto() != null ? proveedor.getNombreContacto() : "N/A");
            lblTelefono.setText(proveedor.getTelefono() != null ? proveedor.getTelefono() : "N/A");
            lblCorreo.setText(proveedor.getCorreo() != null ? proveedor.getCorreo() : "N/A");
            lblDireccion.setText(proveedor.getDireccion() != null ? proveedor.getDireccion() : "N/A");
            lblTotalAdeudado.setText(String.format("$%.2f", proveedor.getTotalAdeudado()));
        }
    }

    @FXML
    private void handleCerrar() {
        control.cerrarVentana();
    }

    public void cierra() {
        if (stage != null) {
            stage.close();
        }
    }
}