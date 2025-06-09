package com.fabio.controladores;

import com.fabio.modelo.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;

public class ControladorJuego {
    @FXML
    private GridPane gridEscenario;
    @FXML
    private Label lblNombreJugador;
    @FXML
    private Label lblSaludJugador;
    @FXML
    private Label lblFuerzaJugador;
    @FXML
    private Label lblDefensaJugador;
    @FXML
    private Label lblExperienciaJugador;
    @FXML
    private Label lblTurnoActual;
    @FXML
    private Label lblEnemigosRestantes;
    @FXML
    private Label lblMensaje;

    private GestorJuego gestorJuego;
    private Escenario escenario;
    private Protagonista protagonista;

    public void inicializar(Protagonista protagonista) {
        try {
            this.protagonista = protagonista;
            // Cambiar las rutas para que coincidan con la estructura del proyecto
            escenario = new Escenario(
                    "/com/fabio/mapa.txt",
                    "/com/fabio/enemigos.txt");
            escenario.setProtagonista(protagonista);

            // Inicializar el gestor de juego que faltaba
            this.gestorJuego = new GestorJuego(escenario);

            // Configurar la interfaz
            actualizarInterfaz();
            dibujarEscenario();

            // Resto del código permanece igual...
        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al cargar el escenario o los enemigos");
        }
    }

    @FXML
    private void manejarTeclaPresionada(KeyEvent event) {
        if (gestorJuego == null || !gestorJuego.esTurnoProtagonista())
            return;

        boolean movimientoExitoso = false;

        switch (event.getCode()) {
            case UP:
                movimientoExitoso = gestorJuego.moverProtagonista(0, -1);
                break;
            case DOWN:
                movimientoExitoso = gestorJuego.moverProtagonista(0, 1);
                break;
            case LEFT:
                movimientoExitoso = gestorJuego.moverProtagonista(-1, 0);
                break;
            case RIGHT:
                movimientoExitoso = gestorJuego.moverProtagonista(1, 0);
                break;
            default:
                return;
        }

        if (movimientoExitoso) {
            actualizarInterfaz();
            dibujarEscenario();
        }
    }

    private void actualizarInterfaz() {
        lblNombreJugador.setText(protagonista.getNombre());
        lblSaludJugador.setText(String.format("Salud: %d/%d",
                protagonista.getSalud(), protagonista.getSaludMaxima()));
        lblFuerzaJugador.setText("Fuerza: " + protagonista.getAtaque());
        lblDefensaJugador.setText("Defensa: " + protagonista.getDefensa());
        lblEnemigosRestantes.setText(String.valueOf(escenario.getEnemigos().size()));

        if (gestorJuego.esTurnoProtagonista()) {
            lblTurnoActual.setText("Jugador");
        } else {
            lblTurnoActual.setText("Enemigo");
        }
    }

    private void dibujarEscenario() {
        gridEscenario.getChildren().clear();

        for (int y = 0; y < escenario.getAlto(); y++) {
            for (int x = 0; x < escenario.getAncho(); x++) {
                Celda celda = escenario.getCelda(x, y);
                Personaje personaje = escenario.getPersonajeEn(x, y);

                Rectangle rect = new Rectangle(30, 30);
                rect.setFill(celda.esTransitable() ? Color.WHITE : Color.GRAY);
                rect.setStroke(Color.BLACK);

                StackPane stackPane = new StackPane();
                stackPane.getChildren().add(rect);

                if (personaje != null) {
                    Text texto = new Text(personaje instanceof Protagonista ? "P" : "E");
                    texto.setFill(personaje instanceof Protagonista ? Color.BLUE : Color.RED);
                    stackPane.getChildren().add(texto);
                }

                gridEscenario.add(stackPane, x, y);
            }
        }
    }

    private void mostrarMensajeFinal() {
        if (protagonista.estaVivo()) {
            lblMensaje.setText("¡Felicidades! Has derrotado a todos los enemigos.");
        } else {
            lblMensaje.setText("¡Game Over! Has sido derrotado.");
        }
    }
}