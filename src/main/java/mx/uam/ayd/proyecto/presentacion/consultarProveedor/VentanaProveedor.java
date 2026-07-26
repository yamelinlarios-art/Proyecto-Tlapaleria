package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class VentanaProveedor {

    @Autowired
    private ApplicationContext context;

    private Stage stage;

    public void muestra(ControlProveedor control) {
        try {
            if (stage == null) {
                // 💡 RUTA CORRECTA: /fxml/ventana-proveedor.fxml
                java.net.URL url = getClass().getResource("/fxml/ventana-proveedor.fxml");
                
                if (url == null) {
                    System.err.println("❌ Ojo: Verifica si el archivo termina en 'ventana-proveedor.fxml' o 'ventana-proveedores.fxml'");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(url);
                loader.setControllerFactory(context::getBean);
                
                Parent root = loader.load();

                stage = new Stage();
                stage.setTitle("Directorio de Proveedores - La Nueva");
                stage.setScene(new Scene(root));
            }

            stage.show();
            stage.toFront();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}