package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private ControlDetalleProveedor control;

    @Autowired
    @Lazy
    private ControlProveedor controlProveedor;

    private Stage stage;

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

    public void muestra(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-detalle-proveedor.fxml"));
            
            // Asignamos el controlador dinámicamente
            loader.setController(this);

            Parent root = loader.load();

            poblarDatos(proveedor);

            stage = new Stage();
            stage.setTitle("Detalles del Proveedor");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void poblarDatos(Proveedor proveedor) {
        if (proveedor != null) {
            lblNombre.setText(proveedor.getNombreCompleto() != null ? proveedor.getNombreCompleto() : "N/A");
            lblContacto.setText(proveedor.getCorporativo() != null ? proveedor.getCorporativo() : "N/A");
            lblTelefono.setText(proveedor.getTelefono() != null ? proveedor.getTelefono() : "N/A");
            lblCorreo.setText(proveedor.getTipoProveedor() != null ? proveedor.getTipoProveedor() : "N/A");
            lblDireccion.setText(String.valueOf(proveedor.getIdProveedor()));
            
            double saldo = controlProveedor.obtenerSaldoProveedor(proveedor.getIdProveedor());
            lblTotalAdeudado.setText(String.format("$%.2f", saldo));
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