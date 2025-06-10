package com.fabio.controladores;

import com.fabio.modelo.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private AnimationTimer gameLoop;

    public void inicializar(Protagonista protagonista) {
        try {
            this.protagonista = protagonista;
            System.out.println("=== INICIANDO CONTROLADOR DE JUEGO ===");
            System.out.println("Protagonista: " + protagonista.getNombre() + " en (" + 
                             protagonista.getPosX() + "," + protagonista.getPosY() + ")");

            // Crear escenario con rutas correctas
            escenario = new Escenario(
                    "/com/fabio/mapa.txt",
                    "/com/fabio/enemigos.txt");
            
            System.out.println("Escenario creado. Dimensiones: " + escenario.getAncho() + "x" + escenario.getAlto());
            System.out.println("Enemigos en el escenario: " + escenario.getEnemigos().size());
            
            // Mostrar detalles de cada enemigo
            for (int i = 0; i < escenario.getEnemigos().size(); i++) {
                Enemigo enemigo = escenario.getEnemigos().get(i);
                System.out.println("  Enemigo " + (i+1) + ": " + enemigo.getTipo() + 
                                 " en (" + enemigo.getPosX() + "," + enemigo.getPosY() + ")");
            }
            
            escenario.setProtagonista(protagonista);
            System.out.println("Protagonista colocado en el escenario");

            // Inicializar el gestor de juego
            this.gestorJuego = new GestorJuego(escenario);
            System.out.println("Gestor de juego inicializado");

            // Configurar el foco para recibir eventos de teclado
            gridEscenario.setFocusTraversable(true);
            gridEscenario.requestFocus();

            // Configurar la interfaz
            actualizarInterfaz();
            dibujarEscenario();

            // Iniciar el bucle de juego para el turno de enemigos
            iniciarBucleJuego();
            
            System.out.println("=== CONTROLADOR INICIALIZADO CORRECTAMENTE ===");

        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al cargar el escenario o los enemigos: " + e.getMessage());
            System.err.println("Error de E/S: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("Error inesperado: " + e.getMessage());
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    private void iniciarBucleJuego() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 500_000_000) { // 500ms
                    if (!gestorJuego.esTurnoProtagonista() && !gestorJuego.isJuegoTerminado()) {
                        gestorJuego.ejecutarTurnoEnemigo();

                        actualizarInterfaz();
                        dibujarEscenario();

                        if (gestorJuego.isJuegoTerminado()) {
                            // Use Platform.runLater to avoid the showAndWait exception
                            Platform.runLater(() -> mostrarMensajeFinal());
                            stop();
                        }
                    }
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    @FXML
    private void manejarTeclaPresionada(KeyEvent event) {
        if (gestorJuego == null || !gestorJuego.esTurnoProtagonista() || gestorJuego.isJuegoTerminado())
            return;

        boolean accionRealizada = false;
        switch (event.getCode()) {
            case UP:
            case W:
                accionRealizada = gestorJuego.moverProtagonista(0, -1);
                break;
            case DOWN:
            case S:
                accionRealizada = gestorJuego.moverProtagonista(0, 1);
                break;
            case LEFT:
            case A:
                accionRealizada = gestorJuego.moverProtagonista(-1, 0);
                break;
            case RIGHT:
            case D:
                accionRealizada = gestorJuego.moverProtagonista(1, 0);
                break;
            default:
                return;
        }

        if (accionRealizada) {
            actualizarInterfaz();
            dibujarEscenario();

            if (gestorJuego.isJuegoTerminado()) {
                // Use Platform.runLater to avoid potential issues
                Platform.runLater(() -> mostrarMensajeFinal());
                gameLoop.stop();
            }
        }
    }

    private void actualizarInterfaz() {
        if (protagonista == null)
            return;

        lblNombreJugador.setText(protagonista.getNombre());
        lblSaludJugador.setText(String.format("Salud: %d/%d",
                protagonista.getSalud(), protagonista.getSaludMaxima()));
        lblFuerzaJugador.setText("Fuerza: " + protagonista.getAtaque());
        lblDefensaJugador.setText("Defensa: " + protagonista.getDefensa());

        if (escenario != null) {
            lblEnemigosRestantes.setText(String.valueOf(escenario.getEnemigos().size()));
        }

        if (gestorJuego != null) {
            if (gestorJuego.esTurnoProtagonista()) {
                lblTurnoActual.setText("Jugador");
            } else {
                lblTurnoActual.setText("Enemigo");
            }
        }
    }

    private void dibujarEscenario() {
        if (escenario == null)
            return;

        gridEscenario.getChildren().clear();
        
        System.out.println("=== DIBUJANDO ESCENARIO ===");
        int protagonistasEncontrados = 0;
        int enemigosEncontrados = 0;

        for (int y = 0; y < escenario.getAlto(); y++) {
            for (int x = 0; x < escenario.getAncho(); x++) {
                Celda celda = escenario.getCelda(x, y);
                Personaje personaje = escenario.getPersonajeEn(x, y);

                Rectangle rect = new Rectangle(30, 30);
                // Establecer el color del fondo como blanco si hay un personaje,
                // de lo contrario, usar el color basado en la transitabilidad
                rect.setFill(personaje != null ? Color.WHITE : (celda.esTransitable() ? Color.WHITE : Color.GRAY));
                rect.setStroke(Color.BLACK);

                StackPane stackPane = new StackPane();
                stackPane.getChildren().add(rect);

                if (personaje != null) {
                    Text texto = new Text();
                    if (personaje instanceof Protagonista) {
                        texto.setText("P");
                        texto.setFill(Color.BLUE);
                        protagonistasEncontrados++;
                        System.out.println("Protagonista dibujado en (" + x + "," + y + ")");
                    } else if (personaje instanceof Enemigo) {
                        texto.setText("E");
                        texto.setFill(Color.RED);
                        enemigosEncontrados++;
                        Enemigo enemigo = (Enemigo) personaje;
                        System.out.println("Enemigo dibujado: " + enemigo.getTipo() + " en (" + x + "," + y + ")");
                    }
                    stackPane.getChildren().add(texto);
                }

                gridEscenario.add(stackPane, x, y);
            }
        }
        
        System.out.println("Total dibujado - Protagonistas: " + protagonistasEncontrados + 
                         ", Enemigos: " + enemigosEncontrados);
        System.out.println("=== FIN DIBUJO ESCENARIO ===");
    }

    private void mostrarMensajeFinal() {
        String titulo;
        String mensaje;

        if (protagonista != null && protagonista.estaVivo()) {
            titulo = "¡Victoria!";
            mensaje = "¡Felicidades! Has derrotado a todos los enemigos.";
        } else {
            titulo = "¡Derrota!";
            mensaje = "¡Game Over! Has sido derrotado.";
        }

        lblMensaje.setText(mensaje);

        // Mostrar alerta - now safe to call showAndWait
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para limpiar recursos cuando se cierre la ventana
    public void cleanup() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}
