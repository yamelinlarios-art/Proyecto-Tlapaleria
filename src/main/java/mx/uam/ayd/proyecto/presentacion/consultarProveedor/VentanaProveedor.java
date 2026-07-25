package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Encargada de cargar el FXML y mostrar la ventana en el hilo de JavaFX.
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class VentanaProveedor {

    @Autowired
    private ApplicationContext context;

    private Stage stage;

    /**
     * Carga el archivo FXML e inicia la vista de JavaFX.
     */
    public void muestra(ControlProveedor control) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/presentacion/consultarProveedor/ventana-proveedor.fxml"));
            
            // Le indicamos a JavaFX que use el contenedor de Spring para instanciar el controlador
            loader.setControllerFactory(context::getBean);
            
            Parent root = loader.load();

            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Directorio de Proveedores - La Nueva");
                stage.setScene(new Scene(root));
            }

            // Llamamos al método del control para llenar las tablas
            control.muestra();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}