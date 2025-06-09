package com.fabio.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Clase que gestiona la lógica principal del juego.
 */
public class GestorJuego {
    private Escenario escenario;
    private List<Personaje> ordenTurnos;
    private int turnoActual;
    private boolean juegoTerminado;
    
    /**
     * Constructor de la clase GestorJuego.
     * 
     * @param escenario Escenario del juego.
     */
    public GestorJuego(Escenario escenario) {
        this.escenario = escenario;
        this.ordenTurnos = new ArrayList<>();
        this.turnoActual = 0;
        this.juegoTerminado = false;
        actualizarOrdenTurnos();
    }
    
    /**
     * Actualiza el orden de los turnos basado en la velocidad de los personajes.
     */
    private void actualizarOrdenTurnos() {
        ordenTurnos.clear();
        ordenTurnos.add(escenario.getProtagonista());
        ordenTurnos.addAll(escenario.getEnemigos());
        
        // Ordenar por velocidad (de mayor a menor)
        Collections.sort(ordenTurnos, new Comparator<Personaje>() {
            @Override
            public int compare(Personaje p1, Personaje p2) {
                return Integer.compare(p2.getVelocidad(), p1.getVelocidad());
            }
        });
    }
    
    /**
     * Mueve al protagonista en una dirección.
     * 
     * @param dx Dirección en X (-1, 0, 1).
     * @param dy Dirección en Y (-1, 0, 1).
     * @return true si el movimiento fue exitoso, false en caso contrario.
     */
    public boolean moverProtagonista(int dx, int dy) {
        if (juegoTerminado || !esTurnoProtagonista()) {
            return false;
        }
        
        boolean movimientoExitoso = escenario.getProtagonista().mover(escenario, dx, dy);
        if (movimientoExitoso) {
            siguienteTurno();
        }
        return movimientoExitoso;
    }
    
    /**
     * Ejecuta el turno del enemigo actual.
     */
    public void ejecutarTurnoEnemigo() {
        if (juegoTerminado || esTurnoProtagonista()) {
            return;
        }
        
        Enemigo enemigo = (Enemigo) ordenTurnos.get(turnoActual);
        enemigo.decidirMovimiento(escenario, escenario.getProtagonista());
        siguienteTurno();
    }
    
    /**
     * Avanza al siguiente turno.
     */
    private void siguienteTurno() {
        turnoActual++;
        if (turnoActual >= ordenTurnos.size()) {
            turnoActual = 0;
            actualizarOrdenTurnos();
            
            // Eliminar enemigos muertos
            List<Enemigo> enemigosMuertos = new ArrayList<>();
            for (Enemigo enemigo : escenario.getEnemigos()) {
                if (!enemigo.estaVivo()) {
                    enemigosMuertos.add(enemigo);
                }
            }
            
            for (Enemigo enemigo : enemigosMuertos) {
                escenario.eliminarEnemigo(enemigo);
            }
            
            // Verificar si el juego ha terminado
            if (!escenario.getProtagonista().estaVivo()) {
                juegoTerminado = true;
            } else if (escenario.getEnemigos().isEmpty()) {
                juegoTerminado = true;
            }
        }
    }
    
    // Getters
    public Escenario getEscenario() { return escenario; }
    public List<Personaje> getOrdenTurnos() { return ordenTurnos; }
    public int getTurnoActual() { return turnoActual; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public boolean esTurnoProtagonista() { return turnoActual == 0; }
    public Personaje getPersonajeActual() { return ordenTurnos.get(turnoActual); }
}