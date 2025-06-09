package com.fabio.controladores;

import com.fabio.modelo.Protagonista;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorCreacionPersonaje {
    @FXML private TextField tfNombre;
    @FXML private Spinner<Integer> spSalud;
    @FXML private Spinner<Integer> spFuerza;
    @FXML private Spinner<Integer> spDefensa;
    @FXML private Spinner<Integer> spVelocidad;

    @FXML
    private void comenzarJuego() {
        try {
            // Crear el protagonista con los datos ingresados
            String nombre = tfNombre.getText().trim();
            if (nombre.isEmpty()) {
                nombre = "Héroe Anónimo";
            }
            
            int salud = spSalud.getValue();
            int fuerza = spFuerza.getValue();
            int defensa = spDefensa.getValue();
            int velocidad = spVelocidad.getValue();
            
            Protagonista protagonista = new Protagonista(
                nombre, salud, fuerza, defensa, velocidad, 5, 1, 1
            );
            
            // Cargar la vista del juego usando la misma ruta que App.java
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fabio/juego.fxml"));
            Parent root = loader.load();
            
            // Configurar el controlador del juego si existe
            ControladorJuego controladorJuego = loader.getController();
            if (controladorJuego != null) {
                controladorJuego.inicializar(protagonista);
            }
            
            // Mostrar la nueva escena
            Stage stage = (Stage) tfNombre.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Juego de Mazmorras");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar mensaje de error al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar el juego");
            alert.setContentText("No se pudo cargar la vista del juego: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error inesperado");
            alert.setContentText("Ocurrió un error inesperado: " + e.getMessage());
            alert.showAndWait();
        }
    }
}