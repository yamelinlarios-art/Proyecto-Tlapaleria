package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

@Component
public class VentanaDetalleProveedor {

    @Autowired
    private ApplicationContext context;

    public void muestra(ControlProveedor controlPrincipal, Proveedor proveedor) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vistas/ventanaDetalleProveedor.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            
            Parent root = fxmlLoader.load();

            ControlDetalleProveedor controlDetalle = fxmlLoader.getController();
            controlDetalle.inicializarDatos(controlPrincipal, proveedor);

            Stage stage = new Stage();
            stage.setTitle("Detalle de Proveedor - " + proveedor.getNombreCompleto());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}