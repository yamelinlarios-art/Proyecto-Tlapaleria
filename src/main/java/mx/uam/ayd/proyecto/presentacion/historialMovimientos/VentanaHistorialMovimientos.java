package mx.uam.ayd.proyecto.presentacion.historialMovimientos;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;

/**
 * Ventana para consultar el historial de movimientos de inventario.
 *
 * @author Yael Mora Simón
 */
@Component
public class VentanaHistorialMovimientos {

    private Stage escenario;